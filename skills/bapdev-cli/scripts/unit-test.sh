#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

# 所有参数都会直接透传给 CLI。
# 常用示例：
#   ./unit-test.sh --class test.HelloServiceSilentTest
#   ./unit-test.sh --class test.HelloServiceSilentTest --execution-mode fork
#   ./unit-test.sh --class test.HelloServiceSilentTest --execution-mode in-process
exec "${SCRIPT_DIR}/run-cli.sh" unit-test "$@"
