#!/usr/bin/env bash

set -euo pipefail

BAPDEV_LIB_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
BAPDEV_SCRIPTS_DIR="$(cd -- "${BAPDEV_LIB_DIR}/.." && pwd)"
BAPDEV_SKILL_DIR="$(cd -- "${BAPDEV_SCRIPTS_DIR}/.." && pwd)"
BAPDEV_DEFAULT_JAR="${BAPDEV_SKILL_DIR}/bapdev-cli-1.0.0-SNAPSHOT.jar"
BAPDEV_LEGACY_RUNTIME_LIST="${BAPDEV_LIB_DIR}/legacy-runtime-jars.txt"
BAPDEV_STATE_DIR_NAME=".bapdev-cli"
BAPDEV_SESSION_FILE_NAME="session.properties"
BAPDEV_CLOUD_FILE_NAME="cloud.properties"

bapdev_stderr() {
  printf '%s\n' "$*" >&2
}

bapdev_die() {
  local message="$1"
  local code="${2:-1}"
  bapdev_stderr "${message}"
  exit "${code}"
}

bapdev_require_java() {
  command -v java >/dev/null 2>&1 || bapdev_die "未找到 java，请先安装 Java 运行环境。" 127
}

bapdev_resolve_jar() {
  local jar_path="${BAPDEV_CLI_JAR:-${BAPDEV_DEFAULT_JAR}}"
  [[ -f "${jar_path}" ]] || bapdev_die "未找到 bapdev-cli jar：${jar_path}" 2
  printf '%s\n' "${jar_path}"
}

bapdev_resolve_legacy_lib_dir() {
  local candidates=()

  if [[ -n "${BAPDEVTOOL_LIB_DIR:-}" ]]; then
    candidates+=("${BAPDEVTOOL_LIB_DIR}")
  fi
  if [[ -n "${BAPDEVTOOL_SOURCE_DIR:-}" ]]; then
    candidates+=("${BAPDEVTOOL_SOURCE_DIR}/lib/platform")
  fi

  candidates+=(
    "${BAPDEV_SKILL_DIR}/lib/platform"
    "/mnt/d/AIProject/BapDevTool/lib/platform"
    "/d/AIProject/BapDevTool/lib/platform"
  )

  local dir
  for dir in "${candidates[@]}"; do
    if [[ -d "${dir}" ]]; then
      printf '%s\n' "${dir}"
      return 0
    fi
  done
  return 1
}

