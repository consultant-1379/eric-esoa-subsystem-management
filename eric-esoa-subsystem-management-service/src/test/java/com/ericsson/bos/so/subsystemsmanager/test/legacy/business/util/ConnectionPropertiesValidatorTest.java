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

import static com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory.createDefaultTestNameProperty;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory.createDefaultTestNameProperty2;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.business.util.SubsystemValidatorTest.validSubsystem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * The Class ConnectionPropertiesValidatorTest.
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class ConnectionPropertiesValidatorTest {

    private static final String ENCRYPT_ALL = "*";

    private static final String STRING_WITH_256_CHARS =
            "ICWDnkokCdSS4bvQnl55MYDf7h5jKtZmdhMQwSFplgWyLy6iwN"
            + "h0fvpEdIFGPqoRYrpo2Zl5nPdqpV7obofArWbM0Qkm5uYiHNLkGNwmi3pisxe8XGhd1W68nHtf84600s"
            + "Abkcb5xzfBeDg7CnXU2YV2pMQYIsQ8cxDHZugoev2C209ab79udnO1uSTyMfCKsedVeBw4tAXF5Qf4uW90T"
            + "vbBYZhUUpFVdnqg5ZNPvUclqPjVNqhs4F6YjmvGyahR";

    @Mock
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    @Mock
    private SubsystemValidator subsystemValidator;

    @InjectMocks
    private ConnectionPropertiesValidator connectionPropertiesValidator;

    /**
     * Valid connection property.
     *
     * @return the connection properties
     */
    public static ConnectionProperties validConnectionProperty() {
        final ConnectionProperties connectionProperties = new ConnectionProperties();

        final SubsystemUser subsystemUser = new SubsystemUser();

        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setEncryptedKeys(validEncrpytedKeys());
        connectionProperties.setSubsystemUsers(usersSet);
        connectionProperties.setId(26l);
        connectionProperties.setSubsystemId(123L);

        return connectionProperties;
    }

    /**
     * Valid connection property2.
     *
     * @return the connection properties
     */
    private static ConnectionProperties validConnectionProperty2() {
        final ConnectionProperties connectionProperties = validConnectionProperty();

        connectionProperties.getProperties().clear();
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties2());

        return connectionProperties;
    }

    /**
     * Valid connection property with password field but without encrypted keys.
     *
     * @return the connection properties
     */
    private static ConnectionProperties validConnectionPropertyWithPasswordFieldButWithoutEncryptedKeys() {
        final ConnectionProperties connectionProperties = new ConnectionProperties();

        final SubsystemUser subsystemUser = new SubsystemUser();

        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystemUsers(usersSet);
        connectionProperties.setId(26l);

        return connectionProperties;
    }

    /**
     * Valid properties.
     *
     * @param extraKey the extra key
     * @return the list
     */
    private static List<Property> validProperties(String extraKey) {
        final List<Property> properties = new ArrayList<>();
        final Property property = new Property();
        property.setKey(Constants.$_NAME);
        property.setValue("name-1");
        final Property property2 = new Property();
        property2.setKey(Constants.$_USERNAME);
        property2.setValue("username-1");
        final Property extraProperty = new Property();
        extraProperty.setKey(extraKey);
        extraProperty.setValue("extra-key-1");
        properties.add(property);
        properties.add(property2);
        properties.add(extraProperty);
        return properties;
    }

    /**
     * Valid encrpyted keys.
     *
     * @return the list
     */
    private static List<String> validEncrpytedKeys() {
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add(Constants.$_NAME);
        encryptedKeys.add(Constants.$_USERNAME);
        return encryptedKeys;
    }

    /**
     * Valid encrpyted keys.
     *
     * @param extraKey the extra key
     * @return the list
     */
    private static List<String> validEncrpytedKeys(String extraKey) {
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add(Constants.$_NAME);
        encryptedKeys.add(Constants.$_USERNAME);
        encryptedKeys.add(extraKey);
        return encryptedKeys;
    }

    /**
     * Builds the subsystem type.
     *
     * @return the subsystem type
     */
    private static SubsystemType buildSubsystemType() {
        return SubsystemType.builder().type("DomainManager").build();
    }

    /**
     * Test is valid connection properties no exception thrown.
     */
    @Test
    public void testIsValidConnProps_noExceptionThrown() {
        final Optional<ConnectionProperties> returnValue = Optional.of(validConnectionProperty());
        when(connectionPropertiesRepository.findById(Mockito.anyLong())).thenReturn(returnValue);

        final ConnectionProperties myConnectionProperties = connectionPropertiesValidator.isValidConnProps(String.valueOf(100l));

        assertEquals(validConnectionProperty().getId(), myConnectionProperties.getId());
        assertEquals(validConnectionProperty().getProperties().get(2).getValue(), myConnectionProperties.getProperties().get(2).getValue());
        assertEquals(validConnectionProperty().getProperties().get(0).getValue(), myConnectionProperties.getProperties().get(0).getValue());
        assertEquals(validConnectionProperty().getProperties().get(1).getValue(), myConnectionProperties.getProperties().get(1).getValue());
        assertEquals(validConnectionProperty().getSubsystemUsers(), myConnectionProperties.getSubsystemUsers());
        assertEquals(validConnectionProperty().getProperties().get(2).getValue(), myConnectionProperties.getProperties().get(2).getValue());
    }

    /**
     * Test is valid connection properties exception thrown.
     */
    @Test(expected = Exception.class)
    public void testIsValidConnProps_exceptionThrown() {
        final String id = null;
        connectionPropertiesValidator.isValidConnProps(id);
    }

    /**
     * Checks if connection properties are valid when both ids are valid.
     */
    @Test
    public void isValidConnProps_whenBothIdsAreValid() {
        final Optional<ConnectionProperties> returnConnectionProperties = Optional.of(validConnectionProperty());
        when(connectionPropertiesRepository.findById(Mockito.anyLong())).thenReturn(returnConnectionProperties);

        final ConnectionProperties connectionProperties = connectionPropertiesValidator.isValidConnProps(String.valueOf(100l), String.valueOf(100l));
        connectionProperties.setSubsystem(validSubsystem(Constants.$_validSubsystemName));

        assertEquals(validConnectionProperty().getProperties().get(2).getValue(), connectionProperties.getProperties().get(2).getValue());
        assertEquals(validSubsystem(Constants.$_validSubsystemName).getHealthCheckTime(),
                connectionProperties.getSubsystem().getHealthCheckTime());
        assertEquals(validSubsystem(Constants.$_validSubsystemName).getOperationalState(),
                connectionProperties.getSubsystem().getOperationalState());

        assertEquals(validSubsystem(Constants.$_validSubsystemName).getConnectionProperties().get(0).getId(),
                connectionProperties.getSubsystem().getConnectionProperties().get(0).getId());
    }

    /**
     * Checks if is valid connection properties with all three parameters valid.
     */
    @Test
    public void isValidConnProps_withAllThreeParametersValid() {
        final Optional<ConnectionProperties> returnConnectionProperties = Optional.of(validConnectionProperty());
        when(connectionPropertiesRepository.findById(Mockito.anyLong())).thenReturn(returnConnectionProperties);

        final ConnectionProperties connectionProperties = connectionPropertiesValidator.isValidConnProps(String.valueOf(100l),
                String.valueOf(100l), ImmutableMap.of(Constants.$_NAME, "value"));
        connectionProperties.setSubsystem(validSubsystem(Constants.$_validSubsystemName));

        assertEquals(validSubsystem(Constants.$_validSubsystemName).getName(), connectionProperties.getSubsystem().getName());
        assertEquals(validSubsystem(Constants.$_validSubsystemName).getHealthCheckTime(),
                connectionProperties.getSubsystem().getHealthCheckTime());
        assertEquals(validSubsystem(Constants.$_validSubsystemName).getOperationalState(),
                connectionProperties.getSubsystem().getOperationalState());

        assertEquals(validSubsystem(Constants.$_validSubsystemName).getConnectionProperties().get(0).getId(),
                connectionProperties.getSubsystem().getConnectionProperties().get(0).getId());
    }

    /**
     * Test is connection properties id valid.
     */
    @Test
    public void testIsConnPropsIdValid() {
        final Optional<ConnectionProperties> returnValue = Optional.of(validConnectionProperty());
        when(connectionPropertiesRepository.findById(Mockito.anyLong())).thenReturn(returnValue);

        final ConnectionProperties myConnectionProperties = connectionPropertiesValidator.isConnPropsIdValid(String.valueOf(100l));

        assertEquals(validConnectionProperty().getId(), myConnectionProperties.getId());
        assertEquals(validConnectionProperty().getProperties().get(2).getValue(), myConnectionProperties.getProperties().get(2).getValue());
        assertEquals(validConnectionProperty().getProperties().get(0).getValue(), myConnectionProperties.getProperties().get(0).getValue());
        assertEquals(validConnectionProperty().getProperties().get(1).getValue(), myConnectionProperties.getProperties().get(1).getValue());
        assertEquals(validConnectionProperty().getSubsystemUsers(), myConnectionProperties.getSubsystemUsers());
        assertEquals(validConnectionProperty().getProperties().get(2).getValue(), myConnectionProperties.getProperties().get(2).getValue());
    }

    /**
     * Test valid connection properties.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test
    public void testValidConnectionProperties() {
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(validConnectionProperty());
        connectionPropertiesValidator.validateConnectionProperties(connectionPropertiesList, buildSubsystemType());
    }

    /**
     * Test validate connection properties with different connection properties.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test
    public void testValidateConnectionPropertiesWithDifferentConnectionProperties() {
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(validConnectionProperty());
        connectionPropertiesList.add(validConnectionProperty2());
        connectionPropertiesValidator.validateConnectionProperties(connectionPropertiesList, buildSubsystemType());
    }

    /**
     * Test validate connection properties throws exception when name duplicates.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testValidateConnectionPropertiesThrowsExceptionWhenNameDuplicates() {
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(validConnectionProperty());
        connectionPropertiesList.add(validConnectionProperty2());
        connectionPropertiesList.get(1).getProperties().remove(createDefaultTestNameProperty2());
        connectionPropertiesList.get(1).getProperties().add(createDefaultTestNameProperty());
        connectionPropertiesValidator.validateConnectionProperties(connectionPropertiesList, buildSubsystemType());
    }

    /**
     * Test validate connection properties with password field but without encrypted keys should create encrypted keys with password.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test
    public void testValidateConnectionPropertiesWithPasswordFieldButWithoutEncryptedKeysShouldCreateEncryptedKeysWithPassword() {
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(validConnectionPropertyWithPasswordFieldButWithoutEncryptedKeys());
        connectionPropertiesValidator.validateConnectionProperties(connectionPropertiesList, buildSubsystemType());
    }

    /**
     * Test properties is empty.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testPropertiesIsEmpty() {
        final ConnectionProperties connectionProperty = validConnectionProperty();
        final List<Property> properties = new ArrayList<>();
        connectionProperty.setProperties(properties);
        connectionPropertiesValidator.validateConnectionProperty(connectionProperty, buildSubsystemType());
    }

    /**
     * Test duplicate property key.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testDuplicatePropertyKey() {
        final ConnectionProperties connectionProperty = validConnectionProperty();
        connectionProperty.setProperties(validProperties(Constants.$_USERNAME));
        connectionPropertiesValidator.validateConnectionProperty(connectionProperty, buildSubsystemType());
    }

    /**
     * Unexpected space in property key field.
     */
    @Test(expected = MalformedContentException.class)
    public void unexpectedSpaceInPropertyKeyField() {
        final ConnectionProperties connectionProperty = validConnectionProperty();
        connectionProperty.setProperties(validProperties(" user&ba%_!"));
        connectionPropertiesValidator.validateConnectionProperty(connectionProperty, buildSubsystemType());
    }

    /**
     * Unexpected length of chars in property key field.
     */
    @Test(expected = MalformedContentException.class)
    public void unexpectedLengthOfCharsInPropertyKeyField() {
        final ConnectionProperties connectionProperty = validConnectionProperty();
        connectionProperty.setProperties(validProperties(STRING_WITH_256_CHARS));
        connectionPropertiesValidator.validateConnectionProperty(connectionProperty, buildSubsystemType());
    }

    /**
     * Validate encrypted keys remove extra keys when asterisk is present.
     */
    @Test
    public void validateEncryptedKeysRemoveExtraKeysWhenAsteriskIsPresent() {
        final ConnectionProperties connectionProperty = validConnectionProperty();
        connectionProperty.setEncryptedKeys(validEncrpytedKeys(ENCRYPT_ALL));
        connectionPropertiesValidator.validateConnectionProperty(connectionProperty, buildSubsystemType());
        assertEquals(ENCRYPT_ALL, connectionProperty.getEncryptedKeys().get(0));
    }

    /**
     * Validate encrypted keys throws error when encrypted key not exist.
     */
    @Test(expected = MalformedContentException.class)
    public void validateEncryptedKeysThrowsErrorWhenEncryptedKeyNotExist() {
        final ConnectionProperties connectionProperties = validConnectionProperty();
        connectionProperties.setEncryptedKeys(validEncrpytedKeys("doesntexist"));
        connectionPropertiesValidator.validateConnectionProperty(connectionProperties, buildSubsystemType());
    }

}
