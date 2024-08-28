{{/*
Create release name used for cluster role.
*/}}
{{- define "eric-esoa-subsystem-management.release.name" -}}
{{- default .Release.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Expand the name of the Kubernetes service account associated with Subsystem Management.
The service account will be referred to by name when accessing the ADP KMS
microservice - this template is a single point of definition to keep that name
consistent when creating the account and everywhere Subsystem Management uses it afterward.
*/}}
{{- define "eric-esoa-subsystem-management.service-account.name" -}}
{{- template "eric-esoa-so-library-chart.name" . -}}
{{- end -}}

{{/*
Expand the name of the Kubernetes secret associated with the service account.
Information from the secret is used when accessing the ADP KMS microservice
- this template is a single point of definition to ensure the secret can be
predictably mounted to pods.
*/}}
{{- define "eric-esoa-subsystem-management.service-account-secret.name" -}}
{{- template "eric-esoa-subsystem-management.service-account.name" . -}}-token
{{- end -}}

{{/*
Define the variable for the ADP KMS CA cert Kubernetes secret file location.
*/}}
{{- define "eric-esoa-subsystem-management.adp-kms-cacert.file-location" -}}
  {{- $runSecretsFilePath := print .Values.kms.certificate.cacertVolumeName "/" .Values.kms.certificate.cacertFilePath -}}
  {{- template "eric-esoa-subsystem-management.run-secrets-file-path" (dict "runSecretsFilePath" $runSecretsFilePath) -}}
{{- end -}}

{{/*
Define the variable for the ADP KMS cacert Kubernetes secret volume location.
*/}}
{{- define "eric-esoa-subsystem-management.adp-kms-certificate.volume-location" -}}
  {{- $runSecretsFilePath := print .Values.kms.certificate.cacertVolumeName }}
  {{- template "eric-esoa-subsystem-management.run-secrets-file-path" (dict "runSecretsFilePath" $runSecretsFilePath) -}}
{{- end -}}

{{/*
Define the variable for the ADP KMS admin account Kubernetes secret file location.
*/}}
{{- define "eric-esoa-subsystem-management.adp-kms-admin-account.file-location" -}}
  {{- $runSecretsFilePath := include "eric-esoa-subsystem-management.kms-admin-account.name" . }}
  {{- template "eric-esoa-subsystem-management.run-secrets-file-path" (dict "runSecretsFilePath" $runSecretsFilePath) -}}
{{- end -}}

{{/*
Define the variable for the Subsystem Management service account Kubernetes secret file location.
*/}}
{{- define "eric-esoa-subsystem-management.service-account-secret.file-location" -}}
  {{- $runSecretsFilePath := include "eric-esoa-subsystem-management.service-account-secret.name" . }}
  {{- template "eric-esoa-subsystem-management.run-secrets-file-path" (dict "runSecretsFilePath" $runSecretsFilePath) -}}
{{- end -}}

{{/*
Define Kubernetes secrets in the run/secrets directory of the Subsystem Management file
structure, with a defined path.
*/}}
{{- define "eric-esoa-subsystem-management.run-secrets-file-path" -}}
  {{- $runSecretsFilePath := index . "runSecretsFilePath" -}}
  {{- printf "/run/secrets/%s" $runSecretsFilePath | quote -}}
{{- end -}}

