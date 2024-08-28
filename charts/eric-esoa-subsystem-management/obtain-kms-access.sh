#!/bin/bash
#
# COPYRIGHT Ericsson 2023-2024
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

# This bash script create the roles and policies required to configure Subsystem Manager's access towards ADP KMS
# It performs the following steps:
#   1. Log into ADP KMS REST API.
#   2. Retrieve KMS client_token.
#   3. Create KMS encryption key policy.
#   4. Create KMS encrypt policy.
#   5. Create KMS decrypt policy.
#   6. Bind KMS role to Subsystem Manager service account.
#   7. Create an ADP KMS encryption key using the client_token from the previous step.

######################################################################################################################################################

# Utility Functions

# Logs a message to the console, formatted with useful information such as:
#   date/time stamp
#   level (passed by argument: INFO (default), WARN, ERROR, or DEBUG)
#   script line number
# Multi-line messages will be split into individual log lines
function log() {
    local level="\e[34mINFO\e[0m"

    case "${1}" in
        DEBUG|debug)
            if [[ ${DEBUG_OUTPUT} ]]; then
                level="\e[36mDEBUG\e[0m"
                shift
            else
                return
            fi
        ;;
        WARN|warn)
            level="\e[33mWARN\e[0m"
            shift
        ;;
        ERROR|error)
            level="\e[31mERROR\e[0m"
            shift
        ;;
    esac

    local line_portion
    line_portion=$(
        if [[ -n ${line_number} ]]; then
            echo " - {line: ${line_number}}"
        else
            echo " - {line: ${BASH_LINENO[0]}}"
        fi
    )

    if [[ "${*}" = *$'\n'* ]]; then
        echo "${*}" | while read -r individual_line; do
            line_number=${BASH_LINENO[0]} log $(echo "${level}" | sed 's/.*m\(.*\)\\e.*/\1/') "${individual_line}"
        done
        return
    fi

    local logged_string=" - $*"

    echo -e "$(date -Ins | sed 's/,\(...\).*\(\+.*\)/.\1\2/' | tr 'T' ' ') - [${level}] ${line_portion} ${logged_string}"
}

# Aliases cURL with some default arguments useful when calling KMS REST API
function curlKms(){
    curl --include --cacert "${ADP_KMS_CACERT_FILE_PATH}" "${@}"
}

######################################################################################################################################################

# Global Variable

ADP_KMS_BASE_PATH="https://eric-sec-key-management:8200/v1"

######################################################################################################################################################

# Log in to KMS with the Subsystem Manager service account.
function log_into_kms() {
  local adp_kms_base_path="${1}"
  local default_login_role='service-credentials'

  log "Logging in to KMS with role ${default_login_role} and service account ${ADP_KMS_ADMIN_ACCOUNT_NAME}"

  # The file may not be read right here.
  local kms_login_response
  kms_login_response=$(\
      curlKms --request POST \
      "${adp_kms_base_path}/auth/kubernetes/login" \
      --data '{"role":"'"${default_login_role}"'", "jwt":"'"$(cat /run/secrets/"${ADP_KMS_ADMIN_ACCOUNT_NAME}"/token)"'"}' \
  ) || {
      log error 'Error encountered logging in to KMS (eric-sec-key-management):'
      log error "${kms_login_response}"
      exit 1
  }

  local login_status
  login_status=$(echo "${kms_login_response}" | grep "HTTP/")
  [[ "${login_status}" = *"200"* ]] || {
      log error "KMS login failed - response was:"
      log error "${kms_login_response}"
      exit 1
  }

  log "KMS login successful role ${default_login_role}"

  echo "${kms_login_response}"
}

KMS_LOGIN_RESPONSE=$(log_into_kms "${ADP_KMS_BASE_PATH}")

######################################################################################################################################################

# Retrieve the ADP client_token.
KMS_CREDENTIAL_TOKEN_ADMIN=$(\
    grep -e '^{' <<< "${KMS_LOGIN_RESPONSE}" \
    | tr ',' '\n' \
    | sed -n 's/.*"client_token":"\(.*\)"/\1/p'\
)

