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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeResponse
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemTypesV2PositiveBase extends ContractsBase {
    def SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER = "/subsystemTypes/positive/responseJson/"
    def JSON_SUBSYSTEM_TYPES_V2_CONTENT = "GetSubsystemTypesResponse.json"
    def JSON_SUBSYSTEM_TYPES_V2_BY_TYPE_NFVO_CONTENT = "GetSubsystemTypesByTypeResponse.json"
    def JSON_POST_SUBSYSTEM_TYPE_WITH_EMPTY_SUBTYPE_V2_RESPONSE = "PostSubsystemTypeWithEmptySubtypesResponse.json"
    def JSON_POST_SUBSYSTEM_TYPE_WITH_SINGLE_SUBTYPE_V2_RESPONSE = "PostSubsystemTypeWithSingleSubtypeResponse.json"
    def JSON_POST_SUBSYSTEM_TYPE_WITH_LIST_OF_SUBTYPES_V2_RESPONSE = "PostSubsystemTypeWithListOfSubtypesResponse.json"
    def JSON_POST_SUBSYSTEM_SUBTYPE_V2_RESPONSE = "PostSubsystemSubtypeResponse.json"
    def setup () {

        ObjectMapper mapper = new ObjectMapper()

        subsystemTypeServiceV2.getSubsystemTypes() >> {
            return mapper.readValue(
                    prepareMockResponseFromFile(BASE_DIR + SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER
                            + JSON_SUBSYSTEM_TYPES_V2_CONTENT), SubsystemTypeResponse[].class)
        }
        subsystemTypeServiceV2.getSubsystemTypesByType(*_) >> { arguments ->
            String subSystemType = arguments[0]
            if(subSystemType.equals("NFVO")){
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER
                                + JSON_SUBSYSTEM_TYPES_V2_BY_TYPE_NFVO_CONTENT), SubsystemTypeResponse[].class)
            }
        }

        subsystemTypeServiceV2.postSubsystemType(_ as SubsystemTypeRequest) >> { SubsystemTypeRequest request ->

            if(request.getSubtypes() == null){
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER
                                + JSON_POST_SUBSYSTEM_TYPE_WITH_EMPTY_SUBTYPE_V2_RESPONSE), SubsystemTypeResponse.class)
            }else if(request.getSubtypes().size() == 1){
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER
                                + JSON_POST_SUBSYSTEM_TYPE_WITH_SINGLE_SUBTYPE_V2_RESPONSE), SubsystemTypeResponse.class)
            }else if(request.getSubtypes().size() > 1){
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER
                                + JSON_POST_SUBSYSTEM_TYPE_WITH_LIST_OF_SUBTYPES_V2_RESPONSE), SubsystemTypeResponse.class)
            }
        }

        subsystemTypeServiceV2.postSubsystemSubtype(*_) >> { arguments ->
            Subtype subtype = arguments[1]
            if(subtype.getName().equals("subtype")){
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEM_TYPES_V2_RESPONSE_FOLDER
                                + JSON_POST_SUBSYSTEM_SUBTYPE_V2_RESPONSE), Subtype[].class)
            }
        }

        RestAssuredMockMvc.standaloneSetup(subsystemTypeControllerV2)
    }
}