{{/*
Expand the name of the Kubernetes ConfigMap used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.adp-kms-access-config-map.name" -}}
{{- template "eric-esoa-so-library-chart.name" . -}}-kms-access-configmap
{{- end -}}

{{/*
Expand the volume of the Kubernetes ConfigMap Volume used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.adp-kms-access-config-map-volume.name" -}}
{{- template "eric-esoa-subsystem-management.adp-kms-access-config-map.name" . -}}-volume
{{- end -}}

{{/*
Expand the name of the volume containing the Kubernetes secret file used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.kms-certificate-cacert-volume.name" }}
{{- if .Values.kms -}}
  {{- if .Values.kms.certificate -}}
    {{- if .Values.kms.certificate.cacertVolumeName -}}
      {{- .Values.kms.certificate.cacertVolumeName | toString -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Expand the name of the secret key used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.kms-certificate-cacert-key.name" }}
{{- if .Values.kms -}}
  {{- if .Values.kms.certificate -}}
    {{- if .Values.kms.certificate.cacertKey -}}
      {{- .Values.kms.certificate.cacertKey | toString -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Expand the file path of the secret key used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.kms-certificate-cacert-key.filepath" }}
{{- if .Values.kms -}}
  {{- if .Values.kms.certificate -}}
    {{- if .Values.kms.certificate.cacertFilePath -}}
      {{- .Values.kms.certificate.cacertFilePath | toString -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Expand the name of the ADP KMS administrative account used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.kms-admin-account.name" }}
{{- if .Values.kms -}}
  {{- if .Values.kms.adminAccountName -}}
    {{- .Values.kms.adminAccountName | toString -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Expand the name of the root token used for SIP-TLS communication with KMS.
*/}}
{{- define "eric-esoa-subsystem-management.sip-tls-root-token.name" }}
{{- if .Values.kms -}}
  {{- if .Values.kms.sipTlsTrustedRootTokenName -}}
    {{- .Values.kms.sipTlsTrustedRootTokenName | toString -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Expand the name of the script name used to obtain KMS access.
*/}}
{{- define "eric-esoa-subsystem-management.obtain-kms-access-script.file-name" }}
{{- if .Values.kms -}}
  {{- if .Values.kms.scriptFileName -}}
    {{- .Values.kms.scriptFileName | toString -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create image registry url
*/}}
{{- define "eric-esoa-subsystem-management.registryUrl" -}}
{{- if .Values.imageCredentials.registry -}}
  {{- if .Values.imageCredentials.registry.url -}}
    {{- print .Values.imageCredentials.registry.url -}}
  {{- else if .Values.global.registry.url -}}
    {{- print .Values.global.registry.url -}}
  {{- else -}}
    ""
  {{- end -}}
{{- else if .Values.global.registry.url -}}
  {{- print .Values.global.registry.url -}}
{{- else -}}
  ""
{{- end -}}
{{- end -}}

{{/*
Enable Node Selector functionality
*/}}
{{- define "eric-esoa-subsystem-management.nodeSelector" -}}
{{- if .Values.global.nodeSelector }}
nodeSelector:
  {{ toYaml .Values.global.nodeSelector | trim }}
{{- else if .Values.nodeSelector }}
nodeSelector:
  {{ toYaml .Values.nodeSelector | trim }}
{{- end }}
{{- end -}}

{{/*
Support user defined labels (DR-D1121-068)
*/}}
{{- define "eric-esoa-subsystem-management.user-labels" }}
{{- if .Values.labels }}
{{ toYaml .Values.labels }}
{{- end }}
{{- end }}

{{/*
Create a map from global values with defaults if not in the values file.
*/}}
{{ define "eric-schema-registry-sr.globalMap" }}
  {{- $globalDefaults := dict "security" (dict "policyBinding" (dict "create" false)) -}}
  {{- $globalDefaults := merge $globalDefaults (dict "security" (dict "policyReferenceMap" (dict "default-restricted-security-policy" "default-restricted-security-policy"))) -}}
  {{ if .Values.global }}
    {{- mergeOverwrite $globalDefaults .Values.global | toJson -}}
  {{ else }}
    {{- $globalDefaults | toJson -}}
  {{ end }}
{{ end }}

{{/*
The name of the cluster role used during openshift deployments.
This helper is provided to allow use of the new global.security.privilegedPolicyClusterRoleName if set, otherwise
use the previous naming convention of <release_name>-allowed-use-privileged-policy for backwards compatibility.
*/}}
{{- define "eric-esoa-subsystem-management.privileged.cluster.role.name" -}}
  {{- if hasKey (.Values.global.security) "privilegedPolicyClusterRoleName" -}}
    {{ .Values.global.security.privilegedPolicyClusterRoleName }}
  {{- else -}}
    {{ template "eric-esoa-subsystem-management.release.name" . }}-allowed-use-privileged-policy
  {{- end -}}
{{- end -}}

{{/* DR-D1126-005
This function takes (dict "Values" .Values "resourceName" "i.e:eric-esoa-subsystem-management") as parameter
And render the resource attributes (requests and limits)
* Values to access .Values
* resourceName to help access the specific resource from .Values.resources
*/}}
{{- define "eric-esoa-subsystem-management.resourcesHelper" -}}
requests:
  cpu: {{ index .Values "resources" "eric-esoa-subsystem-management" "requests" "cpu" | quote }}
  memory: {{ index .Values "resources" "eric-esoa-subsystem-management" "requests" "memory" | quote }}
  {{- if index .Values "resources" "eric-esoa-subsystem-management" "requests" "ephemeral-storage" }}
  ephemeral-storage: {{ index .Values "resources" "eric-esoa-subsystem-management" "requests" "ephemeral-storage" | quote }}
  {{- end }}
limits:
  cpu: {{ index .Values "resources" "eric-esoa-subsystem-management" "limits" "cpu" | quote }}
  memory: {{ index .Values "resources" "eric-esoa-subsystem-management" "limits" "memory" | quote }}
  {{- if index .Values "resources" "eric-esoa-subsystem-management" "limits" "ephemeral-storage" }}
  ephemeral-storage: {{ index .Values "resources" "eric-esoa-subsystem-management" "limits" "ephemeral-storage" | quote }}
  {{- end }}
{{- end -}}

