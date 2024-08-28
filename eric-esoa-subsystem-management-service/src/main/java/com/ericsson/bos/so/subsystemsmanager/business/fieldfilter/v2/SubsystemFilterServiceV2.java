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
package com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v2;

import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterEntity;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterService;

/**
 * The interface SubsystemFilterServiceV2
 */
public interface SubsystemFilterServiceV2 extends SubsystemFilterService {

    /**
     * Filters response fields from subsystem details
     * @param fields String
     * @param tenantName String
     * @return filtered fields of subsystem details
     */
    Object filterResponseFields(String fields, String tenantName);

    /**
     * Filters single field from subsystem details
     * @param fields String
     * @param tenantName String
     * @return filtered field of subsystem details
     */
    List<Object> filterResponseSingleField(String fields, String tenantName);

    /**
     * Filters details of subsystem
     * @param subsystemId String
     * @param tenantName String
     * @return filtered details of subsystem by Id
     */
    SubsystemFilterEntity getByIdAsFilterEntity(String subsystemId, String tenantName);

    /**
     * Filters details of subsystem by certain fields passed
     * @param fields String
     * @param subsystemId String
     * @param tenantName String
     * @return filtered details of subsystem by Id
     */
    Object filterResponseFields(String fields, String subsystemId, String tenantName);

    /**
     * Filters details of subsystem by single field passed
     * @param field String
     * @param subsystemId String
     * @param tenantName String
     * @return filtered details of subsystem by Id
     */
    List<Object> filterResponseSingleFieldFromKnownSubsystem(String field, String subsystemId, String tenantName);
}
