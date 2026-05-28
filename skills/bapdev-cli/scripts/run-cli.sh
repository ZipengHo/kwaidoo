#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/common.sh"

bapdev_require_java

if [[ $# -lt 1 ]]; then
  bapdev_die "请至少提供一个子命令，例如：commit、publish、download。" 2
fi

COMMAND_NAME="$1"
shift

SHOW_HELP_OR_VERSION=0
for arg in "$@"; do
  case "${arg}" in
    -h|--help|-V|--version)
      SHOW_HELP_OR_VERSION=1
      break
      ;;
  esac
done

if [[ "${BAPDEV_SKIP_AUTO_ENV:-0}" != "1" ]]; then
  bapdev_load_secure_env "${BAPDEV_PROJECT_ROOT:-$PWD}"
fi

bapdev_require_uri() {
  if [[ -n "${BAP_URI:-}" ]]; then
    return 0
  fi
  bapdev_die "缺少 ws 服务地址。请先在当前工程的 .bapdev-cli/cloud.properties 中提供 uri，或执行 auth-http.sh 完成首次鉴权。" 2
}

case "${COMMAND_NAME}" in
  ""|-h|--help|-V|--version)
    ;;
  resolve-project|compile|auth-http)
    ;;
  *)
    if [[ "${SHOW_HELP_OR_VERSION}" != "1" ]]; then
      bapdev_require_uri
    fi
    ;;
esac

GLOBAL_ARGS=()
bapdev_build_global_args GLOBAL_ARGS

if bapdev_resolve_legacy_lib_dir >/dev/null 2>&1; then
  LEGACY_CLASSPATH="$(bapdev_build_legacy_classpath)"
  exec java -cp "${LEGACY_CLASSPATH}:$(bapdev_resolve_jar)" com.bap.cli.BapDevCli "${GLOBAL_ARGS[@]}" "${COMMAND_NAME}" "$@"
fi

exec java -jar "$(bapdev_resolve_jar)" "${GLOBAL_ARGS[@]}" "${COMMAND_NAME}" "$@"
