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
package com.ericsson.bos.so.subsystemsmanager.business.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The Interface SubsystemUserRepository.
 */
@Repository
public interface SubsystemUserRepository extends JpaRepository<SubsystemUser, Long>, JpaSpecificationExecutor<SubsystemUser> {

    /**
     * Find subsystem user by connection props id.
     *
     * @param connectionPropertiesId the connection properties id
     * @return the list of SubsystemUser
     */
    List<SubsystemUser> findSubsystemUserByConnectionPropsId(Long connectionPropertiesId);
}