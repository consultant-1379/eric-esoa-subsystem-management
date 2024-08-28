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
package com.ericsson.bos.so.subsystemsmanager.business.api.v1;

import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemTypeService;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemTypeCategory;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.SubsystemTypeRequest;

/**
 * The interface SubsystemTypeServiceV1
 */
public interface SubsystemTypeServiceV1 extends SubsystemTypeService {

    /**
     * Saves subsystem type to database
     * @param request SubsystemTypeRequest
     * @return response of saved subsystem type
     */
    SubsystemType postSubsystemType(SubsystemTypeRequest request);

    /**
     * Gets list of subsystem types
     * @return list of subsystem types
     */
    List<SubsystemType> getSubsystemTypes();

    /**
     * Gets list of subsystem types based on category
     * @param category SubsystemTypeCategory
     * @return list of subsystem types
     */
    List<SubsystemType> getSubsystemTypes(SubsystemTypeCategory category);

}
