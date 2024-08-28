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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The interface SubsystemUserService
 */
public interface SubsystemUserService {

    /**
     * Deletes subsystem user by id
     * @param subsystemId String
     * @param connectionPropertiesId String
     * @param subsystemUserId String
     */
    void deleteSubsystemUserById(String subsystemId, String connectionPropertiesId, String subsystemUserId);

    /**
     * Creates subsystem user by subsystem and connection properties id.
     * @param subsystemId String
     * @param connectionPropertiesId String
     * @return response of the saved subsystem user
     */
    SubsystemUser postUserByConnsPropId(String subsystemId, String connectionPropertiesId);
}