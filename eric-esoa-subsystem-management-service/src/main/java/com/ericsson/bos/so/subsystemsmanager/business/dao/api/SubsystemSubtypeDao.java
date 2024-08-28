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

import java.util.List;

import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

/**
 * The class SubsystemSubtypeDao
 */
@Component
public interface SubsystemSubtypeDao {

    /**
     * Gets list of all subsystem subtypes
     * @return list of subtypes
     */
    List<Subtype> getSubsystemSubtypes();

    /**
     * Finds subtype by it's name
     * @param name String
     * @return details of subtype
     */
    Subtype findByName(String name);

    /**
     * finds subtype by it's id
     * @param id Long
     * @return details of subtype
     */
    Subtype findById(Long id);
}
