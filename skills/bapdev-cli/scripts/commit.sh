#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/common.sh"

EXTRA_ARGS=()
if ! bapdev_has_option "--project-root" "$@"; then
  if [[ -z "${BAPDEV_PROJECT_ROOT:-}" ]]; then
    bapdev_load_develop_env "${PWD}" || true
  fi
  if [[ -n "${BAPDEV_PROJECT_ROOT:-}" ]]; then
    EXTRA_ARGS+=("--project-root" "${BAPDEV_PROJECT_ROOT}")
  fi
fi

exec "${SCRIPT_DIR}/run-cli.sh" commit "${EXTRA_ARGS[@]}" "$@"

