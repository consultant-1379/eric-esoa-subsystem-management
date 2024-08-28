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
package com.ericsson.bos.so.subsystemsmanager.business.api.v2;

import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemTypeService;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeRequest;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeResponse;

/**
 * The interface SubsystemTypeServiceV2
 */
public interface SubsystemTypeServiceV2 extends SubsystemTypeService {

    /**
     * Saves subsystem type to database
     * @param request SubsystemTypeRequest
     * @return response of saved subsystem type
     */
    SubsystemTypeResponse postSubsystemType(SubsystemTypeRequest request);

    /**
     * Saves subtype of subsystem to database
     * @param subsystemTypeId Long
     * @param subtype Subtype
     * @return response of saved subtype of subsystem
     */
    List<Subtype> postSubsystemSubtype(Long subsystemTypeId, Subtype subtype);

    /**
     * Gets list of subsystem types
     * @return list of subsystem types
     */
    List<SubsystemTypeResponse> getSubsystemTypes();

    /**
     * Gets list of subsystem types by passed type
     * @param subsystemType String
     * @return list of subsystem types
     */
    List<SubsystemTypeResponse> getSubsystemTypesByType(String subsystemType);

    /**
     * Deletes subsystem by subtype
     * @param subsystemTypeId Long
     * @param subsystemSubtypeId Long
     */
    void deleteSubsystemSubtype(Long subsystemTypeId, Long subsystemSubtypeId);

}
