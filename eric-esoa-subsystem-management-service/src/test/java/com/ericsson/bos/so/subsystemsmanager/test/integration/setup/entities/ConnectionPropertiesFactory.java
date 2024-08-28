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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

import lombok.extern.slf4j.Slf4j;


/**
 * Assists with creation and persistence of {@link ConnectionProperties} entity objects.
 */
@Component
@Slf4j
public class ConnectionPropertiesFactory {

    @Autowired
    private SubsystemFactory subsystemFactory;
    @Autowired
    private SubsystemRepository subsystemRepository;
    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    // -- Basic Entity builders -- //

    /**
     * Builds the default connection properties.
     *
     * @param connectionPropertiesName the connection properties name
     * @return the connection properties
     */
    public static ConnectionProperties buildDefaultConnectionProperties(String connectionPropertiesName) {
        return buildConnectionProperties(PropertyFactory.buildProperties(connectionPropertiesName));
    }

    /**
     * Builds the connection properties.
     *
     * @param properties the properties
     * @return the connection properties
     */
    public static ConnectionProperties buildConnectionProperties(List<Property> properties) {
        final ConnectionProperties connectionProperties = ConnectionProperties.builder()
                .properties(properties)
                .encryptedKeys(filterEncryptedProperties(properties))
                .subsystemUsers(new ArrayList<>())
                .build();

        log.debug("Built {}.", connectionProperties);
        return connectionProperties;
    }

    /**
     * Builds the default connection properties.
     *
     * @param subsystem the subsystem
     * @param connectionPropertiesName the connection properties name
     * @return the connection properties
     */
    public static ConnectionProperties buildDefaultConnectionProperties(Subsystem subsystem, String connectionPropertiesName) {
        return buildConnectionProperties(subsystem, PropertyFactory.buildProperties(connectionPropertiesName));
    }

    /**
     * Builds the connection properties.
     *
     * @param subsystem the subsystem
     * @param properties the properties
     * @return the connection properties
     */
    public static ConnectionProperties buildConnectionProperties(Subsystem subsystem, Map<String, String> properties) {
        return buildConnectionProperties(subsystem, PropertyFactory.buildProperties(properties));
    }

    /**
     * Builds the connection properties.
     *
     * @param subsystem the subsystem
     * @param properties the properties
     * @return the connection properties
     */
    public static ConnectionProperties buildConnectionProperties(Subsystem subsystem, List<Property> properties) {
        final ConnectionProperties connectionProperties = ConnectionProperties.builder()
                .properties(properties)
                .encryptedKeys(filterEncryptedProperties(properties))
                .subsystem(subsystem)
                .subsystemId(subsystem.getId())
                .subsystemUsers(new ArrayList<>())
                .build();

        log.debug("Built {}.", connectionProperties);
        return connectionProperties;
    }

    /**
     * Builds the connection properties.
     *
     * @param count the count
     * @return the list
     */
    public static List<ConnectionProperties> buildConnectionProperties(int count) {
        return IntStream.range(0, count)
                .mapToObj(x -> buildDefaultConnectionProperties("connection-properties-" + x))
                .collect(Collectors.toList());
    }

    private static List<String> filterEncryptedProperties(List<Property> properties) {
        return properties.stream()
                .filter(Property::isEncrypted)
                .map(Property::getKey)
                .collect(Collectors.toList());
    }

    // -- Basic Entity builders -- //

    /**
     * Persist connection properties.
     *
     * @param subsystem the subsystem
     * @param properties the properties
     * @return the connection properties
     */
    public ConnectionProperties persistConnectionProperties(Subsystem subsystem, Map<String, String> properties) {
        return connectionPropertiesRepository.save(buildConnectionProperties(subsystem, properties));
    }

    /**
     * Persist default connection properties.
     *
     * @param count the count
     * @return the list
     */
    public List<ConnectionProperties> persistDefaultConnectionProperties(int count) {
        return persistDefaultConnectionProperties(subsystemFactory.persistDefaultSubsystem().getId(), count);
    }

    /**
     * Persist default connection properties.
     *
     * @param subsystemId the subsystem id
     * @param count the count
     * @return the list
     */
    public List<ConnectionProperties> persistDefaultConnectionProperties(long subsystemId, int count) {
        final Subsystem subsystem = subsystemRepository.findById(subsystemId)
                .orElseThrow(() -> new SubsystemDoesNotExistException(String.valueOf(subsystemId)));
                //.orElseThrow(() -> new IllegalArgumentException("There is no subsystem with ID: " + subsystemId));

        final List<ConnectionProperties> connectionProperties = IntStream.range(0, count)
                .mapToObj(x -> buildDefaultConnectionProperties(subsystem, "connection-properties-" + x))
                .collect(Collectors.toList());

        return connectionPropertiesRepository.saveAll(connectionProperties);
    }

}