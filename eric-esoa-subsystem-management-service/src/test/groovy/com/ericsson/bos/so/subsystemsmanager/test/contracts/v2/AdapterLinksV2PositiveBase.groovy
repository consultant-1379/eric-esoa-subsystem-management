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

import com.ericsson.bos.so.subsystemsmanager.test.contracts.util.MockResponseFormatter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import io.restassured.module.mockmvc.RestAssuredMockMvc

class AdapterLinksV2PositiveBase extends ContractsBase {

    def JSON_ADAPTER_LINKS_CONTENT = "/v2/adapterLinks/positive/GetAdapterLinks.json"

    def setup () {
        adaptersLinksService.fetchAdapterLinksByType(_ as String) >> { String subsystemType ->
            return new ObjectMapper().readValue(
                            MockResponseFormatter.prepareMockResponseFromFile(BASE_DIR + JSON_ADAPTER_LINKS_CONTENT),
                            new TypeReference<List<String>>(){})
        }

        RestAssuredMockMvc.standaloneSetup(adapterLinksControllerV2)
    }
}