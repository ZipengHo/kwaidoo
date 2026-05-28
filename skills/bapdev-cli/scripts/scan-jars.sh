#!/usr/bin/env bash

set -euo pipefail

# scan-jars.sh - 扫描工程内的 jar 包，管理分析状态
# 
# 用法：
#   scan-jars.sh --list              # 列出所有 jar（含哈希和状态）
#   scan-jars.sh --pending           # 只输出需要分析的 jar
#   scan-jars.sh --analyze <jar>     # 分析单个 jar，输出信息供 AI 使用
#   scan-jars.sh --mark-done <jar>   # 标记 jar 已完成分析
#   scan-jars.sh --refresh           # 刷新黑名单后重新扫描
#   scan-jars.sh --rescan            # 全量重新扫描（清除状态）
#   scan-jars.sh --blacklist add <pattern>    # 添加黑名单
#   scan-jars.sh --blacklist remove <pattern> # 移除黑名单
#   scan-jars.sh --status            # 显示扫描状态摘要
#   scan-jars.sh --init              # 初始化配置文件

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/common.sh"
source "${SCRIPT_DIR}/lib/jar-utils.sh"

# ============================================
# 参数解析
# ============================================

usage() {
  cat << 'EOF'
用法: scan-jars.sh <子命令> [选项]

子命令:
  --list              列出所有 jar 包及其哈希和分析状态
  --pending           只输出需要分析的 jar（JSON 格式）
  --analyze <jar>     输出单个 jar 的详细信息（供 AI 分析）
  --mark-done <jar>   标记指定 jar 已完成分析
  --refresh           刷新扫描状态（应用黑名单变更）
  --rescan            全量重新扫描（清除所有状态）
  --status            显示扫描状态摘要
  --init              初始化配置文件（黑名单模板）
  --blacklist add <pattern>    添加黑名单模式
  --blacklist remove <pattern> 移除黑名单模式
  --help              显示帮助信息

选项:
  --project-root <dir>  指定项目根目录
  --output <dir>        指定输出目录（默认 .bapdev-cli/jars）
  --exclude <dirs>      排除目录（逗号分隔）

示例:
  # 列出所有 jar
  scan-jars.sh --list
  
  # 获取需要分析的 jar 列表
  scan-jars.sh --pending
  
  # 分析单个 jar
  scan-jars.sh --analyze lib/example.jar
  
  # 初始化配置
  scan-jars.sh --init
EOF
}

