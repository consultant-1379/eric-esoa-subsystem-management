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

import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException
import com.ericsson.bos.so.subsystemsmanager.business.exception.ServiceUnavailableException
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties

import io.restassured.module.mockmvc.RestAssuredMockMvc

class ConnectionPropertiesNegativeBase extends ContractsBase {

    def setup () {

        connectionPropsService.deleteConnProps(_, _) >> { String subsystemId, String connectionPropertiesId ->
            if (connectionPropertiesId.equals(NOT_FOUND_ID)) {
                throw new ConnectionPropertiesDoesNotExistException(connectionPropertiesId)
            } 
            if (connectionPropertiesId.isLong() && connectionPropertiesId.toLong() < 0) {
                throw new MalformedContentException("SSM-B-25", connectionPropertiesId)
            } 
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID)) {
                throw new ServiceUnavailableException()
            }
        }

        connectionPropsService.getConnPropsBySubsystemId(_ as String) >> { String subsystemId ->
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            } 
            if (!subsystemId.isLong() || subsystemId.toLong() < 0) {
                throw new MalformedContentException("SSM-B-25", subsystemId);
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID))
            {
                throw new ServiceUnavailableException()
            }
        }

        connectionPropsService.getConnPropsById(_ as String, _ as String) >> { String subsystemId, String connectionPropertiesId ->
            if (connectionPropertiesId.equals(NOT_FOUND_ID)) {
                throw new ConnectionPropertiesDoesNotExistException(connectionPropertiesId)
            } 
            if (!connectionPropertiesId.isLong() || connectionPropertiesId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", connectionPropertiesId)
            } 
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID))
            {
                throw new ServiceUnavailableException()
            }
        }

        connectionPropsService.patchConnProps(_, _, _) >> { String subsystemId, String connPropsId, Map<String, Object> patchRequestFields ->
            if (connPropsId.equals(NOT_FOUND_ID)) {
                throw new ConnectionPropertiesDoesNotExistException(connPropsId)
            } 
            if (!connPropsId.isLong() || connPropsId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", connPropsId)
            } 
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID))
            {
                throw new ServiceUnavailableException()
            }
        }

        connectionPropsService.postConnProp(_ as String, _ as ConnectionProperties) >> { String subsystemId, ConnectionProperties connPropsId ->
            if (!subsystemId.isLong() || subsystemId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", subsystemId)
            } 
            if (subsystemId.equals(NOT_FOUND_ID)){
                throw new SubsystemDoesNotExistException(subsystemId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID))
            {
                throw new ServiceUnavailableException()
            }
        }

        connectionPropsService.putConnProps(_, _, _) >> { String subsystemId, String connPropsId, ConnectionProperties connectionProperties ->
            if (subsystemId.equals(NOT_FOUND_ID)) {
                throw new SubsystemDoesNotExistException(subsystemId)
            } 
            if (connPropsId.equals(NOT_FOUND_ID)) {
                throw new ConnectionPropertiesDoesNotExistException(connPropsId)
            } 
            if (!connPropsId.isLong() || connPropsId.toLong() < 0){
                throw new MalformedContentException("SSM-B-25", connPropsId)
            }
            if (subsystemId.equals(SERVICE_UNAVAILABLE_ID))
            {
                throw new ServiceUnavailableException()
            }
        }

        RestAssuredMockMvc.standaloneSetup(connectionPropertiesController, daoControllerAdvice)
    }
}
