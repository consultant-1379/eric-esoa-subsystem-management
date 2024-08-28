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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
/**
 * The Class Constants
 */
public class Constants {
    public static final String $_NAME = "name";
    public static final String $_USERNAME = "username";
    public static final String $_PASSWORD = "password";
    public static final String $_TENANT = "tenant";
    public static final String $_CONNECTIONS = "connections";
    public static final String $_validSubsystemName = "validSubsystem";
    public static final String healthCheckTime = "t09/02/2018 10:00:01";
    public static final String subsystem_name = "subsystem_name";
    public static final String url = "/some/url/goes/here";
    public static final String $_SYBSYSTEM_TYPE = "type";
    public static final String URL_POSTFIX = "/subsystem-manager/v1";
    public static final String URL_POSTFIX_V2 = "/subsystem-manager/v2";
    public static final String HTTP_LOCALHOST = "http://localhost:";
    public static final String PORT_NUMBER_8080 = "8080";
    public static final String SUBSYSTEMS = "subsystems";
    public static final String CONNECTION_PROPERTIES = "connection-properties";
    public static final String $_ID = "$.id";
    public static final String $_SUBSYSTEM_USERS = "subsystemUsers";
    public static final String $_SUBSYSTEMUSERS_ID = "$.subsystemUsers[0].id";
    public static final String $_SUBSYSTEMUSERS_CONNPROPS_ID = "$.subsystemUsers[0].connectionPropsId";
    public static final String CONNECTION_PROPERTIES_ID = "connectionPropertiesId";
    public static final String $_AUTH_URL = $_NAME;
    public static final String AUTH_URL = "/token";
    public static final String AUTH_HEADERS = "{\"Content-Type\": \"application/json\"}";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "client-secret";
    public static final String GRANT_TYPE = "client-credentials";

    public static final Long _1 = 1L;
    public static final Long _2 = 2L;
    public static final Long _3 = 3L;
    public static final Long subsystemTypeId = 33L;

    public static final String $_INTERNAL_ERROR_CODE = "$.errorCode";
    public static final String $_ERROR_DATA = "$.errorData";
    public static final String $_USER_MESSAGE = "$.userMessage";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ERROR_MESSAGE = "Error message";
    public static final String ERROR_DATA = "1";
    public static final String $_ERROR_MESSAGE = "$.errorData";

    public static final String WRONG_ERROR_CODE_GENERIC = "SSM-Z-99";
    public static final String MALFORMED_CONTENT_ERROR_CODE = "SSM-B-26";

    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;

    public static final String ZERO_ID = "[0].id";
    public static final String ZERO_SUBSYSTEMID = "[0].subsystemId";
    public static final String ZERO_NAME = "[0].name";
    public static final String ZERO_USERNAME = "[0].username";
    public static final String ZERO_PASSWORD = "[0].password";
    public static final String ZERO_SUBSYSTEMUSERS = "[0].subsystemUsers";
    public static final String ZERO_SUBSYSTEMUSERS_ANDID = "[0].subsystemUsers[0].id";
    public static final String ZERO_SUBSYSTEMUSERS_AND_CONNPROPSID = "[0].subsystemUsers[0].connectionPropsId";

    public static final String GREETING_MESSAGE = "Welcome to Subsystem Management Service";
    public static final String URL_PREFIX = "/subsystem-manager/v1";
    public static final String URL_PREFIX_V2 = "/subsystem-manager/v2";
    public static final String SUBSYSTEMS_URL = "/subsystems";
    public static final String CONN_PROP_URL = "/connection-properties";
    public static final String SUBSYSTEM_USER_URL = "/subsystem-users";
    public static final String SUBSYSTEM_PAGINATION = "/subsystems";
    public static final String PAGINATION_DETAILS_URL =
            "?offset=0&limit=100&sortAttr=name&sortDir=asc&filters=%7B%22connectionProperties.name%22%3A%22connProp%22%7D";

