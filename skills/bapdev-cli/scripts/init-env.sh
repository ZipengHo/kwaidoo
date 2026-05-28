#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/common.sh"

print_usage() {
  cat <<'EOF'
用法：
  source <技能目录>/scripts/init-env.sh [选项]
  <技能目录>/scripts/init-env.sh [选项]

说明：
  1. 被 source 时，会把项目级配置装载到当前 shell。
  2. 直接执行时，会输出 export 语句，可配合 eval 使用。
  3. 默认优先读取当前工程 `.bapdev-cli`，其次读取 `~/.bapdev-cli/<ws目录>`，不自动读取 `.develop`。
  4. 当当前工程还没有 `.bapdev-cli` 或缺少 ws 服务地址时，可先通过本脚本写入最小连接信息，
     再尝试复用 `~/.bapdev-cli/<ws目录>/` 下的全局会话；只有全局会话不存在时，才需要执行 `auth-http.sh`。

选项：
  --project-root <dir>  指定项目根目录或查找 `.bapdev-cli` 的起点
  --uri <ws-uri>        指定 ws 服务地址，并可配合 --write-uri 写入当前工程
  --project-name <name> 指定工程名称，并可配合 --write-uri 写入当前工程
  --config <file>       设置 BAPDEV_CONFIG
  --write-uri           若当前工程缺少 `.bapdev-cli/cloud.properties`，则把已解析到的 ws 地址写入当前工程
  -h, --help            显示帮助

示例：
  source "<技能实际目录>/scripts/init-env.sh"
  eval "$("<技能实际目录>/scripts/init-env.sh" --project-root /path/to/project)"
EOF
}

PROJECT_ROOT_INPUT="${BAPDEV_PROJECT_ROOT:-}"
WRITE_URI=0
INPUT_URI="${BAP_URI:-}"
INPUT_PROJECT_NAME="${BAP_PROJECT_NAME:-}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project-root)
      PROJECT_ROOT_INPUT="$2"
      shift 2
      ;;
    --uri)
      INPUT_URI="$2"
      shift 2
      ;;
    --project-name)
      INPUT_PROJECT_NAME="$2"
      shift 2
      ;;
    --config)
      export BAPDEV_CONFIG="$2"
      shift 2
      ;;
    --write-uri)
      WRITE_URI=1
      shift
      ;;
    -h|--help)
      print_usage
      exit 0
      ;;
    *)
      bapdev_die "未知参数：$1" 2
      ;;
  esac
done

export BAPDEV_SKILL_DIR
export BAPDEV_CLI_JAR="$(bapdev_resolve_jar)"
if [[ -n "${INPUT_URI}" ]]; then
  export BAP_URI="${INPUT_URI}"
fi
if [[ -n "${INPUT_PROJECT_NAME}" ]]; then
  export BAP_PROJECT_NAME="${INPUT_PROJECT_NAME}"
fi
bapdev_load_secure_env "${PROJECT_ROOT_INPUT:-$PWD}"

if [[ "${WRITE_URI}" == "1" ]]; then
  bapdev_write_project_uri_if_missing "${BAPDEV_PROJECT_ROOT:-${PROJECT_ROOT_INPUT:-$PWD}}"
fi

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  bapdev_print_env_exports
else
  bapdev_stderr "已初始化 BapDev CLI 项目配置。"
  if [[ -n "${BAPDEV_PROJECT_ROOT:-}" ]]; then
    bapdev_stderr "项目目录：${BAPDEV_PROJECT_ROOT}"
  fi
  if [[ -n "${BAPDEV_STATE_DIR:-}" ]]; then
    bapdev_stderr "安全配置目录：${BAPDEV_STATE_DIR}"
  fi
  if [[ -n "${BAP_URI:-}" ]]; then
    bapdev_stderr "云工程地址：${BAP_URI}"
  else
    bapdev_stderr "当前工程尚未声明 ws 服务地址。请先在当前工程的 .bapdev-cli/cloud.properties 中设置 uri，或执行 auth-http.sh。"
  fi
  if [[ -n "${BAP_PROJECT:-}" ]]; then
    bapdev_stderr "项目ID：${BAP_PROJECT}"
  fi
  if [[ -n "${BAP_PROJECT_NAME:-}" ]]; then
    bapdev_stderr "工程名称：${BAP_PROJECT_NAME}"
  fi
  if [[ -n "${BAP_USER:-}" ]]; then
    bapdev_stderr "连接用户：${BAP_USER}"
  fi
  if [[ -z "${BAP_PASSWORD:-}" ]]; then
    if [[ -n "${BAP_SESSION_USER_CODE:-}" ]]; then
      bapdev_stderr "已加载会话种子用户：${BAP_SESSION_USER_CODE}"
    else
      bapdev_stderr "未找到可用会话种子。如需首次鉴权，请执行：\"${SCRIPT_DIR}/auth-http.sh\""
    fi
  fi
fi
