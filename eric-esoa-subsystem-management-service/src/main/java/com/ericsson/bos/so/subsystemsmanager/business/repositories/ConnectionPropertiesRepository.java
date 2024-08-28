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
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;

/**
 * The Interface ConnectionPropertiesRepository.
 */
@Repository
public interface ConnectionPropertiesRepository extends JpaRepository<ConnectionProperties, Long>, JpaSpecificationExecutor<ConnectionProperties> {

    /**
     * Find by subsystem id.
     *
     * @param subsystemId the subsystem id
     * @return list of ConnectionProperties
     */
    Optional<List> findBySubsystemId(Long subsystemId);
}