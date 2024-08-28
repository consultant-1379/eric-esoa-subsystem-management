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
package com.ericsson.bos.so.subsystemsmanager.business.dao.api;

import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The class SubsystemUserDao
 */
@Component
public interface SubsystemUserDao {

    /**
     * Saves subsystem user to database
     * @param subsystemUserRequest SubsystemUser
     * @return saved subsystem user
     */
    SubsystemUser saveSubsystemUser(SubsystemUser subsystemUserRequest);

    /**
     * Deletes subsystem user
     * @param subsystemUserId Long
     */
    void deleteSubsystemUserById(Long subsystemUserId);
}
