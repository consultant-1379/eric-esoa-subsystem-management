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

import org.springframework.util.ObjectUtils

import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException
import com.ericsson.bos.so.subsystemsmanager.business.exception.NameMustBeUniqueException
import com.ericsson.bos.so.subsystemsmanager.business.exception.ServiceUnavailableException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemCannotBeDeletedException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemPartialDeleteException
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem

import io.restassured.module.mockmvc.RestAssuredMockMvc

class SubsystemsNegativeBase extends ContractsBase {

    def setup () {
        subsystemsService.deleteSubsystemById(_ as String) >> { String subsystemId ->
            if (subsystemId.equals(PARTIAL_DELETE_ID)){
                throw new SubsystemPartialDeleteException(subsystemId)
            }
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(NAME_MUST_BE_UNIQUE_ID)) {
                throw new SubsystemCannotBeDeletedException(subsystemId)
            }
            if (subsystemId.isLong() && subsystemId.toLong() < 0) {
                throw new MalformedContentException("SSM-B-25", subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)) {
                throw new ServiceUnavailableException()
            }
        }
        subsystemsService.getAllSubsystemsPagination(_, _, _, _, _, _) >> { Integer offset, Integer limit, String sortAttr, String sortDir, String filter, String tenantName ->
            throw new ServiceUnavailableException()
        }
        subsystemsService.fetchSubsystemByQuery(_, _) >> { final String tenantName, Map<String,Object> search ->
            throw new ServiceUnavailableException()
        }
        subsystemJsonFilterService.filterResponseSingleField(_, _) >> { String fields, String tenantName ->
            if (fields.contains("malformed")) {
                throw new MalformedContentException("SSM-B-34", fields)
            }
            throw new ServiceUnavailableException()
        }
        subsystemJsonFilterService.filterResponseFields(_, _) >> { String fields, String tenantName ->
            if (fields.contains("malformed")) {
                throw new MalformedContentException("SSM-B-34", fields)
            }
            throw new ServiceUnavailableException()
        }
        subsystemJsonFilterService.filterResponseSingleFieldFromKnownSubsystem(_ as String, _ as String, _ as String) >> { String field, String subsystemId, String tenantName ->
            if (!subsystemId.isLong() || subsystemId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", subsystemId)
            }
            if (subsystemId.equals(NOT_FOUND_ID)){
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)){
                throw new ServiceUnavailableException()
            }
        }
        subsystemJsonFilterService.filterResponseFields(_ as String, _ as String, _ as String) >> { String fields, String subsystemId, String tenantName ->
            if (!subsystemId.isLong() || subsystemId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", subsystemId)
            }
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)){
                throw new ServiceUnavailableException()
            }
        }
        subsystemsService.getSubsystemById(_ as String, _ as String) >> { String subsystemId, String tenantName ->
            if (subsystemId.equals(NOT_FOUND_ID)){
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)){
                throw new ServiceUnavailableException()
            }
        }
        subsystemsService.patchSubsystem(_ as String, _ as Map<String,Object>) >> { String subsystemId, Map<String,Object> updatedSubsystem ->
            if (!subsystemId.isLong() || subsystemId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)){
                throw new ServiceUnavailableException()
            }
            if (subsystemId.equals(NAME_MUST_BE_UNIQUE_ID)){
                throw new NameMustBeUniqueException(updatedSubsystem.name)
            }
            throw new SubsystemDoesNotExistException(subsystemId)
        }
        subsystemsService.postSubsystem(_ as Subsystem) >> { Subsystem subsystemOnboardingRequest ->
            if (ObjectUtils.isEmpty(subsystemOnboardingRequest.getConnectionProperties())) {
                throw new MalformedContentException("SSM-B-25", "")
            }
            if (subsystemOnboardingRequest.name.equals(NAME_MUST_BE_UNIQUE)){
                throw new NameMustBeUniqueException(subsystemOnboardingRequest.name)
            }
            throw new ServiceUnavailableException()
        }

        RestAssuredMockMvc.standaloneSetup(subsystemController, daoControllerAdvice)
    }
}