    public static final String SUBSYSTEM_ID = "1";
    public static final String SUBSYSTEM_ONE_NAME = "subsystem_one";
    public static final String SUBSYSTEM_ONE_HEALTH_CHECK_TIME = "health_check";
    public static final String SUBSYSTEM_ONE_URL = "url";
    public static final String SUBSYSTEMTYPE_ONE_TYPE = "type";
    public static final String SUBSYSTEMTYPE_API_KEY = "ea5a2045-225f-4819-87d9-9bc388639354";
    public static final String $0_SUBSYSTEM_ONE_NAME = "$[0].name";
    public static final String $0_SUBSYSTEM_ONE_ID = "$[0].id";
    public static final String $0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME = "$[0].healthCheckTime";
    public static final String $0_SUBSYSTEM_ONE_URL = "$[0].url";
    public static final String $0_SUBSYSTEM_ONE_TYPEID = "$[0].subsystemTypeId";
    public static final String $0_SUBSYSTEM_ONE_STATE = "$[0].operationalState";
    public static final String $0_SUBSYSTEM_ONE_API_KEY = "$[0].apiKey";
    public static final String $_SUBSYSTEM_NAME = "$.name";
    public static final String $_SUBSYSTEM_ID = "$.id";
    public static final String $_SUBSYSTEM_HEALTH_CHECK_TIME = "$.healthCheckTime";
    public static final String $_SUBSYSTEM_URL = "$.url";
    public static final String $_SUBSYSTEM_TYEPID = "$.subsystemTypeId";
    public static final String $_SUBSYSTEM_STATE = "$.operationalState";
    public static final String SUBSYSTEM_UPDATE_NAME = "subsystem_two";
    public static final String SUBSYSTEM_DUPLICATE_NAME = "subsystem-duplicate-name";

    public static final Long SUBSYSTEMTYPE_ONE_ID = 1L;
    public static final Long SUBSYSTEM_USER_CONNPROP_ID = 1L;
    public static final Long SUBSYSTEM_USER_ID = 1L;
    public static final Long SUBSYSTEM_ONE_ID = 1L;

    public static final OperationalState SUBSYSTEM_ONE_STATE = OperationalState.REACHABLE;
    public static final List<ConnectionProperties> SUBSYSTEM_ONE_CONNPROP = new ArrayList<ConnectionProperties>();
    public static final ConnectionProperties SUBSYSTEM_USER_CONNPROP = null;

    public static final String TENANT_NAME = "tenant-1";
    public static final String MALFORMED_CONTENT_DEVELOPER_MSG = "Malformed service request.";
    public static final String SUBSYSTEM_NOT_EXIST_DEVELOPER_MSG = "The specified Subsystem does not exist.";
    public static final String $_CONNPROP_ID = "$.connectionPropsId";

    public static final Integer PAGE_OFFSET = 0;
    public static final Integer PAGE_LIMIT = 100;
    public static final String SORT_ATTR = $_NAME;
    public static final String SORT_DIR = "asc";
    public static final String TOTAL = "total";

    public static final String FILTERED_ID = "id";
    public static final String FILTERED_NAME = $_NAME;
    public static final String FILTERED_NOT_EXIST = "something";
    public static final String SELECT_URL = "?select=";
    public static final String SUBSYSTEM_NAME = $_NAME;
    public static final String FIELDS = "name,id";
    public static final String FILTER_CONNPROP = "%7B%22connectionProperties.name%22%3A%22connProp%22%7D";
    public static final Integer FILTERED_ID_1 = 1;
    public static final Integer FILTERED_ID_2 = 2;

    public static final String CONN_PROP_NAME_1 = "connPropOne";
    public static final String CONN_PROP_USERNAME_1 = "username_1";
    public static final String CONN_PROP_PASSWORD_1 = "password_1";
    public static final String CONN_PROP_TENANT_1 = "tenant_1";
    public static final Long CONN_PROP_SUBSYSTEM_ID = 9L;
    public static final Long CONN_PROP_ID_1 = 10L;

    public static final String $0_CONN_PROP_NAME = "$[0].connectionProperties[0].name";
    public static final String $0_CONN_PROP_ID = "$[0].connectionProperties[0].id";
    public static final String $0_CONN_PROP_USERNAME = "$[0].connectionProperties[0].username";
    public static final String $0_CONN_PROP_PASSWORD = "$[0].connectionProperties[0].password";
    public static final String $0_CONN_PROP_SUBSYSTEM_ID = "$[0].connectionProperties[0].subsystemId";

