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

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

/**
 * The Interface SubsystemRepository.
 */
@Repository
public interface SubsystemRepository extends JpaRepository<Subsystem, Long>, JpaSpecificationExecutor<Subsystem> {

    /**
     * Exists by name.
     *
     * @param name the name
     * @return true, if successful
     */
    boolean existsByName(String name);

    /**
     * Count by subsystem type type.
     *
     * @param type the type
     * @return the long
     */
    long countBySubsystemType_type(String type);

    /**
     * Find by api key.
     *
     * @param apiKey the api key
     * @return the subsystem details
     */
    Optional<Subsystem> findByApiKey(UUID apiKey);
}