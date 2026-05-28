#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

if [[ $# -gt 0 ]]; then
  case "$1" in
    -h|--help)
      cat <<'EOF'
用法：
  history.sh [全局环境变量已初始化时] [选项]

等价原生命令：
  bapdev history [-f <file> | --files <file1,file2>] [-l <limit>]

选项：
  -f, --file <file>     单个文件路径，查询文件历史时使用
      --files <files>   多个文件路径，逗号分隔
  -l, --limit <n>       返回数量限制，默认 10

说明：
  1. 项目标识优先从当前工程 `.bapdev-cli` 读取。
  2. 连接信息优先从 `.bapdev-cli` 与全局会话信息读取。
  3. 该子命令在 jar 内未实现标准 --help，因此包装脚本在这里提供帮助文本。
EOF
      exit 0
      ;;
  esac
fi

exec "${SCRIPT_DIR}/run-cli.sh" history "$@"
