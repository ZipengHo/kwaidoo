#!/usr/bin/env bash

set -euo pipefail

# jar-utils.sh - jar 包扫描与分析的公共函数库
# 用于 scan-jars.sh 命令的辅助功能

JAR_UTILS_LIB_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
JAR_UTILS_SCRIPTS_DIR="$(cd -- "${JAR_UTILS_LIB_DIR}/.." && pwd)"
JAR_UTILS_SKILL_DIR="$(cd -- "${JAR_UTILS_SCRIPTS_DIR}/.." && pwd)"

source "${JAR_UTILS_LIB_DIR}/common.sh"

# 状态文件相关常量
JAR_STATE_FILE_NAME="jars-state.json"
JAR_BLACKLIST_FILE_NAME="jars-blacklist.txt"
JAR_OUTPUT_DIR_NAME="jars"

# ============================================
# 哈希计算
# ============================================

jar_sha256() {
  local file="$1"
  [[ -f "${file}" ]] || return 1
  
  if command -v sha256sum >/dev/null 2>&1; then
    sha256sum "${file}" | awk '{print $1}'
    return 0
  fi
  
  if command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "${file}" | awk '{print $1}'
    return 0
  fi
  
  # 使用 openssl 作为备选
  if command -v openssl >/dev/null 2>&1; then
    openssl dgst -sha256 "${file}" | awk '{print $NF}'
    return 0
  fi
  
  bapdev_die "无法计算 SHA256 哈希，请安装 sha256sum、shasum 或 openssl" 2
}

# ============================================
# 黑名单处理
# ============================================

jar_blacklist_file() {
  local project_root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local state_dir
  state_dir="$(bapdev_state_dir "${project_root}")"
  printf '%s/%s\n' "${state_dir}" "${JAR_BLACKLIST_FILE_NAME}"
}

jar_blacklist_template() {
  printf '%s/references/jars-blacklist.template.txt\n' "${JAR_UTILS_SKILL_DIR}"
}