bapdev_build_legacy_classpath() {
  local legacy_dir
  legacy_dir="$(bapdev_resolve_legacy_lib_dir)"

  local cp_entries=()
  if [[ -f "${BAPDEV_LEGACY_RUNTIME_LIST}" ]]; then
    local jar_name
    while IFS= read -r jar_name; do
      [[ -n "${jar_name}" ]] || continue
      [[ "${jar_name}" =~ ^# ]] && continue
      if [[ -f "${legacy_dir}/${jar_name}" ]]; then
        cp_entries+=("${legacy_dir}/${jar_name}")
      fi
    done < "${BAPDEV_LEGACY_RUNTIME_LIST}"
  else
    local jar_path
    for jar_path in "${legacy_dir}"/*.jar; do
      [[ -f "${jar_path}" ]] || continue
      case "$(basename -- "${jar_path}")" in
        slf4j-log4j12-*.jar)
          continue
          ;;
      esac
      cp_entries+=("${jar_path}")
    done
  fi

  local joined=""
  local entry
  for entry in "${cp_entries[@]}"; do
    if [[ -z "${joined}" ]]; then
      joined="${entry}"
    else
      joined="${joined}:${entry}"
    fi
  done

  printf '%s\n' "${joined}"
}

bapdev_find_project_root() {
  local start_path="${1:-$PWD}"
  local current
  if [[ -d "${start_path}" ]]; then
    current="${start_path}"
  else
    current="$(cd -- "$(dirname -- "${start_path}")" && pwd)"
  fi

  while [[ -n "${current}" ]]; do
    if [[ -d "${current}/${BAPDEV_STATE_DIR_NAME}" ]]; then
      printf '%s\n' "${current}"
      return 0
    fi
    local parent
    parent="$(dirname -- "${current}")"
    if [[ "${parent}" == "${current}" ]]; then
      break
    fi
    current="${parent}"
  done

  return 1
}

bapdev_state_dir() {
  local root="$1"
  printf '%s\n' "${root}/${BAPDEV_STATE_DIR_NAME}"
}

bapdev_global_state_root() {
  printf '%s\n' "${HOME}/${BAPDEV_STATE_DIR_NAME}"
}

bapdev_properties_get() {
  local file="$1"
  local key="$2"
  [[ -f "${file}" ]] || return 1
  sed -nE "s/^${key}=//p" "${file}" | tail -n 1
}

bapdev_properties_set() {
  local file="$1"
  local key="$2"
  local value="$3"

  mkdir -p "$(dirname -- "${file}")"
  if [[ -f "${file}" ]] && grep -q "^${key}=" "${file}"; then
    sed -i "s#^${key}=.*#${key}=${value}#g" "${file}"
  else
    printf '%s=%s\n' "${key}" "${value}" >> "${file}"
  fi
}

bapdev_write_project_uri_if_missing() {
  local root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local state_dir cloud_file

  if [[ -d "${root}" ]]; then
    root="$(cd -- "${root}" && pwd)"
  else
    root="$(cd -- "$(dirname -- "${root}")" && pwd)"
  fi

  [[ -n "${BAP_URI:-}" ]] || bapdev_die "无法写入当前工程 ws 地址，因为尚未解析到 BAP_URI。" 2

  state_dir="$(bapdev_state_dir "${root}")"
  cloud_file="${state_dir}/${BAPDEV_CLOUD_FILE_NAME}"
  mkdir -p "${state_dir}"

  if [[ -f "${cloud_file}" ]]; then
    local existing_uri
    existing_uri="$(bapdev_properties_get "${cloud_file}" "uri" || true)"
    if [[ -n "${existing_uri}" ]]; then
      return 0
    fi
  fi

  bapdev_properties_set "${cloud_file}" "uri" "${BAP_URI}"
  if [[ -n "${BAP_PROJECT:-}" ]]; then
    bapdev_properties_set "${cloud_file}" "project" "${BAP_PROJECT}"
  fi
  if [[ -n "${BAP_PROJECT_NAME:-}" ]]; then
    bapdev_properties_set "${cloud_file}" "projectName" "${BAP_PROJECT_NAME}"
  fi
  if [[ -n "${BAP_USER:-}" ]]; then
    bapdev_properties_set "${cloud_file}" "user" "${BAP_USER}"
  fi
  bapdev_stderr "已为当前工程写入最小 cloud.properties：${cloud_file}"
}

bapdev_is_blank() {
  local value="${1:-}"
  [[ -z "${value//[[:space:]]/}" ]]
}

bapdev_sha1() {
  local value="$1"
  if command -v sha1sum >/dev/null 2>&1; then
    printf '%s' "${value}" | sha1sum | awk '{print $1}'
    return 0
  fi
  if command -v shasum >/dev/null 2>&1; then
    printf '%s' "${value}" | shasum -a 1 | awk '{print $1}'
    return 0
  fi
  python3 -c 'import hashlib,sys; print(hashlib.sha1(sys.argv[1].encode("utf-8")).hexdigest())' "${value}"
}

bapdev_global_server_dir() {
  local uri="$1"
  [[ -n "${uri}" ]] || return 1

  local safe_name
  safe_name="$(printf '%s' "${uri}" | sed 's/[^[:alnum:]]/_/g')"
  safe_name="${safe_name:0:48}"
  local digest
  digest="$(bapdev_sha1 "${uri}")"
  printf '%s/%s_%s\n' "$(bapdev_global_state_root)" "${safe_name}" "${digest:0:12}"
}

bapdev_global_file() {
  local uri="$1"
  local filename="$2"
  local server_dir
  server_dir="$(bapdev_global_server_dir "${uri}")" || return 1
  printf '%s/%s\n' "${server_dir}" "${filename}"
}

bapdev_ensure_security_config() {
  local root="$1"
  local state_dir
  state_dir="$(bapdev_state_dir "${root}")"
  mkdir -p "${state_dir}"

  local security_file="${state_dir}/security.properties"
  if [[ ! -f "${security_file}" ]]; then
    cat > "${security_file}" <<'EOF'
allowDevelopRead=false
EOF
  fi
}

bapdev_load_cloud_metadata() {
  local root="$1"
  local cloud_file
  cloud_file="$(bapdev_state_dir "${root}")/${BAPDEV_CLOUD_FILE_NAME}"
  [[ -f "${cloud_file}" ]] || return 1

  if [[ -z "${BAP_URI:-}" ]]; then
    export BAP_URI="$(bapdev_properties_get "${cloud_file}" "uri" || true)"
  fi
  if [[ -z "${BAP_PROJECT:-}" ]]; then
    export BAP_PROJECT="$(bapdev_properties_get "${cloud_file}" "project" || true)"
  fi
  if [[ -z "${BAP_PROJECT_NAME:-}" ]]; then
    export BAP_PROJECT_NAME="$(bapdev_properties_get "${cloud_file}" "projectName" || true)"
  fi
  if [[ -z "${BAP_USER:-}" ]]; then
    export BAP_USER="$(bapdev_properties_get "${cloud_file}" "user" || true)"
  fi
}

bapdev_load_session_metadata() {
  local state_dir="$1"
  local session_file="${state_dir}/${BAPDEV_SESSION_FILE_NAME}"
  [[ -f "${session_file}" ]] || return 1

  if [[ -z "${BAP_USER:-}" ]]; then
    export BAP_USER="$(bapdev_properties_get "${session_file}" "userCode" || true)"
  fi
  if [[ -z "${BAP_SESSION_USER_CODE:-}" ]]; then
    export BAP_SESSION_USER_CODE="$(bapdev_properties_get "${session_file}" "userCode" || true)"
  fi
  if [[ -z "${BAP_SESSION_USER_ALIAS:-}" ]]; then
    export BAP_SESSION_USER_ALIAS="$(bapdev_properties_get "${session_file}" "userAlias" || true)"
  fi
  if [[ -z "${BAP_SESSION_USER_UUID:-}" ]]; then
    export BAP_SESSION_USER_UUID="$(bapdev_properties_get "${session_file}" "userUuid" || true)"
  fi
  if [[ -z "${BAP_SESSION_PASSWORD:-}" ]]; then
    export BAP_SESSION_PASSWORD="$(bapdev_properties_get "${session_file}" "sessionPassword" || true)"
  fi
}

bapdev_load_global_env() {
  local uri="${1:-${BAP_URI:-}}"
  [[ -n "${uri}" ]] || return 1

  local global_cloud_file
  global_cloud_file="$(bapdev_global_file "${uri}" "${BAPDEV_CLOUD_FILE_NAME}")" || return 1
  if [[ -f "${global_cloud_file}" ]]; then
    if [[ -z "${BAP_PROJECT:-}" ]]; then
      export BAP_PROJECT="$(bapdev_properties_get "${global_cloud_file}" "project" || true)"
    fi
    if [[ -z "${BAP_PROJECT_NAME:-}" ]]; then
      export BAP_PROJECT_NAME="$(bapdev_properties_get "${global_cloud_file}" "projectName" || true)"
    fi
    if [[ -z "${BAP_USER:-}" ]]; then
      export BAP_USER="$(bapdev_properties_get "${global_cloud_file}" "user" || true)"
    fi
  fi

  local global_state_dir
  global_state_dir="$(dirname -- "${global_cloud_file}")"
  bapdev_load_session_metadata "${global_state_dir}" || true
  export BAPDEV_GLOBAL_STATE_DIR="${global_state_dir}"
}

bapdev_extract_attr() {
  local file="$1"
  local attr="$2"
  sed -nE "s/.*${attr}=\"([^\"]*)\".*/\\1/p" "${file}" | head -n 1
}

bapdev_load_develop_env() {
  local start_path="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local root=""

  if [[ "${BAPDEV_ALLOW_DEVELOP_READ:-0}" != "1" ]]; then
    return 1
  fi

  if root="$(bapdev_find_project_root "${start_path}")"; then
    export BAPDEV_PROJECT_ROOT="${root}"
    export BAPDEV_DEVELOP_FILE="${root}/.develop"
  else
    return 1
  fi

  if [[ -z "${BAP_URI:-}" ]]; then
    export BAP_URI="$(bapdev_extract_attr "${BAPDEV_DEVELOP_FILE}" "Uri")"
  fi
  if [[ -z "${BAP_PROJECT:-}" ]]; then
    export BAP_PROJECT="$(bapdev_extract_attr "${BAPDEV_DEVELOP_FILE}" "Project")"
  fi
  if [[ -z "${BAP_USER:-}" ]]; then
    export BAP_USER="$(bapdev_extract_attr "${BAPDEV_DEVELOP_FILE}" "User")"
  fi
  if [[ -z "${BAP_PASSWORD:-}" ]]; then
    export BAP_PASSWORD="$(bapdev_extract_attr "${BAPDEV_DEVELOP_FILE}" "Password")"
  fi

  return 0
}

bapdev_load_secure_env() {
  local start_path="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local root=""

  if root="$(bapdev_find_project_root "${start_path}")"; then
    export BAPDEV_PROJECT_ROOT="${root}"
  else
    if [[ -d "${start_path}" ]]; then
      export BAPDEV_PROJECT_ROOT="$(cd -- "${start_path}" && pwd)"
    else
      export BAPDEV_PROJECT_ROOT="$(cd -- "$(dirname -- "${start_path}")" && pwd)"
    fi
  fi

  bapdev_ensure_security_config "${BAPDEV_PROJECT_ROOT}"
  export BAPDEV_STATE_DIR="$(bapdev_state_dir "${BAPDEV_PROJECT_ROOT}")"
  bapdev_load_cloud_metadata "${BAPDEV_PROJECT_ROOT}" || true
  bapdev_load_session_metadata "${BAPDEV_STATE_DIR}" || true
  bapdev_load_global_env "${BAP_URI:-}" || true

  local allow_develop
  allow_develop="$(bapdev_properties_get "${BAPDEV_STATE_DIR}/security.properties" "allowDevelopRead" || true)"
  if [[ "${allow_develop}" == "true" ]]; then
    export BAPDEV_ALLOW_DEVELOP_READ=1
    bapdev_load_develop_env "${BAPDEV_PROJECT_ROOT}" || true
  fi
}

bapdev_has_session_seed() {
  local root="${1:-${BAPDEV_PROJECT_ROOT:-$PWD}}"
  local state_dir
  state_dir="$(bapdev_state_dir "${root}")"
  if [[ -f "${state_dir}/${BAPDEV_SESSION_FILE_NAME}" ]]; then
    return 0
  fi

  local uri="${BAP_URI:-$(bapdev_properties_get "${state_dir}/${BAPDEV_CLOUD_FILE_NAME}" "uri" || true)}"
  if [[ -n "${uri}" ]]; then
    local global_session_file
    global_session_file="$(bapdev_global_file "${uri}" "${BAPDEV_SESSION_FILE_NAME}" || true)"
    if [[ -n "${global_session_file}" && -f "${global_session_file}" ]]; then
      return 0
    fi
  fi
  return 1
}


bapdev_build_global_args() {
  local -n out_ref="$1"
  out_ref=()

  if [[ -n "${BAP_URI:-}" ]]; then
    out_ref+=("--uri" "${BAP_URI}")
  fi
  if [[ -n "${BAP_USER:-}" ]]; then
    out_ref+=("--user" "${BAP_USER}")
  fi
  if [[ -n "${BAP_PASSWORD:-}" ]]; then
    out_ref+=("--password" "${BAP_PASSWORD}")
  fi
  if [[ -n "${BAP_HTTP_USER_API:-}" ]]; then
    out_ref+=("--http-user-api" "${BAP_HTTP_USER_API}")
  fi
  if [[ -n "${BAP_JWT_TOKEN:-}" ]]; then
    out_ref+=("--jwt-token" "${BAP_JWT_TOKEN}")
  fi
  if [[ -n "${BAP_PROJECT:-}" ]]; then
    out_ref+=("--project" "${BAP_PROJECT}")
  fi
  if [[ -n "${BAPDEV_CONFIG:-}" ]]; then
    out_ref+=("--config" "${BAPDEV_CONFIG}")
  fi
}

bapdev_print_env_exports() {
  printf 'export BAPDEV_SKILL_DIR=%q\n' "${BAPDEV_SKILL_DIR}"
  printf 'export BAPDEV_CLI_JAR=%q\n' "$(bapdev_resolve_jar)"
  if [[ -n "${BAPDEV_PROJECT_ROOT:-}" ]]; then
    printf 'export BAPDEV_PROJECT_ROOT=%q\n' "${BAPDEV_PROJECT_ROOT}"
  fi
  if [[ -n "${BAPDEV_STATE_DIR:-}" ]]; then
    printf 'export BAPDEV_STATE_DIR=%q\n' "${BAPDEV_STATE_DIR}"
  fi
  if [[ -n "${BAP_URI:-}" ]]; then
    printf 'export BAP_URI=%q\n' "${BAP_URI}"
  fi
  if [[ -n "${BAP_PROJECT:-}" ]]; then
    printf 'export BAP_PROJECT=%q\n' "${BAP_PROJECT}"
  fi
  if [[ -n "${BAP_PROJECT_NAME:-}" ]]; then
    printf 'export BAP_PROJECT_NAME=%q\n' "${BAP_PROJECT_NAME}"
  fi
  if [[ -n "${BAP_USER:-}" ]]; then
    printf 'export BAP_USER=%q\n' "${BAP_USER}"
  fi
  if [[ -n "${BAP_HTTP_USER_API:-}" ]]; then
    printf 'export BAP_HTTP_USER_API=%q\n' "${BAP_HTTP_USER_API}"
  fi
  if [[ -n "${BAP_SESSION_USER_CODE:-}" ]]; then
    printf 'export BAP_SESSION_USER_CODE=%q\n' "${BAP_SESSION_USER_CODE}"
  fi
  if [[ -n "${BAP_SESSION_USER_ALIAS:-}" ]]; then
    printf 'export BAP_SESSION_USER_ALIAS=%q\n' "${BAP_SESSION_USER_ALIAS}"
  fi
  if [[ -n "${BAP_SESSION_USER_UUID:-}" ]]; then
    printf 'export BAP_SESSION_USER_UUID=%q\n' "${BAP_SESSION_USER_UUID}"
  fi
  if [[ -n "${BAPDEV_GLOBAL_STATE_DIR:-}" ]]; then
    printf 'export BAPDEV_GLOBAL_STATE_DIR=%q\n' "${BAPDEV_GLOBAL_STATE_DIR}"
  fi
}
