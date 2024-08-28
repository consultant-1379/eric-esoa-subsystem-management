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

import com.ericsson.bos.so.subsystemsmanager.business.exception.K8sApiException

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException

import io.restassured.module.mockmvc.RestAssuredMockMvc

class AdapterLinksV2NegativeBase extends ContractsBase {

    def setup () {
        adaptersLinksService.fetchAdapterLinksByType(_ as String) >> { String subsystemType ->
            if (subsystemType.equals(SUBSYSTEM_TYPE_API_PROBLEM)) {
                throw new K8sApiException(subsystemType, "mynamespace")
            }

            if (subsystemType.equals(SUBSYSTEM_TYPE_NOT_FOUND)) {
                throw new SubsystemTypeDoesNotExistException(subsystemType)
            }
        }
        RestAssuredMockMvc.standaloneSetup(adapterLinksControllerV2, daoControllerAdvice)
    }
}