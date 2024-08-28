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

import com.ericsson.bos.so.subsystemsmanager.business.exception.ServiceUnavailableException
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeAlreadyExistsException
import com.ericsson.bos.so.subsystemsmanager.api.models.request.SubsystemTypeRequest
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException

import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemTypesNegativeBase extends ContractsBase {

    def setup () {
        subsystemTypeService.getSubsystemTypes() >> {
            throw new ServiceUnavailableException()
        }

        subsystemTypeService.postSubsystemType(_ as SubsystemTypeRequest) >> { SubsystemTypeRequest subsystemTypeRequest ->
            if (subsystemTypeRequest.type.isEmpty()) {
                throw new MalformedContentException("SSM-B-25", "")
            }
            if (subsystemTypeRequest.type.equals(SUBSYSTEM_TYPE_ALREADY_EXIST)) {
                throw new SubsystemTypeAlreadyExistsException(subsystemTypeRequest.type)
            }
            if (subsystemTypeRequest.type.equals(SERVICE_UNAVAILABLE_ID)) {
                throw new ServiceUnavailableException()
            }
       }

       subsystemTypeService.deleteSubsystemType(_ as Long) >> { Long subsystemTypeId ->
            if (subsystemTypeId == SERVICE_UNAVAILABLE_ID.toLong()) {
                throw new ServiceUnavailableException()
            }
            if (subsystemTypeId < 0) {
                throw new MalformedContentException("SSM-B-25", subsystemTypeId.toString())
            }
            if (subsystemTypeId == NOT_FOUND_ID.toLong()) {
                throw new SubsystemTypeDoesNotExistException(subsystemTypeId)
            }
       }

       RestAssuredMockMvc.standaloneSetup(subsystemTypeController, daoControllerAdvice)
    }
}
