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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.util;

import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.models.util.ConnectionPropertiesSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ResponseUtilTest.
 */
@Ignore
/**
/* @deprecated These tests assess the JSON format of ConnectionProperties (the name of the class is totally off..)
/*  and are depending on the order of the individual properties, which makes them extremely rigid
 */
public class ResponseUtilTest {

    private static final String EXPECTED_CONNECTION_PROPERTIES =
            "{\"id\":1,\"subsystemId\":1," + OldPropertyFactory.TEST_PROPERTIES_JSON + ",\"encryptedKeys\":[\"password\"],\"subsystemUsers\":[]}";

    private static final String EXPECTED_CONNECTION_PROPERTIES_WITH_SUBSYSTEM_USERS = "{\"id\":2,\"subsystemId\":1,"
            + OldPropertyFactory.TEST_PROPERTIES_JSON + ",\"encryptedKeys\":[\"password\"],\"subsystemUsers\":[{\"id\":1}]}";

    private static final String EXPECTED_SUBSYSTEMS =
            "{\"id\":1,\"subsystemTypeId\":1,\"name\":\"onboard-subsystem-1\",\"healthCheckTime\":\"health-check\","
            + "\"url\":\"url\",\"operationalState\":\"REACHABLE\",\""
                    + "connectionProperties\":[" + EXPECTED_CONNECTION_PROPERTIES + "," + EXPECTED_CONNECTION_PROPERTIES_WITH_SUBSYSTEM_USERS
                    + "],\"subsystemType\":{\"id\":1,\"type\":\"type\"}}";

    private Subsystem subsystem;

    private ConnectionProperties connectionProperties;

    private ConnectionProperties connectionPropertiesWithSubSystemUser;

    private SubsystemType subsystemType;

    private List<SubsystemUser> subsystemUsers;

    private SubsystemUser subsystemUser;

    private final List<ConnectionProperties> connectionPropList = new ArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ConnectionPropertiesSerializer connectionPropertiesSerializer;

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setUpConnectionProperties();
        setUpSubsystem();

        connectionPropertiesSerializer = new ConnectionPropertiesSerializer();

        final SimpleModule module = new SimpleModule("ConnectionPropertiesSerializer", new Version(2, 1, 3, null, null, null));
        module.addSerializer(ConnectionProperties.class, connectionPropertiesSerializer);
        objectMapper.registerModule(module);
    }

    /**
     * Convert connection properties without subsystem users test.
     *
     * @throws JsonProcessingException the json processing exception
     */
    @Test
    public void convertConnectionPropertiesWithoutSubsystemUsersTest() throws JsonProcessingException {
        final String serializedConnectionProperties = objectMapper.writeValueAsString(connectionProperties);
        Assert.assertEquals(EXPECTED_CONNECTION_PROPERTIES, serializedConnectionProperties); // TODO Asserting in such a way is.. very bad.
    }

    /**
     * Convert connection properties with subsystem users test.
     *
     * @throws JsonProcessingException the json processing exception
     */
    @Test
    public void convertConnectionPropertiesWithSubsystemUsersTest() throws JsonProcessingException {
        final String serializedConnectionProperties = objectMapper.writeValueAsString(connectionPropertiesWithSubSystemUser);
        Assert.assertEquals(EXPECTED_CONNECTION_PROPERTIES_WITH_SUBSYSTEM_USERS, serializedConnectionProperties);
    }

    /**
     * Convert subsystem test.
     *
     * @throws JsonProcessingException the json processing exception
     */
    @Test
    public void convertSubsystemTest() throws JsonProcessingException {
        final String serializedSubsystem = objectMapper.writeValueAsString(subsystem);
        Assert.assertEquals(EXPECTED_SUBSYSTEMS, serializedSubsystem);
    }


    /**
     * Setup connection properties.
     */
    private void setUpConnectionProperties() {
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add("password");
        connectionProperties = new ConnectionProperties();
        connectionProperties.setId(Long.valueOf(1));
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setEncryptedKeys(encryptedKeys);
        connectionProperties.setSubsystem(subsystem);
        connectionProperties.setSubsystemUsers(null);
        connectionProperties.setSubsystemId(Long.valueOf(1));

        setUpSubsystemUser();
        connectionPropertiesWithSubSystemUser = new ConnectionProperties();
        connectionPropertiesWithSubSystemUser.setId(Long.valueOf(2));
        connectionPropertiesWithSubSystemUser.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionPropertiesWithSubSystemUser.setEncryptedKeys(encryptedKeys);
        connectionPropertiesWithSubSystemUser.setSubsystem(subsystem);
        connectionPropertiesWithSubSystemUser.setSubsystemUsers(subsystemUsers);
        connectionPropertiesWithSubSystemUser.setSubsystemId(Long.valueOf(1));
    }

    /**
     * Setup subsystem.
     */
    private void setUpSubsystem() {
        setUpSubsystemType();
        connectionPropList.add(connectionProperties);
        connectionPropList.add(connectionPropertiesWithSubSystemUser);
        subsystem = new Subsystem();
        subsystem.setId(Long.valueOf(1));
        subsystem.setName("onboard-subsystem-1");
        subsystem.setOperationalState(OperationalState.REACHABLE);
        subsystem.setUrl("url");
        subsystem.setHealthCheckTime("health-check");
        subsystem.setConnectionProperties(connectionPropList);
        subsystem.setSubsystemTypeId(Long.valueOf(1));
        subsystem.setSubsystemType(subsystemType);
    }

    /**
     * Setup subsystem type.
     */
    private void setUpSubsystemType() {
        subsystemType = new SubsystemType();
        subsystemType.setId(Long.valueOf(1));
        subsystemType.setType("type");
    }

    /**
     * Setup subsystem user.
     */
    private void setUpSubsystemUser(){
        subsystemUser = new SubsystemUser();
        subsystemUsers = new ArrayList<>();
        subsystemUser.setId(Long.valueOf(1));
        subsystemUsers.add(subsystemUser);
    }
}

