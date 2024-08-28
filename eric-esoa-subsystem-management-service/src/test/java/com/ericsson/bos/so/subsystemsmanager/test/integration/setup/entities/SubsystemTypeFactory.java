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
package com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

/**
 * Assists with creation and persistence of {@link SubsystemType} entity objects.
 */
@Component
@Slf4j
public class SubsystemTypeFactory {

    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    /**
     * Persist subsystem type.
     *
     * @param type the type
     * @return the subsystem type
     */
    public SubsystemType persistSubsystemType(String type) {
        final SubsystemType subsystemType = SubsystemType.builder().type(type).build();

        log.debug("Built: {}.", subsystemType);
        return subsystemTypeRepository.save(subsystemType);
    }

    /**
     * Persist subsystem type.
     *
     * @param type the type
     * @param alias the alias
     * @param subtypes the subtypes
     * @return the subsystem type
     */
    public SubsystemType persistSubsystemType(String type, String alias, List<Subtype> subtypes) {
        final SubsystemType subsystemType = SubsystemType.builder().type(type).alias(alias).subtypes(subtypes).build();

        log.debug("Built: {}.", subsystemType);
        return subsystemTypeRepository.save(subsystemType);
    }
}