    public static final String SERIALIZED_CONN_PROPS_JSON =
            "{\"id\":1,\"subsystemId\":1,\"name\":\"name\",\"username\":\"username\",\"password\":\"password\","
            + "\"subsystemUsers\":[{\"id\":2,\"connectionPropsId\":1}]}";
    public static final String SERIALIZED_SUBSYSTEM_JSON =
            "{\"id\":1,\"subsystemTypeId\":1,\"name\":\"subsystem_one\",\"healthCheckTime\":\"$[0].healthCheckTime\","
            + "\"url\":\"url\",\"operationalState\":\"REACHABLE\",\"connectionProperties\":[]}";
    public static final String SERIALIZED_UPDATED_SUBSYSTEM_JSON =
            "{\"id\":1,\"subsystemTypeId\":1,\"name\":\"subsystem_two\",\"healthCheckTime\":\"$[0].healthCheckTime\","
            + "\"url\":\"url\",\"operationalState\":\"REACHABLE\",\"connectionProperties\":[]}";
    public static final String SERIALIZED_CONN_PROPS_LIST_JSON =
            "[{\"id\":1,\"subsystemId\":1,\"name\":\"name\",\"username\":\"username\",\"password\":\"password\","
            + "\"subsystemUsers\":[{\"id\":2,\"connectionPropsId\":1}]}]";
    public static final String SERIALIZED_SUBSYSTEM_LIST_JSON =
            "[{\"id\":1,\"subsystemTypeId\":1,\"name\":\"subsystem_one\",\"healthCheckTime\":\"health_check\","
            + "\"url\":\"url\",\"operationalState\":\"REACHABLE\",\"connectionProperties\":"
            + "[{\"id\":10,\"subsystemId\":9,\"name\":\"connPropOne\",\"username\":\"username_1\",\"password\":\"password_1\"}]}]";
    public static final String SERIALIZED_SUBSYSTEM_LIST_JSON_V2 =
            "[{\"id\":1,\"apiKey\":\"ea5a2045-225f-4819-87d9-9bc388639354\",\"name\":\"subsystem_one\","
            + "\"healthCheckTime\":\"health_check\",\"url\":\"url\",\"operationalState\":\"REACHABLE\","
            + "\"connectionProperties\":[{\"id\":10,\"subsystemId\":9,\"name\":\"connPropOne\","
            + "\"username\":\"username_1\",\"password\":\"password_1\"}]}]";
    public static final String SERIALIZED_FILTERED_SUBSYSTEM = "[{\"id\":1,\"name\":\"subsystem_one\"}]";

    public static final String DOMAIN_MANAGER_2 = "DomainManager2";
    public static final String INVALID_SUBSYSTEM_ID = "9999";
    public static final String STRING_WITH_256_CHARS =
            "ICWDnkokCdSS4bvQnl55MYDf7h5jKtZmdhMQwSFplgWyLy6iwNh0fvpEdIFGPqoRYrpo2Zl5"
            + "nPdqpV7obofArWbM0Qkm5uYiHNLkGNwmi3pisxe8XGhd1W68nHtf84600sAbkcb5xzfBeDg7"
            + "CnXU2YV2pMQYIsQ8cxDHZugoev2C209ab79udnO1uSTyMfCKsedVeBw4tAXF5Qf4uW90TvbB"
            + "YZhUUpFVdnqg5ZNPvUclqPjVNqhs4F6YjmvGyahR";
    public static String $_SLASH="/";
    public static String $_BACKSLASH="\"";
    public static String $_FBSLASH= "\":\"";
    public static final String $_COMMA = ",";
    public static String $_123= "123";
    public static String $_1231= "1231";
    public static String $_2111= "2111";
    public static final String BUILT="Built {}.";
    public static final String TEST ="TEST";
    public static final String USERFORPUT= "userForPut";
    public static final String TESTFORPUT= "testForPut";
    public static final String AUTH_HEADERS_VAL="{\\\"Content-Type\\\":\\\"application/json\\\","
            + "\\\"Accept\\\":\\\"*/*\\\",\\\"X-login\\\":\\\"eo-user\\\",\\\"X-password\\\":\\\"Ericsson@12345\\\"}\"";
    public static final String SSM ="SSM-B-45";
    public static final String USER_FOR_TEST="userForTest";
    public static final String TEST_FOR_TEST ="testForTest";
    public static final String TENANTW_FOR_TEST="tenantForTest";
    public static final String DOMAIN_MANAGER="DomainManager";
    public static final String CISCO="cisco";
    public static final String NEWNAME= "NewName";
    public static final String TENANT_FOR_TEST= "tenantForTest";
    public static final String ENM= "enm";
    public static final String  SUBSYSTEM_TYPE="$.subsystemType.type";
    public static final String SUBTYPE = "subtype";
    public static final String OPERATOR ="Operator";
    public static final String AIRTEL="Airtel";
    public static final String VODAFONE="Vodafone";
    public static final String IDEA="Idea";
    public static final String UTF_8="UTF-8";
    public static final String ESO_SECURITY ="/eso-security";
    public static final String TESTPASSWORD="testpassword";
    public static final String TESTUSER="testuser";
    public static final String OUTH_2="oauth2";
    public static final String AUTHV1="/auth/v1";
    public static final String NEW_SUBSYSTEM_TYPE="new-subsystem-type";

}
