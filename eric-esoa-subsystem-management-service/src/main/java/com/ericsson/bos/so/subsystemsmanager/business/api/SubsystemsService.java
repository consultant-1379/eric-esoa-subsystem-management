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
package com.ericsson.bos.so.subsystemsmanager.business.api;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

/**
 * The interface SubsystemsService
 */
@Component
public interface SubsystemsService {

    /**
     * Creates a subsystem.
     * @param subsystemRequest Subsystem
     * @return response of the saved subsystem
     */
    Subsystem postSubsystem(Subsystem subsystemRequest);

    /**
     * Gets all subsystems
     * @param tenantName String
     * @return list of subsystems
     */
    List<Subsystem> getAllSubsystems(String tenantName);

    /**
     * Gets subsystem by it's id
     * @param subsystemId String
     * @param tenantName String
     * @return response of details of subsystem
     */
    Subsystem getSubsystemById(String subsystemId, String tenantName);

    /**
     * Deletes subsystem by it's id
     * @param subsystemId String
     * @return deletion status of subsystem
     */
    boolean deleteSubsystemById(String subsystemId);

    /**
     * Deletes all ghost connection properties
     */
    void clearGhostConnnectionProperties();
}