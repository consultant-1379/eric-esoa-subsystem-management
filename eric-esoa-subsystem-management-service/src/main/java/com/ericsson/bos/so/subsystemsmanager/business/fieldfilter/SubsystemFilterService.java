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
package com.ericsson.bos.so.subsystemsmanager.business.fieldfilter;

import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

/**
 * The interface SubsystemFilterService
 */
public interface SubsystemFilterService {

    /**
     * Gets all subsystem filter entities
     * @param subsystems List
     * @param populateSubsystemType boolean
     * @return list of subsystem filter entities
     */
    List<SubsystemFilterEntity> getAllAsFilterEntities(List<Subsystem> subsystems, boolean populateSubsystemType);

    /**
     * Maps subsystem to filter entity
     * @param subsystem Subsystem
     * @return mapped subsystem filter entity
     */
    SubsystemFilterEntity mapSubsystemToFilterEntity(Subsystem subsystem);

    /**
     * Maps subsystem to filter entity based populating subsystem type based on the boolean parameter
     * @param subsystem Subsystem
     * @param populateSubsystemType boolean
     * @return mapped subsystem filter entity
     */
    SubsystemFilterEntity mapSubsystemToFilterEntity(Subsystem subsystem, boolean populateSubsystemType);
}