log "Successfully retrieved KMS client_token."

######################################################################################################################################################

# Retrieve the unlimited (root) admin token stored by ADP at service-credentials/${ADP_KMS_ADMIN_ACCOUNT_NAME}/credentials
function retrieve_kms_unlimited_admin_token() {
  local kms_credential_token_admin="${1}"
  local adp_kms_base_path="${2}"

  log "Retrieving unlimited admin token."

  local kms_get_response
  kms_get_response=$(\
      curlKms --header "X-Vault-Token: ${kms_credential_token_admin}" \
      --request GET \
      "${adp_kms_base_path}/secret-v2/data/service-credentials/${ADP_KMS_ADMIN_ACCOUNT_NAME}/credentials" \
  ) || {
      log error 'Error encountered retrieving admin token from KMS (eric-sec-key-management)'
      exit 1
  }

  local get_status
  get_status=$(echo "${kms_get_response}" | grep "HTTP/")
  [[ $get_status = *"200"* ]] || {
      log error "Token retrieval failed - response was:"
      log error "${kms_get_response}"
      exit 1
  }

  echo "${kms_get_response}"
}

KMS_GET_RESPONSE=$(\
  retrieve_kms_unlimited_admin_token "${KMS_CREDENTIAL_TOKEN_ADMIN}" "${ADP_KMS_BASE_PATH}")

ADP_KMS_ADMIN_TOKEN=$(\
    grep --regexp='^{' <<< "${KMS_GET_RESPONSE}" \
    | tr ',' '\n' \
    | sed -n 's/"data":{"data":{"token":"\(.*\)"}/\1/p' \
)

log "Successfully retrieved KMS unlimited (root) admin client_token"

######################################################################################################################################################

# Create Policies
function create_kms_policy() {
    local kms_policy_name="${1}"
    local kms_policy_payload="${2}"
    local adp_kms_admin_token="${3}"
    local adp_kms_base_path="${4}"

    log "Creating \"${kms_policy_name}\" KMS policy."

    local kms_create_policy_response
    kms_create_policy_response=$(\
        curlKms --header "X-Vault-Token: ${adp_kms_admin_token}" \
        --request POST \
        "${adp_kms_base_path}/sys/policy/${kms_policy_name}" \
        --data "${kms_policy_payload}" \
    ) || {
        log error 'Error encountered creating policy in KMS (eric-sec-key-management):'
        log error "$kms_create_policy_response"
        exit 1
    }

    local create_policy_response
    create_policy_response=$(echo "${kms_create_policy_response}" | grep "HTTP/")
    [[ ${create_policy_response} = *"204"* ]] || {
        log error "Failed to create KMS policy - response was:"
        log error "${kms_create_policy_response}"
        exit 1
    }

    log "KMS policy ${kms_policy_name} created successfully."
}

ENCRYPTION_KEY_POLICY_NAME="encryption-key-policy"
ENCRYPT_POLICY_NAME="encrypt-policy"
DECRYPT_POLICY_NAME="decrypt-policy"

create_kms_policy \
    "${ENCRYPTION_KEY_POLICY_NAME}" \
    '{"policy":"path \"transit/keys/'"$ADP_KMS_ENCRYPTION_KEY_NAME"'\" { capabilities = [\"create\", \"update\"]}"}' \
    "${ADP_KMS_ADMIN_TOKEN}" \
    "${ADP_KMS_BASE_PATH}"

create_kms_policy \
    "${ENCRYPT_POLICY_NAME}" \
    '{"policy":"path \"transit/encrypt/'"$ADP_KMS_ENCRYPTION_KEY_NAME"'\" { capabilities = [\"create\", \"update\"]}"}' \
    "${ADP_KMS_ADMIN_TOKEN}" \
    "${ADP_KMS_BASE_PATH}"

create_kms_policy \
    "${DECRYPT_POLICY_NAME}" \
    '{"policy":"path \"transit/decrypt/'"$ADP_KMS_ENCRYPTION_KEY_NAME"'\" { capabilities = [\"update\"]}"}' \
    "${ADP_KMS_ADMIN_TOKEN}" \
    "${ADP_KMS_BASE_PATH}"