COMMAND=""
JAR_PATH=""
BLACKLIST_PATTERN=""
PROJECT_ROOT="${BAPDEV_PROJECT_ROOT:-$PWD}"
OUTPUT_DIR=""
EXCLUDE_DIRS=".git,.bapdev-cli,.opencode,node_modules,target,build"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --list|--pending|--refresh|--rescan|--status|--init|--help|-h)
      COMMAND="$1"
      shift
      ;;
    --analyze|--mark-done)
      COMMAND="$1"
      shift
      if [[ $# -gt 0 && ! "$1" =~ ^-- ]]; then
        JAR_PATH="$1"
        shift
      fi
      ;;
    --blacklist)
      COMMAND="$1"
      shift
      if [[ $# -gt 0 ]]; then
        case "$1" in
          add|remove)
            COMMAND="blacklist-$1"
            shift
            if [[ $# -gt 0 && ! "$1" =~ ^-- ]]; then
              BLACKLIST_PATTERN="$1"
              shift
            fi
            ;;
          *)
            bapdev_die "黑名单子命令应为 add 或 remove" 2
            ;;
        esac
      fi
      ;;
    --project-root)
      shift
      if [[ $# -gt 0 ]]; then
        PROJECT_ROOT="$1"
        shift
      fi
      ;;
    --output)
      shift
      if [[ $# -gt 0 ]]; then
        OUTPUT_DIR="$1"
        shift
      fi
      ;;
    --exclude)
      shift
      if [[ $# -gt 0 ]]; then
        EXCLUDE_DIRS="$1"
        shift
      fi
      ;;
    *)
      bapdev_die "未知参数: $1" 2
      ;;
  esac
done

# 设置默认输出目录
if [[ -z "${OUTPUT_DIR}" ]]; then
  OUTPUT_DIR="$(jar_output_dir "${PROJECT_ROOT}")"
fi

# 加载项目环境
bapdev_load_secure_env "${PROJECT_ROOT}"
export BAPDEV_PROJECT_ROOT="${PROJECT_ROOT}"

# ============================================
# 子命令实现
# ============================================

cmd_list() {
  local blacklist_file state_file state_json patterns
  
  blacklist_file="$(jar_blacklist_file "${PROJECT_ROOT}")"
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  state_json="$(jar_read_state "${state_file}")"
  patterns="$(jar_load_blacklist_patterns "${blacklist_file}")"
  
  local jars_json="["
  local first=true
  
  while IFS= read -r jar_path; do
    [[ -f "${jar_path}" ]] || continue
    
    local rel_path jar_hash jar_size needs_analysis="true"
    rel_path="$(jar_normalize_path "${jar_path}" "${PROJECT_ROOT}")"
    
    # 检查黑名单
    if jar_match_blacklist "${jar_path}" "${patterns}"; then
      continue
    fi
    
    jar_hash="$(jar_sha256 "${jar_path}")"
    jar_size="$(stat -c%s "${jar_path}" 2>/dev/null || stat -f%z "${jar_path}" 2>/dev/null)"
    
    # 检查是否需要分析
    if ! jar_needs_analysis "${jar_path}" "${jar_hash}" "${state_json}"; then
      needs_analysis="false"
    fi
    
    if [[ "${first}" == "true" ]]; then
      first=false
    else
      jars_json="${jars_json},"
    fi
    
    jars_json="${jars_json}$(jar_output_jar_info "${jar_path}" "${jar_hash}" "${jar_size}" "${needs_analysis}")"
  done < <(jar_find_all_jars "${PROJECT_ROOT}" "${EXCLUDE_DIRS}")
  
  jars_json="${jars_json}]"
  
  jar_output_jar_list "${jars_json}" | jar_json_tool '.'
}

cmd_pending() {
  local blacklist_file state_file state_json patterns
  
  blacklist_file="$(jar_blacklist_file "${PROJECT_ROOT}")"
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  state_json="$(jar_read_state "${state_file}")"
  patterns="$(jar_load_blacklist_patterns "${blacklist_file}")"
  
  local jars_json="["
  local first=true
  local pending_count=0
  
  while IFS= read -r jar_path; do
    [[ -f "${jar_path}" ]] || continue
    
    local rel_path jar_hash jar_size
    
    # 检查黑名单
    if jar_match_blacklist "${jar_path}" "${patterns}"; then
      continue
    fi
    
    jar_hash="$(jar_sha256 "${jar_path}")"
    jar_size="$(stat -c%s "${jar_path}" 2>/dev/null || stat -f%z "${jar_path}" 2>/dev/null)"
    
    # 只输出需要分析的 jar
    if jar_needs_analysis "${jar_path}" "${jar_hash}" "${state_json}"; then
      if [[ "${first}" == "true" ]]; then
        first=false
      else
        jars_json="${jars_json},"
      fi
      
      jars_json="${jars_json}$(jar_output_jar_info "${jar_path}" "${jar_hash}" "${jar_size}" "true")"
      pending_count=$((pending_count + 1))
    fi
  done < <(jar_find_all_jars "${PROJECT_ROOT}" "${EXCLUDE_DIRS}")
  
  jars_json="${jars_json}]"
  
  # 输出结果
  printf '{"pendingCount":%d,"jars":%s,"outputDir":"%s"}\n' \
    "${pending_count}" "${jars_json}" "${OUTPUT_DIR}"
}

cmd_analyze() {
  [[ -n "${JAR_PATH}" ]] || bapdev_die "请指定要分析的 jar 文件路径" 2
  [[ -f "${JAR_PATH}" ]] || bapdev_die "jar 文件不存在: ${JAR_PATH}" 2
  
  local jar_hash jar_size rel_path overview_file index_file
  
  jar_hash="$(jar_sha256 "${JAR_PATH}")"
  jar_size="$(stat -c%s "${JAR_PATH}" 2>/dev/null || stat -f%z "${JAR_PATH}" 2>/dev/null)"
  rel_path="$(jar_normalize_path "${JAR_PATH}" "${PROJECT_ROOT}")"
  overview_file="${OUTPUT_DIR}/$(jar_overview_file_name "${JAR_PATH}")"
  index_file="${OUTPUT_DIR}/$(jar_index_file_name "${JAR_PATH}")"
  
  # 输出分析所需的元数据（供 AI 使用）
  cat << EOF
{
  "jarPath": "${JAR_PATH}",
  "relativePath": "${rel_path}",
  "jarName": "$(basename -- "${JAR_PATH}")",
  "hash": "sha256:${jar_hash}",
  "size": ${jar_size},
  "outputDir": "${OUTPUT_DIR}",
  "overviewFile": "${overview_file}",
  "indexFile": "${index_file}",
  "analyzeCommand": "请分析该 jar 包并生成以下文件:",
  "instructions": [
    "1. 解压 jar 包，获取其中的 .class 文件列表",
    "2. 分析 jar 包的用途、主要功能和包结构",
    "3. 生成 overviewFile 内容：jar 包总览（用途、包结构、核心类概述）",
    "4. 生成 indexFile 内容：详细类索引（包名、类名、主要方法签名）",
    "5. 更新 jars-state.json 记录分析完成状态"
  ]
}
EOF
}

cmd_mark_done() {
  [[ -n "${JAR_PATH}" ]] || bapdev_die "请指定要标记的 jar 文件路径" 2
  [[ -f "${JAR_PATH}" ]] || bapdev_die "jar 文件不存在: ${JAR_PATH}" 2
  
  local state_file jar_hash jar_size rel_path overview_file index_file
  
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  jar_hash="$(jar_sha256 "${JAR_PATH}")"
  jar_size="$(stat -c%s "${JAR_PATH}" 2>/dev/null || stat -f%z "${JAR_PATH}")"
  rel_path="$(jar_normalize_path "${JAR_PATH}" "${PROJECT_ROOT}")"
  overview_file="${OUTPUT_DIR}/$(jar_overview_file_name "${JAR_PATH}")"
  index_file="${OUTPUT_DIR}/$(jar_index_file_name "${JAR_PATH}")"
  
  # 确保输出目录存在
  mkdir -p "${OUTPUT_DIR}"
  
  # 更新状态文件
  jar_update_jar_state "${state_file}" "${rel_path}" "${jar_hash}" "${jar_size}" "${overview_file}" "${index_file}"
  
  printf '{"success":true,"jar":"%s","stateFile":"%s"}\n' "${rel_path}" "${state_file}"
}

cmd_refresh() {
  local state_file blacklist_file patterns state_json
  
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  blacklist_file="$(jar_blacklist_file "${PROJECT_ROOT}")"
  patterns="$(jar_load_blacklist_patterns "${blacklist_file}")"
  state_json="$(jar_read_state "${state_file}")"
  
  # 移除黑名单中的 jar 状态
  local removed_count=0
  
  if command -v jq >/dev/null 2>&1; then
    local jars_in_state
    jars_in_state=$(jq -r '.jars | keys[]' <<< "${state_json}" 2>/dev/null)
    
    while IFS= read -r jar_rel_path; do
      [[ -z "${jar_rel_path}" ]] && continue
      
      local jar_abs_path="${PROJECT_ROOT}/${jar_rel_path}"
      if jar_match_blacklist "${jar_abs_path}" "${patterns}"; then
        jar_remove_jar_state "${state_file}" "${jar_rel_path}"
        removed_count=$((removed_count + 1))
        bapdev_stderr "已移除黑名单 jar 状态: ${jar_rel_path}"
      fi
    done <<< "${jars_in_state}"
  fi
  
  printf '{"success":true,"removedCount":%d}\n' "${removed_count}"
}

cmd_rescan() {
  local state_file
  
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  
  # 清除状态文件
  if [[ -f "${state_file}" ]]; then
    rm -f "${state_file}"
    bapdev_stderr "已清除扫描状态: ${state_file}"
  fi
  
  # 输出空的初始状态
  printf '{"success":true,"message":"状态已清除，下次扫描将全量分析"}\n'
}

cmd_status() {
  local state_file state_json blacklist_file total_jars analyzed_jars pending_jars
  
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  state_json="$(jar_read_state "${state_file}")"
  blacklist_file="$(jar_blacklist_file "${PROJECT_ROOT}")"
  
  # 统计信息
  if command -v jq >/dev/null 2>&1; then
    analyzed_jars=$(jq '.jars | length' <<< "${state_json}" 2>/dev/null || echo "0")
    last_scan=$(jq -r '.lastScan' <<< "${state_json}" 2>/dev/null || echo "")
  else
    analyzed_jars="?"
    last_scan="?"
  fi
  
  # 统计总 jar 数量
  local patterns
  patterns="$(jar_load_blacklist_patterns "${blacklist_file}")"
  total_jars=0
  
  while IFS= read -r jar_path; do
    [[ -f "${jar_path}" ]] || continue
    if ! jar_match_blacklist "${jar_path}" "${patterns}"; then
      total_jars=$((total_jars + 1))
    fi
  done < <(jar_find_all_jars "${PROJECT_ROOT}" "${EXCLUDE_DIRS}")
  
  pending_jars=$((total_jars - analyzed_jars))
  
  printf '{"totalJars":%d,"analyzedJars":%d,"pendingJars":%d,"lastScan":"%s","stateFile":"%s","blacklistFile":"%s","outputDir":"%s"}\n' \
    "${total_jars}" "${analyzed_jars}" "${pending_jars}" "${last_scan}" "${state_file}" "${blacklist_file}" "${OUTPUT_DIR}"
}

cmd_init() {
  local blacklist_file output_dir
  
  # 初始化黑名单文件
  blacklist_file="$(jar_init_blacklist "${PROJECT_ROOT}")"
  
  # 创建输出目录
  output_dir="$(jar_output_dir "${PROJECT_ROOT}")"
  mkdir -p "${output_dir}"
  
  # 创建初始状态文件
  local state_file
  state_file="$(jar_state_file "${PROJECT_ROOT}")"
  if [[ ! -f "${state_file}" ]]; then
    jar_write_state "${state_file}" '{"version":"1.0","lastScan":"","projectRoot":"","jars":{}}'
  fi
  
  printf '{"success":true,"blacklistFile":"%s","outputDir":"%s","stateFile":"%s"}\n' \
    "${blacklist_file}" "${output_dir}" "${state_file}"
}

cmd_blacklist_add() {
  [[ -n "${BLACKLIST_PATTERN}" ]] || bapdev_die "请指定要添加的黑名单模式" 2
  
  local blacklist_file
  blacklist_file="$(jar_blacklist_file "${PROJECT_ROOT}")"
  
  # 确保文件存在
  if [[ ! -f "${blacklist_file}" ]]; then
    jar_init_blacklist "${PROJECT_ROOT}" >/dev/null
  fi
  
  # 检查是否已存在
  if grep -qF "${BLACKLIST_PATTERN}" "${blacklist_file}" 2>/dev/null; then
    printf '{"success":false,"message":"模式已存在"}\n'
    return 0
  fi
  
  # 添加模式
  printf '%s\n' "${BLACKLIST_PATTERN}" >> "${blacklist_file}"
  printf '{"success":true,"pattern":"%s","file":"%s"}\n' "${BLACKLIST_PATTERN}" "${blacklist_file}"
}

cmd_blacklist_remove() {
  [[ -n "${BLACKLIST_PATTERN}" ]] || bapdev_die "请指定要移除的黑名单模式" 2
  
  local blacklist_file
  blacklist_file="$(jar_blacklist_file "${PROJECT_ROOT}")"
  
  if [[ ! -f "${blacklist_file}" ]]; then
    printf '{"success":false,"message":"黑名单文件不存在"}\n'
    return 0
  fi
  
  # 检查是否存在
  if ! grep -qF "${BLACKLIST_PATTERN}" "${blacklist_file}" 2>/dev/null; then
    printf '{"success":false,"message":"模式不存在"}\n'
    return 0
  fi
  
  # 移除模式
  grep -vF "${BLACKLIST_PATTERN}" "${blacklist_file}" > "${blacklist_file}.tmp"
  mv "${blacklist_file}.tmp" "${blacklist_file}"
  
  printf '{"success":true,"pattern":"%s","file":"%s"}\n' "${BLACKLIST_PATTERN}" "${blacklist_file}"
}

# ============================================
# 主入口
# ============================================

case "${COMMAND}" in
  ""|--help|-h)
    usage
    ;;
  --list)
    cmd_list
    ;;
  --pending)
    cmd_pending
    ;;
  --analyze)
    cmd_analyze
    ;;
  --mark-done)
    cmd_mark_done
    ;;
  --refresh)
    cmd_refresh
    ;;
  --rescan)
    cmd_rescan
    ;;
  --status)
    cmd_status
    ;;
  --init)
    cmd_init
    ;;
  blacklist-add)
    cmd_blacklist_add
    ;;
  blacklist-remove)
    cmd_blacklist_remove
    ;;
  *)
    bapdev_die "未知命令: ${COMMAND}" 2
    ;;
esac