{{/*
init keycloak-client container resource requests and limits
*/}}
{{- define "eric-esoa-subsystem-management.KCresources" -}}
requests:
  cpu: {{ index .Values "resources" "keycloak-client" "requests" "cpu" | quote }}
  memory: {{ index .Values "resources" "keycloak-client" "requests" "memory" | quote }}
  {{- if index .Values "resources" "keycloak-client" "requests" "ephemeral-storage" }}
  ephemeral-storage: {{ index .Values "resources" "keycloak-client" "requests" "ephemeral-storage" | quote }}
  {{- end }}
limits:
  cpu: {{ index .Values "resources" "keycloak-client" "limits" "cpu" | quote }}
  memory: {{ index .Values "resources" "keycloak-client" "limits" "memory" | quote }}
  {{- if index .Values "resources" "keycloak-client" "limits" "ephemeral-storage" }}
  ephemeral-storage: {{ index .Values "resources" "keycloak-client" "limits" "ephemeral-storage" | quote }}
  {{- end }}
{{- end -}}

{{/*
init pgInitContainer container resource requests and limits
*/}}
{{- define "eric-esoa-subsystem-management.pgInitContainer" -}}
requests:
  cpu: {{ index .Values "resources" "pgInitContainer" "requests" "cpu" | quote }}
  memory: {{ index .Values "resources" "pgInitContainer" "requests" "memory" | quote }}
  {{- if index .Values "resources" "pgInitContainer" "requests" "ephemeral-storage" }}
  ephemeral-storage: {{ index .Values "resources" "pgInitContainer" "requests" "ephemeral-storage" | quote }}
  {{- end }}
limits:
  cpu: {{ index .Values "resources" "pgInitContainer" "limits" "cpu" | quote }}
  memory: {{ index .Values "resources" "pgInitContainer" "limits" "memory" | quote }}
  {{- if index .Values "resources" "pgInitContainer" "limits" "ephemeral-storage" }}
  ephemeral-storage: {{ index .Values "resources" "pgInitContainer" "limits" "ephemeral-storage" | quote }}
  {{- end }}
{{- end -}}

{{/*
    Define Image Pull Policy
*/}}
{{- define "eric-esoa-subsystem-management.registryImagePullPolicy" -}}
    {{- $globalRegistryPullPolicy := "IfNotPresent" -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.imagePullPolicy -}}
                {{- $globalRegistryPullPolicy = .Values.global.registry.imagePullPolicy -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- if .Values.imageCredentials.mainImage.registry -}}
            {{- if .Values.imageCredentials.mainImage.registry.imagePullPolicy -}}
            {{- $globalRegistryPullPolicy = .Values.imageCredentials.mainImage.registry.imagePullPolicy -}}
            {{- end -}}
    {{- end -}}
    {{- print $globalRegistryPullPolicy -}}
{{- end -}}