######################################################################################################################################################

# Create Role
function create_kms_role() {
    local kms_policy_names="${1}"
    local kms_role_name="${2}"
    local adp_kms_admin_token="${3}"
    local adp_kms_base_path="${4}"

    log "Creating \"${kms_role_name}\" KMS role."

    local service_account_namespace=$(< "$SUBSYSTEM_MANAGER_SERVICE_ACCOUNT_NAMESPACE") || {
      separator error
      log error "Error reading service account namespace in from $SUBSYSTEM_MANAGER_SERVICE_ACCOUNT_NAMESPACE."
      exit 1
    }

    local create_role_payload="{\"bound_service_account_names\":\"${SUBSYSTEM_MANAGER_SERVICE_ACCOUNT_NAME}\",
        \"bound_service_account_namespaces\":\"${service_account_namespace}\", \"token_policies\":\"${kms_policy_names}\", \"ttl\":\"1800000\" }"

    local kms_create_role_response
    kms_create_role_response=$(\
        curlKms --header "X-Vault-Token: ${adp_kms_admin_token}" \
        --request POST \
        "${adp_kms_base_path}/auth/kubernetes/role/${kms_role_name}" \
        --data "${create_role_payload}" \
    ) || {
        log error 'Error encountered creating role in KMS (eric-sec-key-management):'
        log error "${kms_create_role_response}"
        exit 1
    }

    local create_role_response
    create_role_response=$(echo "${kms_create_role_response}" | grep "HTTP/2")
    [[ "${create_role_response}" = *"204"* ]] || {
        log error "Failed to create KMS policy - response was:"
        log error "${kms_create_role_response}"
        exit 1
    }

    log "KMS role ${kms_role_name} created successfully."
}

ADP_KMS_ROLE_NAME="kms-crypto-role-${SUBSYSTEM_MANAGER_SERVICE_ACCOUNT_NAME}"

create_kms_role \
    "${ENCRYPTION_KEY_POLICY_NAME},${ENCRYPT_POLICY_NAME},${DECRYPT_POLICY_NAME}" \
    "${ADP_KMS_ROLE_NAME}" \
    "${ADP_KMS_ADMIN_TOKEN}" \
    "${ADP_KMS_BASE_PATH}"

######################################################################################################################################################

# Create Encryption Key

function create_encryption_key() {
  local kms_role_name="${1}"
  local adp_kms_admin_token="${2}"
  local kms_encryption_key_name="${3}"
  local adp_kms_base_path="${4}"

  log "Creating encryption key \"${kms_encryption_key_name}\" for ADP KMS role \"${kms_role_name}\"."

  local create_encryption_key_payload="{\"type\": \"aes256-gcm96\"}"

  local kms_create_encryption_key_response
  kms_create_encryption_key_response=$(\
      curlKms --header "X-Vault-Token: ${adp_kms_admin_token}" \
      --request POST \
      "${adp_kms_base_path}/transit/keys/${kms_encryption_key_name}" \
      --data "${create_encryption_key_payload}" \
  ) || {
      log error 'Error encountered creating encryption key in KMS (eric-sec-key-management):'
      log error "${kms_create_encryption_key_response}"
      exit 1
  }

    local create_encryption_key_response
    create_encryption_key_response=$(echo "${kms_create_encryption_key_response}" | grep "HTTP/2")
    [[ "${create_encryption_key_response}" = *"204"* ]] || {
        log error "Failed to create KMS encryption key - response was:"
        log error "${kms_create_encryption_key_response}"
        exit 1
    }

    log "KMS encryption key ${kms_encryption_key_name} created successfully."
}

create_encryption_key \
    "${ADP_KMS_ROLE_NAME}" \
    "${ADP_KMS_ADMIN_TOKEN}" \
    "${ADP_KMS_ENCRYPTION_KEY_NAME}" \
    "${ADP_KMS_BASE_PATH}"

#######################################################################################################################################################