jar_load_blacklist_patterns() {
  local blacklist_file="$1"
  local patterns=()
  
  if [[ -f "${blacklist_file}" ]]; then
    local line
    while IFS= read -r line || [[ -n "${line}" ]]; do
      # 跳过空行和注释
      [[ -z "${line}" ]] && continue
      [[ "${line}" =~ ^[[:space:]]*# ]] && continue
      # 去除前后空白
      line="${line#"${line%%[![:space:]]*}"}"
      line="${line%"${line##*[![:space:]]}"}"
      [[ -n "${line}" ]] && patterns+=("${line}")
    done < "${blacklist_file}"
  fi
  
  printf '%s\n' "${patterns[@]}"
}

jar_match_blacklist() {
  local jar_path="$1"
  local patterns="$2"  # 换行分隔的模式字符串
  
  # 获取 jar 文件名（不含路径）
  local jar_name
  jar_name="$(basename -- "${jar_path}")"
  
  # 获取相对于项目根目录的路径
  local rel_path="${jar_path}"
  if [[ -n "${BAPDEV_PROJECT_ROOT:-}" ]]; then
    rel_path="${jar_path#${BAPDEV_PROJECT_ROOT}/}"
  fi
  
  local pattern
  while IFS= read -r pattern; do
    [[ -z "${pattern}" ]] && continue
    
    # 检查完整路径匹配
    if [[ "${rel_path}" == "${pattern}" ]]; then
      return 0
    fi
    
    # 检查文件名 glob 匹配
    if [[ "${jar_name}" == ${pattern} ]]; then
      return 0
    fi
    
    # 检查相对路径 glob 匹配
    if [[ "${rel_path}" == ${pattern} ]]; then
      return 0
    fi
  done <<< "${patterns}"
  
  return 1
}

jar_init_blacklist() {
  local project_root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local blacklist_file
  blacklist_file="$(jar_blacklist_file "${project_root}")"
  
  if [[ ! -f "${blacklist_file}" ]]; then
    local template_file
    template_file="$(jar_blacklist_template)"
    
    mkdir -p "$(dirname -- "${blacklist_file}")"
    
    if [[ -f "${template_file}" ]]; then
      cp "${template_file}" "${blacklist_file}"
      bapdev_stderr "已创建黑名单配置文件：${blacklist_file}"
    else
      # 创建空的黑名单文件，带说明注释
      cat > "${blacklist_file}" << 'EOF'
# jar 包黑名单配置
# 在此文件中列出不需要分析的 jar 包
# 支持以下格式：
#   - 完整文件名：example.jar
#   - glob 模式：*-sources.jar, test-*.jar
#   - 相对路径：lib/test/helper.jar
#   - 路径 glob：lib/test/*.jar

# 常见测试依赖（建议保留）
# junit-*.jar
# hamcrest-*.jar
# mockito-*.jar

# 源码和文档包（建议保留）
*-sources.jar
*-javadoc.jar

# 平台核心包（通常不需要分析，已由平台文档覆盖）
# 如果需要分析特定平台包，可从下方移除对应行
# tcmcat-bap.jar
# tcmcat-cdao.jar
EOF
      bapdev_stderr "已创建默认黑名单配置文件：${blacklist_file}"
    fi
  fi
  
  printf '%s\n' "${blacklist_file}"
}

# ============================================
# 状态文件处理
# ============================================

jar_state_file() {
  local project_root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local state_dir
  state_dir="$(bapdev_state_dir "${project_root}")"
  printf '%s/%s\n' "${state_dir}" "${JAR_STATE_FILE_NAME}"
}

jar_output_dir() {
  local project_root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local state_dir
  state_dir="$(bapdev_state_dir "${project_root}")"
  printf '%s/%s\n' "${state_dir}" "${JAR_OUTPUT_DIR_NAME}"
}

jar_read_state() {
  local state_file="$1"
  
  if [[ -f "${state_file}" ]]; then
    cat "${state_file}"
  else
    # 返回空的 JSON 结构
    printf '{"version":"1.0","lastScan":"","projectRoot":"","jars":{}}'
  fi
}

jar_write_state() {
  local state_file="$1"
  local content="$2"
  
  mkdir -p "$(dirname -- "${state_file}")"
  printf '%s\n' "${content}" > "${state_file}"
}

# 使用 jq 处理 JSON（如无 jq 则使用 Python）
jar_json_tool() {
  if command -v jq >/dev/null 2>&1; then
    jq "$@"
    return 0
  fi
  
  if command -v python3 >/dev/null 2>&1; then
    python3 -c "
import json, sys
data = json.load(sys.stdin)
result = $1
if isinstance(result, str):
    print(result)
else:
    print(json.dumps(result, ensure_ascii=False, indent=2))
" 2>/dev/null
    return 0
  fi
  
  bapdev_die "需要 jq 或 python3 来处理 JSON 数据" 2
}

jar_get_jar_state() {
  local state_json="$1"
  local jar_rel_path="$2"
  
  if command -v jq >/dev/null 2>&1; then
    jq -r --arg path "${jar_rel_path}" '.jars[$path] // empty' <<< "${state_json}"
  elif command -v python3 >/dev/null 2>&1; then
    python3 -c "
import json, sys
data = json.loads(sys.stdin.read())
path = '${jar_rel_path}'
if path in data.get('jars', {}):
    print(json.dumps(data['jars'][path], ensure_ascii=False))
" <<< "${state_json}"
  fi
}

jar_update_jar_state() {
  local state_file="$1"
  local jar_rel_path="$2"
  local jar_hash="$3"
  local jar_size="$4"
  local overview_file="$5"
  local index_file="$6"
  
  local timestamp
  timestamp="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  
  if command -v jq >/dev/null 2>&1; then
    local new_entry
    new_entry=$(jq -n \
      --arg hash "${jar_hash}" \
      --arg size "${jar_size}" \
      --arg ts "${timestamp}" \
      --arg overview "${overview_file}" \
      --arg index "${index_file}" \
      '{hash: ("sha256:" + $hash), size: ($size | tonumber), lastAnalyzed: $ts, overviewFile: $overview, indexFile: $index}')
    
    jq --arg path "${jar_rel_path}" \
       --argjson entry "${new_entry}" \
       --arg root "${BAPDEV_PROJECT_ROOT:-}" \
       --arg ts "${timestamp}" \
       '.jars[$path] = $entry | .lastScan = $ts | .projectRoot = $root' \
       "${state_file}" > "${state_file}.tmp"
    mv "${state_file}.tmp" "${state_file}"
  elif command -v python3 >/dev/null 2>&1; then
    python3 -c "
import json, sys, os
state_file = '${state_file}'
path = '${jar_rel_path}'
entry = {
    'hash': 'sha256:${jar_hash}',
    'size': int('${jar_size}'),
    'lastAnalyzed': '${timestamp}',
    'overviewFile': '${overview_file}',
    'indexFile': '${index_file}'
}

try:
    with open(state_file, 'r') as f:
        data = json.load(f)
except:
    data = {'version': '1.0', 'lastScan': '', 'projectRoot': '', 'jars': {}}

data['jars'][path] = entry
data['lastScan'] = '${timestamp}'
data['projectRoot'] = '${BAPDEV_PROJECT_ROOT:-}'

with open(state_file, 'w') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)
"
  fi
}

jar_remove_jar_state() {
  local state_file="$1"
  local jar_rel_path="$2"
  
  if command -v jq >/dev/null 2>&1; then
    jq --arg path "${jar_rel_path}" 'del(.jars[$path])' "${state_file}" > "${state_file}.tmp"
    mv "${state_file}.tmp" "${state_file}"
  elif command -v python3 >/dev/null 2>&1; then
    python3 -c "
import json
state_file = '${state_file}'
path = '${jar_rel_path}'

with open(state_file, 'r') as f:
    data = json.load(f)

if path in data.get('jars', {}):
    del data['jars'][path]

with open(state_file, 'w') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)
"
  fi
}

# ============================================
# jar 文件发现
# ============================================

jar_find_all_jars() {
  local project_root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local exclude_dirs="${2:-.git,.bapdev-cli,.opencode,node_modules}"
  
  # 使用 find 命令查找所有 jar 文件
  # 排除指定目录
  local exclude_args=""
  local dir
  for dir in ${exclude_dirs//,/ }; do
    exclude_args="${exclude_args} -not -path '*/${dir}/*'"
  done
  
  eval "find '${project_root}' -type f -name '*.jar' ${exclude_args}" 2>/dev/null | sort
}

jar_normalize_path() {
  local jar_path="$1"
  local project_root="${2:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  
  # 将绝对路径转换为相对路径
  if [[ "${jar_path}" == "${project_root}"* ]]; then
    jar_path="${jar_path#${project_root}/}"
  fi
  
  printf '%s\n' "${jar_path}"
}

# ============================================
# 输出文件命名
# ============================================

jar_safe_name() {
  local jar_path="$1"
  local jar_name
  jar_name="$(basename -- "${jar_path}" .jar)"
  
  # 替换特殊字符为下划线
  jar_name="${jar_name//[-.]/_}"
  # 去除版本号（简化命名）
  # jar_name="$(echo "${jar_name}" | sed -E 's/-[0-9]+[._-].*$//')"
  
  printf '%s\n' "${jar_name}"
}

jar_overview_file_name() {
  local jar_path="$1"
  local safe_name
  safe_name="$(jar_safe_name "${jar_path}")"
  printf '%s-overview.md\n' "${safe_name}"
}

jar_index_file_name() {
  local jar_path="$1"
  local safe_name
  safe_name="$(jar_safe_name "${jar_path}")"
  printf '%s-index.md\n' "${safe_name}"
}

# ============================================
# 待分析 jar 检测
# ============================================

jar_needs_analysis() {
  local jar_path="$1"
  local jar_hash="$2"
  local state_json="$3"
  
  local rel_path
  rel_path="$(jar_normalize_path "${jar_path}")"
  
  local existing_state
  existing_state="$(jar_get_jar_state "${state_json}" "${rel_path}")"
  
  # 如果没有状态记录，需要分析
  if [[ -z "${existing_state}" ]]; then
    return 0
  fi
  
  # 如果哈希不匹配，需要重新分析
  local existing_hash
  if command -v jq >/dev/null 2>&1; then
    existing_hash="$(jq -r '.hash' <<< "${existing_state}" 2>/dev/null)"
  elif command -v python3 >/dev/null 2>&1; then
    existing_hash="$(python3 -c "import json; print(json.loads('${existing_state}').get('hash',''))")"
  fi
  
  if [[ "${existing_hash}" != "sha256:${jar_hash}" ]]; then
    return 0
  fi
  
  # 已分析且哈希匹配，跳过
  return 1
}

# ============================================
# 输出 JSON 结果
# ============================================

jar_output_jar_info() {
  local jar_path="$1"
  local jar_hash="$2"
  local jar_size="$3"
  local needs_analysis="${4:-true}"
  local rel_path
  rel_path="$(jar_normalize_path "${jar_path}")"
  
  printf '{"path":"%s","relativePath":"%s","hash":"sha256:%s","size":%d,"needsAnalysis":%s}\n' \
    "${jar_path}" "${rel_path}" "${jar_hash}" "${jar_size}" "${needs_analysis}"
}

jar_output_jar_list() {
  local jars_json="$1"  # JSON 数组字符串
  
  printf '{"jars":%s}\n' "${jars_json}"
}