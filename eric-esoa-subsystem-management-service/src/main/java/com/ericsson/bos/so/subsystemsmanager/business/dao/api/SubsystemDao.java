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
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

/**
 * The class SubsystemDao
 */
@Component
public interface SubsystemDao {

    /**
     * Gets all subsystems
     * @return list of subsystems
     */
    List<Subsystem> getAllSubsystems();

    /**
     * Gets all subsystems based on example query
     * @param example Example
     * @return list of subsystems
     */
    List<Subsystem> getAllSubsystems(Example<Subsystem> example);

    /**
     * Saves subsystem to database
     * @param subsystem Subsystem
     * @return response of saved subsystem
     */
    Subsystem saveSubsystem(Subsystem subsystem);

    /**
     * Finds subsystem by it's id and throws exception if not found
     * @param subsystemId Long
     * @return details of subsystem
     */
    Subsystem findSubsystemByIdWithException(Long subsystemId);

    /**
     * Finds subsystem by it's id
     * @param subsystemId Long
     * @return details of subsystem
     */
    Subsystem findSubsystemById(Long subsystemId);

    /**
     * Deletes subsystem by it's id
     * @param valueOf Long
     */
    void deleteSubsystemById(Long valueOf);

    /**
     * Gets paginated, sorted and filtered list of all subsystems
     * @param offset Integer
     * @param limit Integer
     * @param sortAttr String
     * @param sortDir String
     * @param filter String
     * @return list of subsystems
     */
    SubsystemList getFullPaginatedSubsystems(Integer offset, Integer limit, String sortAttr, String sortDir, String filter);

    /**
     * Gets paginated, sorted list of all subsystems filtered on the basis of example criteria
     * @param offset Integer
     * @param limit Integer
     * @param sortAttr String
     * @param sortDir String
     * @param filterCriteria Example
     * @return list of subsystems
     */
    List<Subsystem> getFullPaginatedSubsystems(Integer offset, Integer limit, String sortAttr, String sortDir, Example<Subsystem> filterCriteria);

    /**
     * Checks if subsystem exists by the given name
     * @param subsystemName String
     * @return result of verification
     */
    boolean existsByName(String subsystemName);

    /**
     * Counts the number of subsystems of given type
     * @param subsystemType String
     * @return number of subsystems of given type
     */
    long countBySubsystemType(String subsystemType);

    /**
     * Finds subsystem by api key and throws exception if not found
     * @param apiKey UUID
     * @return details of subsystem
     */
    Subsystem findSubsystemByApiKeyWithException(UUID apiKey);

    /**
     * Gets Subsystem subtype
     * @param subtypeId Long
     * @return details of subtype
     */
    Subtype getSubsytemSubtype(Long subtypeId);

}