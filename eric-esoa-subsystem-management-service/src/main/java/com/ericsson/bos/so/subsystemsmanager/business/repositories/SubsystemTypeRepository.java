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
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;

/**
 * The Interface SubsystemTypeRepository.
 */
@Repository
public interface SubsystemTypeRepository extends JpaRepository<SubsystemType, Long>, JpaSpecificationExecutor<SubsystemType> {

    /**
     * Delete subsystemType by id.
     *
     * @param id the id
     */
    void deleteById(long id);

    /**
     * Find subsystemType by id.
     *
     * @param id the id
     * @return the optional
     */
    Optional<SubsystemType> findById(long id);

    /**
     * Find by type.
     *
     * @param type the type
     * @return the optional
     */
    Optional<SubsystemType> findByType(String type);

    /**
     * Find by type not in.
     *
     * @param types the types
     * @return the list
     */
    List<SubsystemType> findByTypeNotIn(Set<String> types);

    /**
     * Exists by type.
     *
     * @param type the type
     * @return true, if successful
     */
    boolean existsByType(String type);

}