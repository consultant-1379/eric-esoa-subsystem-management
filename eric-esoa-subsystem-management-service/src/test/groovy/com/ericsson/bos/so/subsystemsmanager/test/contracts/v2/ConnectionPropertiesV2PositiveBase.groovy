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
package com.ericsson.bos.so.subsystemsmanager.test.contracts.v2

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties
import com.ericsson.bos.so.subsystemsmanager.test.contracts.util.MockResponseFormatter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference

import io.restassured.module.mockmvc.RestAssuredMockMvc

class ConnectionPropertiesV2PositiveBase extends ContractsBase {

    def JSON_GETBYSUBID_CONNECTION_PROPS_CONTENT = "/v2/connectionProperties/positive/GetConnectionPropsResponse.json"
    def JSON_GETBYID_CONNECTION_PROPS_CONTENT = "/v2/connectionProperties/positive/GetConnectionPropsByIdResponse.json"
    def JSON_PATCH_CONNECTION_PROPS_CONTENT = "/v2/connectionProperties/positive/PatchConnectionPropsByIdResponse.json"
    def JSON_POST_CONNECTION_PROPS_CONTENT = "/v2/connectionProperties/positive/PostConnectionPropsResponse.json"
    def JSON_PUT_CONNECTION_PROPS_CONTENT = "/v2/connectionProperties/positive/PutConnectionPropsByIdResponse.json"

    def setup () {

        connectionPropsService.deleteConnProps(_ as String, _ as String) >> {
            return
        }


        connectionPropsService.getConnPropsBySubsystemId(_ as String) >> { String subsystemId ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_GETBYSUBID_CONNECTION_PROPS_CONTENT, ["%id":subsystemId]),
                            new TypeReference<List<ConnectionProperties>>(){})
        }

        connectionPropsService.getConnPropsById(_ as String, _ as String) >> { String subsystemId, String connectionPropertiesId ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_GETBYID_CONNECTION_PROPS_CONTENT, ["%id":subsystemId, "%pid":connectionPropertiesId]),
                            ConnectionProperties)
        }

        connectionPropsService.patchConnProps(_, _, _) >> { String subsystemId, String connPropsId, Map<String, Object> patchRequestFields ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PATCH_CONNECTION_PROPS_CONTENT, ["%id":subsystemId, "%pid":connPropsId]),
                            ConnectionProperties.class)
        }

        connectionPropsService.postConnProp(_ as String, _ as ConnectionProperties) >> { String subsystemId, ConnectionProperties connPropsId ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_POST_CONNECTION_PROPS_CONTENT, ["%id":subsystemId]),
                            ConnectionProperties.class)
        }

        connectionPropsService.putConnProps(_ as String, _ as String, _ as ConnectionProperties) >> { String subsystemId, String connPropsId, ConnectionProperties connectionProperties ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PUT_CONNECTION_PROPS_CONTENT, ["%id":subsystemId, "%pid":connPropsId]),
                            ConnectionProperties.class)
        }

        RestAssuredMockMvc.standaloneSetup(connectionPropertiesControllerV2)
    }
}