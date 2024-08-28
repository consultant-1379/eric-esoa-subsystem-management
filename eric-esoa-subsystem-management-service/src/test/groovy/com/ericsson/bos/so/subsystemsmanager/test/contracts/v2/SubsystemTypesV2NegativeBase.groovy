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
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeRequest
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.ServiceUnavailableException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeAlreadyExistsException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeDoesNotExistException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeAlreadyExistsException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils

class SubsystemTypesV2NegativeBase extends ContractsBase {
    def setup(){
        subsystemTypeServiceV2.getSubsystemTypes() >> {
            throw new ServiceUnavailableException()
        }
        subsystemTypeServiceV2.getSubsystemTypesByType(*_) >> { arguments ->
            String subSystemType = arguments[0]
            throw new SubsystemTypeDoesNotExistException(subSystemType)
        }
        subsystemTypeServiceV2.deleteSubsystemType(_ as Long) >> { Long subsystemTypeId ->
            if (subsystemTypeId == SERVICE_UNAVAILABLE_ID.toLong()) {
                throw new ServiceUnavailableException()
            }else if (subsystemTypeId == NOT_FOUND_ID.toLong()) {
                throw new SubsystemTypeDoesNotExistException(subsystemTypeId)
            }
        }
        subsystemTypeServiceV2.deleteSubsystemSubtype(*_) >> { arguments ->
            Long subsystemId = arguments[0]
            Long subTypeId = arguments[1]
            if(subsystemId == NOT_FOUND_ID.toLong()){
                throw new SubsystemTypeDoesNotExistException(subsystemId)
            }
            if(subTypeId == SERVICE_UNAVAILABLE_ID.toLong()) {
                throw new ServiceUnavailableException()
            }else if(subTypeId == NOT_FOUND_ID.toLong()) {
                throw new SubsystemSubtypeDoesNotExistException(subsystemId, subTypeId)
            }
        }
        subsystemTypeServiceV2.postSubsystemType(_ as SubsystemTypeRequest) >> { SubsystemTypeRequest request ->
            if(request.getType().equals(SERVICE_UNAVAILABLE_ID)){
                throw new ServiceUnavailableException()
            }
            if(request.type.isEmpty()) {
                throw new MalformedContentException("SSM-B-25", "type")
            }
            if (request.type.equals(SUBSYSTEM_TYPE_ALREADY_EXIST)) {
                throw new SubsystemTypeAlreadyExistsException(request.type)
            }
            if(request.getSubtypes().size()>0){
                String subTypeName = request.getSubtypes().get(0).getName()
                String emptyName = request.getSubtypes().get(1).getName()
                if(subTypeName.equals(SUBSYSTEM_SUB_TYPE_ALREADY_EXIST)){
                    throw new MalformedContentException("SSM-B-49", subTypeName)
                }else if(!StringUtils.hasText(emptyName)){
                    throw new MalformedContentException("SSM-B-48", "name")
                }
            }
        }
        subsystemTypeServiceV2.postSubsystemSubtype(* _) >> { arguments ->
            Long subSystemId = arguments[0]
            Subtype subtype = arguments[1]
            if(subSystemId == NOT_FOUND_ID.toLong()) {
                throw new SubsystemTypeDoesNotExistException(subSystemId)
            }
            if(subtype.getName().equals(SERVICE_UNAVAILABLE_ID)) {
                throw new ServiceUnavailableException()
            }
            if(subtype.getName().isEmpty()){
                throw new MalformedContentException("SSM-B-48", "name")
            }
            if(subtype.getName().equals(SUBSYSTEM_TYPE_ALREADY_EXIST)) {
                throw new SubsystemSubtypeAlreadyExistsException(subtype.name)
            }
        }
        RestAssuredMockMvc.standaloneSetup(subsystemTypeControllerV2, daoControllerAdvice)
    }
}
