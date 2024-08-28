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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser
import com.ericsson.bos.so.subsystemsmanager.test.contracts.util.MockResponseFormatter
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemUsersV2PositiveBase extends ContractsBase {

    def JSON_PROPS_USER_CONTENT = "/v2/subsystemUsers/positive/PostSubsystemUserResponse.json"

    def setup () {

        subsystemUserService.postUserByConnsPropId(_, _) >> { String subsystemId, String connectionPropertiesId ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PROPS_USER_CONTENT, ["%id":subsystemId, "%pid":connectionPropertiesId]),
                            SubsystemUser.class)
        }

        RestAssuredMockMvc.standaloneSetup(subsystemUserControllerV2)
    }
}