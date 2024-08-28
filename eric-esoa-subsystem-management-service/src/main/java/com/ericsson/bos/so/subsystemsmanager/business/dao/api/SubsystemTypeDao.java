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
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;

/**
 * The class SubsystemTypeDao
 */
@Component
public interface SubsystemTypeDao {

    /**
     * Saves subsystem type to database
     * @param subsystemType SubsystemType
     * @return saved subsystem type
     */
    SubsystemType saveSubsystemType(SubsystemType subsystemType);

    /**
     * Saves list of subsystem types to database
     * @param subsystemTypeId Long
     * @param subtype Subtype
     * @return list of saved subtypes
     */
    List<Subtype> saveSubsystemSubtype(Long subsystemTypeId, Subtype subtype);

    /**
     * Deletes subsystem type
     * @param id long
     */
    void deleteSubsystemType(long id);

    /**
     * Deletes subtype
     * @param subsystemTypeId Long
     * @param subsystemSubtypeId Long
     */
    void deleteSubsystemSubtype(Long subsystemTypeId, Long subsystemSubtypeId);

    /**
     * Finds subsystem type by it's id
     * @param subsystemTypeId long
     * @return details of subsystem type
     */
    SubsystemType findById(long subsystemTypeId);

    /**
     * Gets list of all subsystem types
     * @return list of subsystem types
     */
    List<SubsystemType> findAll();

    /**
     * Finds subsystem type by the given type.
     * @param type String
     * @return details of subystem type
     */
    SubsystemType findByType(String type);

    /**
     * Checks if subsystem type exists
     * @param subsystemType String
     * @return result of verification
     */
    boolean subsystemTypeExists(String subsystemType);
}