{{- define "eric-esoa-subsystem-management.fsGroup.coordinated" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.fsGroup -}}
      {{- if .Values.global.fsGroup.manual -}}
        {{ .Values.global.fsGroup.manual }}
      {{- else -}}
        {{- if eq .Values.global.fsGroup.namespace true -}}
          # The 'default' defined in the Security Policy will be used.
        {{- else -}}
          10000
      {{- end -}}
    {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
{{- end -}}


{{/*
Seccomp profile section (DR-1123-128)
*/}}
{{- define "eric-esoa-subsystem-management.seccomp-profile" }}
    {{- if .Values.seccompProfile }}
      {{- if .Values.seccompProfile.type }}
          {{- if eq .Values.seccompProfile.type "Localhost" }}
              {{- if .Values.seccompProfile.localhostProfile }}
seccompProfile:
  type: {{ .Values.seccompProfile.type | quote }}
  localhostProfile: {{ .Values.seccompProfile.localhostProfile | quote }}
            {{- end }}
          {{- else }}
seccompProfile:
  type: {{ .Values.seccompProfile.type | quote }}
          {{- end }}
        {{- end }}
      {{- end }}
{{- end }}

# DR-D1123-135 ==> supplementalGroups
{{/*
    Define supplementalGroups
*/}}
{{- define "eric-esoa-subsystem-management.supplementalGroups" -}}
    {{- if .Values.global.podSecurityContext -}}
      {{- if .Values.global.podSecurityContext.supplementalGroups -}}
          {{- if .Values.podSecurityContext -}}
              {{- if .Values.podSecurityContext.supplementalGroups -}}
                  {{ toYaml (concat .Values.global.podSecurityContext.supplementalGroups .Values.podSecurityContext.supplementalGroups) | nindent 8}}
              {{- end }}
          {{- else }}
              {{ toYaml .Values.global.podSecurityContext.supplementalGroups | nindent 8}}
          {{- end -}}
      {{- end }}
    {{- else if .Values.podSecurityContext }}
        {{- if .Values.podSecurityContext.supplementalGroups -}}
            {{ toYaml .Values.podSecurityContext.supplementalGroups | nindent 8}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{/*
This helper get the custom user used to connect to postgres DB instance.
*/}}
{{ define "eric-esoa-subsystem-management.dbuser" }}
  {{- $secret := (lookup "v1" "Secret" .Release.Namespace "eric-eo-database-pg-secret") -}}
  {{- if $secret -}}
    {{ index $secret.data "custom-user" | b64dec | quote }}
  {{- else -}}
    {{- (randAlphaNum 16) | b64enc | quote -}}
  {{- end -}}
{{- end -}}

{{/*
Define the init container which will create the DATABASE object for the microservice in the database

The required helm values for this template:
1. .Values.database.host - the hostname of the database instance
2. .Values.database.port - the port of the database instance
3. .Values.database.dbName - the name of the database
4. .Values.database.secret - the name of the secret which contains the database credentials
5. .Values.database.dbaUserkey - the key of the item in the secret which contains the DB admin user username
6. .Values.database.dbaPasswdkey - the key of the item in the secret which contains the DB admin user password
7. .Values.database.sslEnabled - whether ssl is enabled or not
The optional helm values for this template:
1. .Values.database.creationTime - how long retry for before failing the init container, defaults to 30
2. .Values.database.pathToServerCert - server CA certificate secret mount point if ssl is enabled, defaults to "/mnt/ssl/server/"
*/}}
{{- define "eric-esoa-subsystem-management.create-database-init-container" -}}
{{- if .Values.global.createDB }}
- name: {{ .Chart.Name }}-create-database
  image: {{ template "eric-esoa-so-library-chart.imagePath" (dict "imageId" "pgInitContainer" "values" .Values "files" .Files) }}
  env:
    - name: "POSTGRES_HOST"
      value: {{ .Values.database.host | quote }}
    - name: "POSTGRES_USER"
      valueFrom:
        secretKeyRef:
          name: {{ .Values.database.secret }}
          key: {{ .Values.database.dbaUserkey }}
    - name: "PGPASSWORD"
      valueFrom:
        secretKeyRef:
          name: {{ .Values.database.secret }}
          key: {{ .Values.database.dbaPasswdkey }}
    - name: "POSTGRES_DB"
      value: {{ .Values.database.dbName | quote}}
    - name: "POSTGRES_SCHEMA"
      value: {{ .Values.database.schemaName | quote}}
    - name: "POSTGRES_PORT"
      value: {{ .Values.database.port | quote}}
    - name: SSL_PARAMETERS
      value: {{ include "eric-esoa-so-library-chart.sslParameters" . | quote }}
    - name: "STARTUP_WAIT"
      value: {{ default 30 .Values.database.creationTime | quote}}
    - name: TZ
      value: {{ .Values.global.timezone }}
  {{- if ( include "eric-esoa-so-library-chart.ssl-enabled" . ) }}
  volumeMounts:
{{ include "eric-esoa-so-library-chart.edb-server-cert-volume-mount" . | indent 2 }}
{{ include "eric-esoa-subsystem-management.pg-dba-client-cert-volume-mount" . | indent 2 }}
  {{- end }}
{{- end -}}
{{- end -}}

{{- define "eric-esoa-subsystem-management.pg-dba-client-cert-volume-mount" -}}
- name: pg-dba-client-cert-volume
  mountPath: {{ include "eric-esoa-so-library-chart._value-path-to-client-cert" . | quote }}
{{- end -}}

{{- define "eric-esoa-subsystem-management.pg-dba-client-cert-volume" -}}
{{- if .Values.global.createDB }}
- name: pg-dba-client-cert-volume
  secret:
    items:
    - key: {{ include "eric-esoa-so-library-chart._value-client-cert-secret-item-cert-key" . | quote }}
      path: {{ include "eric-esoa-so-library-chart._value-relative-path-to-client-cert" . | quote }}
    - key: {{ include "eric-esoa-so-library-chart._value-client-cert-secret-item-key-key" . | quote }}
      path: {{ include "eric-esoa-so-library-chart._value-relative-path-to-client-key" . | quote }}
    secretName: {{ default (printf "%s-%s" .Values.database.host "postgres-cert") .Values.database.dbaClientCertSecret | quote }}
{{- end -}}
{{- end -}}
