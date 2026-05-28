#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/common.sh"

print_usage() {
  cat <<'EOF'
用法：
  <技能目录>/scripts/auth-http.sh [选项]

说明：
  1. 通过 HTTP + JWT 完成首次鉴权。
  2. 只需要提供 HTTP 服务地址，例如 http://127.0.0.1:8090，脚本会自动补全用户接口路径。
  3. 鉴权成功后，会把会话种子写入 ~/.bapdev-cli/<ws目录>/。
  4. 如果当前工程下已有 .bapdev-cli/cloud.properties，会优先把其中的 ws 地址作为默认值。

选项：
  --project-root <dir>   指定项目根目录或查找 .bapdev-cli 的起点
  --uri <ws-uri>         ws 服务地址
  --http-user-api <url>  HTTP 服务地址，例如 http://127.0.0.1:8090
  --jwt-token <token>    JWT 令牌
  -h, --help             显示帮助

示例：
  "<技能实际目录>/scripts/auth-http.sh" --project-root .
  "<技能实际目录>/scripts/auth-http.sh" --uri ws://127.0.0.1:9000/ws --http-user-api http://127.0.0.1:8090 --jwt-token xxx
EOF
}

prompt_with_default() {
  local prompt_text="$1"
  local default_value="${2:-}"
  local input_value=""

  if [[ -n "${default_value}" ]]; then
    read -r -p "${prompt_text} [${default_value}]: " input_value
    if [[ -z "${input_value}" ]]; then
      printf '%s\n' "${default_value}"
      return 0
    fi
  else
    read -r -p "${prompt_text}: " input_value
  fi

  printf '%s\n' "${input_value}"
}

PROJECT_ROOT="${PWD}"
INPUT_URI="${BAP_URI:-}"
INPUT_HTTP_USER_API=""
INPUT_JWT_TOKEN=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project-root)
      PROJECT_ROOT="$2"
      shift 2
      ;;
    --uri)
      INPUT_URI="$2"
      shift 2
      ;;
    --http-user-api)
      INPUT_HTTP_USER_API="$2"
      shift 2
      ;;
    --jwt-token)
      INPUT_JWT_TOKEN="$2"
      shift 2
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

PROJECT_ROOT="$(cd -- "${PROJECT_ROOT}" && pwd)"
bapdev_load_secure_env "${PROJECT_ROOT}"

if [[ -z "${INPUT_URI}" ]]; then
  INPUT_URI="$(prompt_with_default "ws 服务地址" "${BAP_URI:-}")"
fi
if [[ -z "${INPUT_HTTP_USER_API}" ]]; then
  INPUT_HTTP_USER_API="$(prompt_with_default "HTTP 服务地址" "http://127.0.0.1:8090")"
fi
if [[ -z "${INPUT_JWT_TOKEN}" ]]; then
  read -r -p "JWT 令牌: " INPUT_JWT_TOKEN
fi

[[ -n "${INPUT_URI}" ]] || bapdev_die "ws 服务地址不能为空。" 2
[[ -n "${INPUT_HTTP_USER_API}" ]] || bapdev_die "HTTP 服务地址不能为空。" 2
[[ -n "${INPUT_JWT_TOKEN}" ]] || bapdev_die "JWT 令牌不能为空。" 2

export BAP_URI="${INPUT_URI}"
export BAP_HTTP_USER_API="${INPUT_HTTP_USER_API}"
export BAP_JWT_TOKEN="${INPUT_JWT_TOKEN}"

exec "${SCRIPT_DIR}/run-cli.sh" auth-http
