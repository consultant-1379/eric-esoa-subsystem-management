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
package com.ericsson.bos.so.subsystemsmanager.test.contracts

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType
import com.ericsson.bos.so.subsystemsmanager.test.contracts.util.MockResponseFormatter
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ericsson.bos.so.subsystemsmanager.api.models.request.SubsystemTypeRequest

import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemTypesPositiveBase extends ContractsBase {

    def JSON_SUBSYSTEM_TYPES_CONTENT = "/subsystemTypes/positive/GetSubsystemTypesResponse.json"
    def JSON_SUBSYSTEM_TYPE_POST_RESPONSE = "/subsystemTypes/positive/PostSubsystemTypeResponse.json"

    def setup () {
        subsystemTypeService.getSubsystemTypes() >> {
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_SUBSYSTEM_TYPES_CONTENT),
                            new TypeReference<List<SubsystemType>>(){})
        }

        subsystemTypeService.postSubsystemType(_ as SubsystemTypeRequest) >> { SubsystemTypeRequest subsystemTypeRequest ->
            return new ObjectMapper().readValue(MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_SUBSYSTEM_TYPE_POST_RESPONSE), SubsystemType.class)
        }

        subsystemTypeService.deleteSubsystemType(_ as Long) >> { Long subsystemTypeId ->
           return subsystemTypeId
       }

        RestAssuredMockMvc.standaloneSetup(subsystemTypeController)
    }
}
