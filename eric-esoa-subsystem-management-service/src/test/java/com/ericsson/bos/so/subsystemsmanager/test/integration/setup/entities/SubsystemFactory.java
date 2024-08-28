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

import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemSubtypeRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;

import lombok.extern.slf4j.Slf4j;


/**
 * Assists with creation and persistence of {@link Subsystem} and {@link SubsystemType} entity objects.
 */
@Component
@Slf4j
public class SubsystemFactory {

    public static final String PRIMARY_SUBSYSTEM = "subsystem-1";
    public static final String SECONDARY_SUBSYSTEM = "subsystem-2";
    public static final String EXTRA_SUBSYSTEM = "subsystem-3";
    public static final String DEFAULT_SUBSYSTEM_TYPE = "DomainManager";
    public static final String DEFAULT_SUBSYSTEM_TYPE_NFVO = "NFVO";

    public static final String DEFAULT_VENDOR = "subsystem-vendor";
    public static final String DEFAULT_ADAPTER_LINK = "https://www.adapter.com";

    @Autowired
    private SubsystemRepository subsystemRepository;
    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;
    @Autowired
    private SubsystemSubtypeRepository subsystemSubtypeRepository;

    // -- Basic Entity builders -- //

    /**
     * Builds the subsystem type.
     *
     * @param name the name
     * @return the subsystem type
     */
    public static SubsystemType buildSubsystemType(String name) {
        final SubsystemType subsystemType = SubsystemType.builder()
                .type(name)
                .build();

        log.debug(Constants.BUILT, subsystemType);
        return subsystemType;
    }

    /**
     * Builds the default subsystem.
     *
     * @return the subsystem
     */
    public Subsystem buildDefaultSubsystem() {
        return buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, ConnectionPropertiesFactory.buildConnectionProperties(1));
    }

    /**
     * Builds the subsystem.
     *
     * @param subsystemName the subsystem name
     * @param subsystemTypeName the subsystem type name
     * @return the subsystem
     */
    public Subsystem buildSubsystem(String subsystemName, String subsystemTypeName) {
        return buildSubsystem(subsystemName, subsystemTypeName, ConnectionPropertiesFactory.buildConnectionProperties(1));
    }

    /**
     * Builds the subsystem.
     *
     * @param subsystemName the subsystem name
     * @param subsystemTypeName the subsystem type name
     * @param connectionProperties the connection properties
     * @return the subsystem
     */
    public Subsystem buildSubsystem(String subsystemName, String subsystemTypeName, List<ConnectionProperties> connectionProperties) {
        final SubsystemType subsystemType = findSubsystemType(subsystemTypeName);
        final String adapterLink = subsystemTypeName.equals(PredefinedSubsystemType.DOMAIN_MANAGER.getType()) ? null : DEFAULT_ADAPTER_LINK;

        final Subsystem subsystem = Subsystem.builder()
                .name(subsystemName)
                .url("/" + subsystemName)
                .operationalState(OperationalState.REACHABLE)
                .vendor(DEFAULT_VENDOR)
                .adapterLink(adapterLink)
                .subsystemType(subsystemType)
                .subsystemTypeId(subsystemType.getId())
                .connectionProperties(connectionProperties)
                .build();

        log.debug(Constants.BUILT, subsystem);
        return subsystem;
    }

    // -- Persistent builders -- //

    /**
     * Persist default subsystem.
     *
     * @return the subsystem
     */
    public Subsystem persistDefaultSubsystem() {
        return subsystemRepository.save(buildDefaultSubsystem());
    }

    /**
     * Persist subsystem.
     *
     * @param subsystem the subsystem
     * @return the subsystem
     */
    public Subsystem persistSubsystem(Subsystem subsystem) {
        return subsystemRepository.save(subsystem);
    }

    /**
     * Persist subsystem.
     *
     * @param subsystemName the subsystem name
     * @return the subsystem
     */
    public Subsystem persistSubsystem(String subsystemName) {
        return persistSubsystem(subsystemName, ConnectionPropertiesFactory.buildConnectionProperties(1));
    }

    /**
     * Persist subsystem.
     *
     * @param subsystemName the subsystem name
     * @param subsystemTypeName the subsystem type name
     * @return the subsystem
     */
    public Subsystem persistSubsystem(String subsystemName, String subsystemTypeName) {
        return persistSubsystem(subsystemName, subsystemTypeName, ConnectionPropertiesFactory.buildConnectionProperties(1));
    }

    /**
     * Persist subsystem.
     *
     * @param subsystemName the subsystem name
     * @param connectionProperties the connection properties
     * @return the subsystem
     */
    public Subsystem persistSubsystem(String subsystemName, List<ConnectionProperties> connectionProperties) {
        return persistSubsystem(subsystemName, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
    }

    /**
     * Persist subsystem.
     *
     * @param subsystemName the subsystem name
     * @param subsystemTypeName the subsystem type name
     * @param connectionProperties the connection properties
     * @return the subsystem
     */
    public Subsystem persistSubsystem(String subsystemName, String subsystemTypeName, List<ConnectionProperties> connectionProperties) {
        final Subsystem subsystem = buildSubsystem(subsystemName, subsystemTypeName, connectionProperties);
        return subsystemRepository.save(subsystem);
    }

    private SubsystemType findSubsystemType(String subsystemTypeName) {
        return subsystemTypeRepository.findByType(subsystemTypeName).orElse(buildSubsystemType(subsystemTypeName));
    }

    /**
     * Builds the subsystem.
     *
     * @param subsystemName the subsystem name
     * @param subsystemTypeName the subsystem type name
     * @param subtypeName the subtype name
     * @param connectionProperties the connection properties
     * @return the subsystem
     */
    public Subsystem buildSubsystem(String subsystemName, String subsystemTypeName, String subtypeName,
            List<ConnectionProperties> connectionProperties) {
        final SubsystemType subsystemType = findSubsystemType(subsystemTypeName);
        final Subtype subtype = findSubsystemSubtype(subtypeName);
        subsystemType.setSubtype(subtype);
        final Subsystem subsystem = Subsystem.builder()
                .name(subsystemName)
                .operationalState(OperationalState.REACHABLE)
                .subsystemType(subsystemType)
                .subsystemTypeId(subsystemType.getId())
                .connectionProperties(connectionProperties)
                .subsystemSubtypeId(subtype.getId())
                .build();

        log.debug(Constants.BUILT, subsystem);
        return subsystem;
    }

    private Subtype findSubsystemSubtype(String subtypeName) {
        return subsystemSubtypeRepository.findByName(subtypeName).orElse(buildSubsystemSubType(subtypeName));
    }

    /**
     * Builds the subsystem sub type.
     *
     * @param name the name
     * @return the subtype
     */
    public static Subtype buildSubsystemSubType(String name) {
        final Subtype subtype = Subtype.builder()
                .name(name)
                .build();

        log.debug(Constants.BUILT, subtype);
        return subtype;
    }
}
