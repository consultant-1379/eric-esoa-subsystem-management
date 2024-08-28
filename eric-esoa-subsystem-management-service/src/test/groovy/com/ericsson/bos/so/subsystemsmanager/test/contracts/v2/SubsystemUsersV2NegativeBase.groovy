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

import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException
import com.ericsson.bos.so.subsystemsmanager.business.exception.ServiceUnavailableException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemUserDoesNotExistException

import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemUsersV2NegativeBase extends ContractsBase {

    def setup () {

        subsystemUserService.deleteSubsystemUserById(_, _, _) >> { String subsystemId, String connectionPropertiesId , String subsystemUserId ->
            if (connectionPropertiesId.equals(NOT_FOUND_ID)) {
                throw new ConnectionPropertiesDoesNotExistException(connectionPropertiesId)
            }
            if (subsystemUserId.toLong() < 0) {
                throw new MalformedContentException("SSM-B-25", subsystemUserId)
            }
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemUserId.equals(NOT_FOUND_ID)) {
                throw new SubsystemUserDoesNotExistException(subsystemUserId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)) {
                throw new ServiceUnavailableException()
            }
        }

        subsystemUserService.postUserByConnsPropId(_ as String, _ as String) >> { String subsystemId, String connectionPropertiesId ->
            if (connectionPropertiesId.equals(NOT_FOUND_ID)) {
                throw new ConnectionPropertiesDoesNotExistException(connectionPropertiesId)
            }
            if (!connectionPropertiesId.isLong() || connectionPropertiesId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", connectionPropertiesId)
            }
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)) {
                throw new ServiceUnavailableException()
            }
        }

        RestAssuredMockMvc.standaloneSetup(subsystemUserControllerV2, daoControllerAdvice)
    }
}