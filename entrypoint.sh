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

######################################################################################################################################################

# Logs a formatted message to the console.
function adp_log() {
  local msg
  msg="$(echo "$@" | sed 's|"|\\"|g' | tr --delete '\n')"

  printf '{"version":"0.3.0", "timestamp":"%s", "severity":"debug", "service_id":"%s", "message":"%s"}\n' \
    "$(date --iso-8601=seconds)" "$SERVICE_ID" "${msg}"
}

######################################################################################################################################################

function add_kms_ca_cert_to_keystore() {
  local current_directory
  current_directory="$(pwd)"

  local individual_certs_directory
  individual_certs_directory="/tmp/individualCerts"

  [[ -d  ${individual_certs_directory} ]] || mkdir ${individual_certs_directory}

  cd "${individual_certs_directory}"

  local file_count
  file_count="$(csplit -f individual- "${ADP_KMS_CACERT_FILE_PATH}"  '/-----BEGIN CERTIFICATE-----/' '{*}' --elide-empty-files | wc --lines)"

  echo "Number of certs for KMS in ${ADP_KMS_CACERT_FILE_PATH} bundle is ${file_count}."

  for cafile in $(ls)
  do
    adp_log "Adding siptlsca-${cafile} to java keystore ${DEFAULT_JAVA_CACERTS}."
    local output
    output="$(keytool -storepass "${JAVA_KEYSTORE_PW}" -noprompt -trustcacerts -importcert \
                      -file "${cafile}" -alias "siptlsca-${cafile}" -keystore "${DEFAULT_JAVA_CACERTS}" 2>&1)" || {
      adp_log "keytool error: ${output}"
      exit 1
    }
    adp_log "keytool: ${output}"
  done

  cd "${current_directory}" && rm -rf "${individual_certs_directory}"
}

# Check if the KMS CACERT FILE exists. If it exists, then it is added to the existing Java KeyStore.
if [[ -f  ${ADP_KMS_CACERT_FILE_PATH} ]];
then
  add_kms_ca_cert_to_keystore
fi

######################################################################################################################################################

exec java ${JAVA_OPTS} \
    -Dtomcat.util.http.parser.HttpParser.requestTargetAllow={} \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.config.additional-location=optional:$SPRING_CONFIG_CUSTOM_LOCATIONS \
    -jar /subsystem-management.jar
