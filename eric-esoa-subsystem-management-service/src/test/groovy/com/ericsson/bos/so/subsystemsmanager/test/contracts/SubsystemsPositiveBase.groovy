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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType
import com.ericsson.bos.so.subsystemsmanager.test.contracts.util.MockResponseFormatter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference

import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemsPositiveBase extends ContractsBase {

    def JSON_PAGINATED_SUBSYTEM_CONTENT = "/subsystems/positive/GetSubsystemsResponse.json"
    def JSON_PAGINATED_SORTED_CONTENT = "/subsystems/positive/GetSubsystemsSortedResponse.json"
    def JSON_PAGINATED_FILTER_SUBSYTEM_CONTENT = "/subsystems/positive/GetSubsystemsFilteredResponse.json"
    def JSON_PARAMSMAP_SUBSYTEM_CONTENT = "/subsystems/positive/GetSubsystemsParamMapResponse.json"
    def JSON_FILTERED_SUBSYTEM_CONTENT = "/subsystems/positive/GetSubsystemsByFilteredJsonResponse.json"
    def JSON_FILTEREDSINGLE_SUBSYTEM_CONTENT = "/subsystems/positive/GetSubsystemsByFilteredJsonSingleResponse.json"
    def JSON_FILTERED_SUBSYTEMKNOWN_CONTENT = "/subsystems/positive/GetSubsystemsByFilteredJsonWithinKnownSubsystemResponse.json"
    def JSON_SINGLE_SUBSYTEMKNOWN_CONTENT = "/subsystems/positive/GetSubsystemsByFilteredSingleJsonWithinKnownSubsystemResponse.json"
    def JSON_SUBSYTEM_BYID_CONTENT = "/subsystems/positive/GetSubsystemsByIdResponse.json"
    def JSON_SUBSYTEM_TYPES_CONTENT = "/subsystems/positive/GetSubsystemTypesResponse.json"
    def JSON_POST_SUBSYTEM_CONTENT = "/subsystems/positive/PostSubsystemResponse.json"
    def JSON_PUT_SUBSYTEM_CONTENT = "/subsystems/positive/PutSubsystemResponse.json"
    def JSON_PATCH_SUBSYTEM_CONTENT = "/subsystems/positive/PatchSubsystemResponse.json"

    def setup () {

        subsystemsService.patchSubsystem(_ as String, _ as Map<String,Object>) >> { String subsystemId, Map<String,Object> updatedSubsystem ->
            if (updatedSubsystem.containsKey("adapterLink")  ) {
                return new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PUT_SUBSYTEM_CONTENT, ["%id":subsystemId]),
                                Subsystem.class)
            } else {
                return new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PATCH_SUBSYTEM_CONTENT, ["%id":subsystemId]),
                                Subsystem.class)
            }
        }

        subsystemsService.getAllSubsystemsPagination(_, _, _, _, _, _) >> { Integer offset, Integer limit, String sortAttr, String sortDir, String filter, String tenantName ->
            SubsystemList subsystemList = new SubsystemList()
            if (filter && !filter.isEmpty() ) {
                subsystemList.setItems(new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PAGINATED_FILTER_SUBSYTEM_CONTENT),
                                new TypeReference<List<Subsystem>>(){}))
            } else if (sortAttr && sortAttr.equals('name') && sortDir && sortDir.equals('asc')) {
                subsystemList.setItems(new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PAGINATED_SORTED_CONTENT),
                                new TypeReference<List<Subsystem>>(){}))
            } else {
                subsystemList.setItems(new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PAGINATED_SUBSYTEM_CONTENT),
                                new TypeReference<List<Subsystem>>(){}))
            }
            subsystemList.setTotal(subsystemList.getItems().size())
            return subsystemList
        }

        subsystemsService.fetchSubsystemByQuery(_, _) >> { final String tenantName, Map<String,Object> search ->
            if (search && !search.isEmpty() ) {
                return new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PARAMSMAP_SUBSYTEM_CONTENT),
                                new TypeReference<List<Subsystem>>(){})
            } else {
                return new ObjectMapper().readValue(
                                MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_PAGINATED_SUBSYTEM_CONTENT),
                                new TypeReference<List<Subsystem>>(){})
            }
        }

        subsystemJsonFilterService.filterResponseSingleField(_, _) >> { String fields, String tenantName ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_FILTEREDSINGLE_SUBSYTEM_CONTENT),
                            new TypeReference<List<Object>>(){})
        }

        subsystemJsonFilterService.filterResponseFields(_, _) >> { String fields, String tenantName ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_FILTERED_SUBSYTEM_CONTENT),
                            new TypeReference<List<Object>>(){})
        }

        subsystemJsonFilterService.filterResponseSingleFieldFromKnownSubsystem(_ as String, _ as String, _ as String) >> { String field, String subsystemId, String tenantName ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_SINGLE_SUBSYTEMKNOWN_CONTENT),
                            new TypeReference<List<Subsystem>>(){})
        }

        subsystemJsonFilterService.filterResponseFields(_ as String, _ as String, _ as String) >> { String fields, String subsystemId, String tenantName ->
            Object data = MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_FILTERED_SUBSYTEMKNOWN_CONTENT, ["%id":subsystemId])
            Subsystem subsystem = new Subsystem()
            return new ObjectMapper().readerForUpdating(subsystem).readValue(data)
        }

        subsystemsService.getSubsystemById(_, _) >> { String subsystemId, String tenantName ->
            tenantName = tenantName ?: "tenant1"
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_SUBSYTEM_BYID_CONTENT, ["%id":subsystemId, "%te":tenantName]),
                            Subsystem.class)
        }

        subsystemTypeService.fetchAllSubsystemTypes() >> {
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_SUBSYTEM_TYPES_CONTENT),
                            new TypeReference<List<SubsystemType>>(){})
        }

        subsystemsService.deleteSubsystemById(_) >> {
            return true
        }

        subsystemsService.postSubsystem(_ as Subsystem) >> { Subsystem subsystemOnboardingRequest ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_POST_SUBSYTEM_CONTENT),
                            Subsystem.class)
        }

        RestAssuredMockMvc.standaloneSetup(subsystemController)
    }
}