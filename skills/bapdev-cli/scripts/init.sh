#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/common.sh"

print_usage() {
  cat <<'EOF'
用法：
  <技能目录>/scripts/init.sh [选项]

说明：
  1. 在项目根目录初始化工程协作文件 `AGENTS.md`。
  2. 模板文件来自当前技能目录内部，不依赖外部工程路径。
  3. 该命令是工程初始化辅助脚本，不进入 bapdev-cli jar。

选项：
  --project-root <dir>  指定项目根目录，默认当前目录
  --force               已存在 AGENTS.md 时强制覆盖
  -h, --help            显示帮助

示例：
  "<技能实际目录>/scripts/init.sh" --project-root .
  "<技能实际目录>/scripts/init.sh" --project-root . --force
EOF
}

PROJECT_ROOT="${PWD}"
FORCE=0
TEMPLATE_FILE="${BAPDEV_SKILL_DIR}/references/templates/AGENTS.template.md"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project-root)
      PROJECT_ROOT="$2"
      shift 2
      ;;
    --force)
      FORCE=1
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

PROJECT_ROOT="$(cd -- "${PROJECT_ROOT}" && pwd)"
TARGET_FILE="${PROJECT_ROOT}/AGENTS.md"

[[ -f "${TEMPLATE_FILE}" ]] || bapdev_die "未找到 AGENTS 模板：${TEMPLATE_FILE}" 2

if [[ -f "${TARGET_FILE}" && "${FORCE}" != "1" ]]; then
  bapdev_die "目标文件已存在：${TARGET_FILE}
如需覆盖，请重新执行并追加 --force。" 2
fi

cp "${TEMPLATE_FILE}" "${TARGET_FILE}"
printf '已初始化工程协作文件：%s\n' "${TARGET_FILE}"
printf '模板来源：%s\n' "${TEMPLATE_FILE}"
