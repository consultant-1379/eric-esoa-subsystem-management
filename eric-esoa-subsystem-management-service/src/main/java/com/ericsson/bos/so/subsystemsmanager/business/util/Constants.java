/*******************************************************************************
 * COPYRIGHT Ericsson 2023-2024
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.bos.so.subsystemsmanager.business.util;


/**
 * The Class Constants.
 */
public class Constants {

    public static final String SUBSYSTEM_MANAGER = "/subsystem-manager";
    public static final String SUBSYSTEMS = "/subsystems";
    public static final String V1 = SUBSYSTEM_MANAGER + "/v1";
    public static final String V2 = SUBSYSTEM_MANAGER + "/v2";
    public static final String GREETING_MESSAGE = "Welcome to Subsystem Management Service";
    public static final String TENANT_NAME = "Tenant-1";
    public static final String TENANT_QUERY_PARAM = "tenantName";

    public static final String URL = "url";

    public static final String DEFAULT_PAGE_OFFSET = "0";
    public static final String DEFAULT_PAGE_LIMIT = "100";
    public static final String DEFAULT_SORT_ATTR = "name";
    public static final String DEFAULT_SORT_DIR = "asc";
    public static final String TOTAL = "total";

    public static final String VENDOR = "vendor";
    public static final String VENDOR_NAME = "Ericsson";
    public static final String NAME = "name";

    public static final String AUTH_URL = "auth_url";
    public static final String AUTH_BODY = "auth_body";
    public static final String AUTH_HEADERS = "auth_headers";
    public static final String AUTH_KEY = "auth_key";
    public static final String AUTH_TYPE = "auth_type";
    public static final String TOKEN_REF = "token_ref";
    // aws
    public static final String ACCESS_KEY_ID = "access_key_id";
    public static final String SECRET_ACCESS_KEY = "secret_access_key";

    public static final String ENCRYPT_ALL = "*";
    public static final String ENCRYPTED = "encrypted";
    public static final String PROPERTIES = "properties";
    public static final String KEY = "key";
    public static final String ENCRYPTED_KEYS = "encryptedKeys";
    public static final String PW = "password";
    public static final String DOMAIN_MANAGER = "DomainManager";
    public static final String NFVO = "NFVO";
    public static final String TEST_NFVO = "testNfvo";

    public static final String REGEX_CORRECT_KEY_LENGTH = "[\\S]{1,255}";
    public static final String REGEX_CORRECT_VALUE_LENGTH ="[\\S]{1,1024}";
    public static final String VALUE = "value";
    public static final String SUBSYSTEM_ID = "subsystemId";
    public static final String SUBSYSTEM_TYPE_ID = "subsystemTypeId";
    public static final String PATCHED_USERNAME = "patched-username";
    public static final String USERNAME = "username";
    public static final String TOKEN = "token";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String ID = "id";
    public static final String NEW_PROPERTY = "new-property";
    public static final String NEW_PROPERTY1 = "NewProperty";
    public static final String OLD_USERNAME = "old-username";
    public static final String NEW_KEY = "newKey";
    public static final String NEW_VALUE = "newValue";
    public static final String ADAPTER_LINK_NAME = "adapterLink";

    public static final String SUBSYSTEM_TYPE_FILTER_NAME = "subsystemType";
    public static final String CONNECTION_PROPERTIES = "connectionProperties";
    public static final String SUBSYSTEM_ADMIN_USER= "ESOA_SubsystemAdmin";

    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "client_secret";
    public static final String CLIENT_ID = "client_id";
    public static final String OAUTH2_CLIENT_CREDENTIALS = "Oauth2ClientCredentials";
    public static final String AUTHENTICATION_SYSTEMS = "AuthenticationSystems";
    public static final String SUBSYSTEM_CONNECTION_PROPERTY_USERNAME = "username";
    public static final String SUBSYSTEM_CONNECTION_PROPERTY_PASSWORD = "password";
    public static final String OPERATIONAL_STATE = "operationalState";
    public static final String SUBTYPE = "subtype";

    // external system auth type
    public static final String SUBSYSTEM_AUTH_TYPE_BEARER = "Bearer";
    public static final String SUBSYSTEM_AUTH_TYPE_BASIC_AUTH = "BasicAuth";
    public static final String SUBSYSTEM_AUTH_TYPE_BASIC_AUTH_TOKEN = "BasicAuthToken";
    public static final String SUBSYSTEM_AUTH_TYPE_COOKIE = "Cookie";
    public static final String SUBSYSTEM_AUTH_TYPE_NO_AUTH = "NoAuth";
    public static final String SUBSYSTEM_AUTH_TYPE_API_KEY = "ApiKey";
    public static final String SUBSYSTEM_AUTH_TYPE_CAI3G = "Cai3g";
    public static final String SUBSYSTEM_AUTH_TYPE_AWS = "Aws";

    public static final String TLS_WEBCLIENT = "TLS_WEBCLIENT";

    public static final String EXECUTE_CONNECTIVITY_CHECK = "executeConnectivityCheck";

    private Constants() {
    }
}