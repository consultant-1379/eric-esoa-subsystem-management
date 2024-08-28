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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class SubsystemsV2PositiveBase extends ContractsBase {

    def SUBSYSTEMS_POSITIVE_FOLDER = "/subsystems/positive/responseJson/"
    def JSON_SUBSYSTEM_RESPONSE = "SubsystemResponse.json"
    def JSON_POST_SUBSYSTEM_WITH_SUBTYPE_RESPONSE = "PostSubsystemWithSubtypeResponse.json"
    def JSON_GET_SUBSYSTEMS_SELECT_RESPONSE = "GetSubsystemsSelectResponse.json"
    def JSON_GET_SUBSYSTEMS_SINGLE_SELECT_RESPONSE = "GetSubsystemsSingleSelectResponse.json"
    def JSON_GET_SUBSYSTEMS_RESPONSE = "GetSubsystemsResponse.json"
    def JSON_GET_SUBSYSTEMS_CODE_RESPONSE = "GetSubsystemsResponse2.json"

    def setup() {
        ObjectMapper mapper = new ObjectMapper()

        subsystemsServiceV2.deleteSubsystemById(_ as String) >> {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        subsystemsService.deleteSubsystemById(_ as String) >> {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        subsystemsServiceV2.getSubsystemById(*_) >> { arguments ->
            String subSystemId = arguments[0]
            String tenantName = arguments[1]
            if (subSystemId.startsWith("0500c886-") || subSystemId == '708'){
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_SUBSYSTEM_RESPONSE), Subsystem.class)
            }
            if (tenantName.equals("master")) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_SUBSYSTEM_RESPONSE), Subsystem.class)
            }
        }
        subsystemControllerV2.getSubsystem(*_) >> { arguments ->
            String apiKey = arguments[1]
            if (apiKey != null) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_SUBSYSTEM_RESPONSE), Subsystem.class)
            }
        }
        subsystemsServiceV2.fetchSubsystemByQuery(*_) >> { arguments ->
            Map<String, Object> paramsMap = arguments[1]
            if (paramsMap != null) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_GET_SUBSYSTEMS_RESPONSE), Subsystem[].class)
            } else {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_GET_SUBSYSTEMS_RESPONSE), Subsystem[].class)
            }
        }
        subsystemsServiceV2.getAllSubsystemsPagination(*_) >> { arguments ->
            String sortAttr = arguments[2]
            String sortDir = arguments[3]
            String filters = arguments[4]
            if (sortAttr.equals("name") && sortDir.equals("desc")) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_GET_SUBSYSTEMS_CODE_RESPONSE), SubsystemList.class)
            }
            if (filters.equals("%3D%7B%22name%22%3A%22auth4%22%7D")) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_GET_SUBSYSTEMS_CODE_RESPONSE), SubsystemList.class)
            }
        }
        subsystemJsonFilterServiceV2.filterResponseSingleField(*_) >> {
            return mapper.readValue(
                    prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                            + JSON_GET_SUBSYSTEMS_SINGLE_SELECT_RESPONSE), Object[].class)
        }
        subsystemJsonFilterServiceV2.filterResponseFields(*_) >> { arguments ->
            String select = arguments[0]
            if (select.equals("id,name")) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_GET_SUBSYSTEMS_SELECT_RESPONSE), Object.class)
            }
        }
        subsystemsServiceV2.postSubsystem(_ as Subsystem) >> { Subsystem subsystem ->
            if (subsystem.getSubsystemType().getSubtype() != null) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_POST_SUBSYSTEM_WITH_SUBTYPE_RESPONSE), Subsystem.class)
            }
            if (subsystem.getSubsystemType().getSubtype() == null) {
                return mapper.readValue(
                        prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                                + JSON_SUBSYSTEM_RESPONSE), Subsystem.class)
            }

        }
        subsystemsServiceV2.patchSubsystem(*_) >> {
            return mapper.readValue(
                    prepareMockResponseFromFile(BASE_DIR + SUBSYSTEMS_POSITIVE_FOLDER
                            + JSON_POST_SUBSYSTEM_WITH_SUBTYPE_RESPONSE), Subsystem.class)
        }

        RestAssuredMockMvc.standaloneSetup(subsystemControllerV2)
    }
}