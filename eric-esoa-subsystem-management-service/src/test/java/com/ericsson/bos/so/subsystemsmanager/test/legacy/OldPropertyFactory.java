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
package com.ericsson.bos.so.subsystemsmanager.test.legacy;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;


/**
 * @deprecated Will be removed and replaced with {@code NewPropertyFactory}.
 */
public class OldPropertyFactory {

    public static final String NAME_PROPERTY = "name";
    public static final String USERNAME_PROPERTY = "username";
    public static final String PW_PROPERTY = "password";
    public static final String TENANT_PROPERTY = "tenant";

    public static final String TEST_NAME = "Geralt";
    public static final String TEST_USERNAME = "witcher";
    public static final String TEST_PW = "Sw0rd#123";
    public static final String TEST_TENANT = "king";

    public static final String SEMICOLON_SEPARATOR = "\":\"";
    public static final String COMMA_SEPARATOR = "\",\"";

    public static final String TEST_PROPERTIES_JSON =
        "\"" + NAME_PROPERTY + SEMICOLON_SEPARATOR
             + TEST_NAME + COMMA_SEPARATOR + USERNAME_PROPERTY + SEMICOLON_SEPARATOR + TEST_USERNAME
             + COMMA_SEPARATOR + PW_PROPERTY + SEMICOLON_SEPARATOR
             + TEST_PW + COMMA_SEPARATOR + TENANT_PROPERTY + SEMICOLON_SEPARATOR + TEST_TENANT +
        "\"";

    private OldPropertyFactory() {}

    /**
     * Creates a new OldProperty object.
     *
     * @param key the key
     * @param value the value
     * @return the property
     */
    public static Property createProperty(String key, String value) {
        final Property property = new Property();
        property.setKey(key);
        property.setEncrypted(key.equals(PW_PROPERTY));
        property.setValue(value);

        return property;
    }

    /**
     * Creates a new OldProperty object.
     *
     * @return the property
     */
    public static Property createDefaultTestNameProperty() {
        return createProperty(NAME_PROPERTY, TEST_NAME);
    }

    /**
     * Creates a new OldProperty object.
     *
     * @return the property
     */
    public static Property createDefaultTestNameProperty2() {
        return createProperty(NAME_PROPERTY, createDifferent(TEST_NAME));
    }

    /**
     * Creates a new OldProperty object.
     *
     * @return the property
     */
    public static Property createDefaultTestTenantProperty() {
        return createProperty(TENANT_PROPERTY, TEST_TENANT);
    }

    /**
     * Creates a new OldProperty object.
     *
     * @return the list< property>
     */
    public static List<Property> createDefaultTestProperties() {
        return createCustomTestProperties(TEST_USERNAME, TEST_PW, TEST_NAME, TEST_TENANT);
    }

    /**
     * Creates a new Default TestProperties.
     *
     * @return the list< property>
     */
    public static List<Property> createDefaultTestProperties2() {
        return createCustomTestProperties(createDifferent(TEST_USERNAME),
                createDifferent(TEST_PW), createDifferent(TEST_NAME), createDifferent(TEST_TENANT));
    }

    /**
     * Creates a new OldProperty object.
     *
     * @param username the username
     * @param password the password
     * @param name THEName
     * @param tenant the tenant
     * @return the list< property>
     */
    public static List<Property> createCustomTestProperties(String username, String password, String name, String tenant) {
        final List<Property> properties = new ArrayList<>();
        properties.add(createProperty(PW_PROPERTY, password));
        properties.add(createProperty(USERNAME_PROPERTY, username));
        properties.add(createProperty(NAME_PROPERTY, name));
        properties.add(createProperty(TENANT_PROPERTY, tenant));

        return properties;
    }

    private static String createDifferent(String propertyValue) {
        return propertyValue + "Diff";
    }

}