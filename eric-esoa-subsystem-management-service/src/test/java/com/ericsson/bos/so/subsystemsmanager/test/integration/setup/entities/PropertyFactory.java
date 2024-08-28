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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;

import lombok.extern.slf4j.Slf4j;


/**
 * Assists with creation of {@link Property} entity objects.
 * <p>
 * Note: Class will be renamed once Maven modules are consolidated (It will replace the existing {@code PropertyFactory})
 */
@Slf4j
public class PropertyFactory {

    public static final String NAME_PROPERTY = "name";
    public static final String USERNAME_PROPERTY = "username";
    public static final String PW_PROPERTY = "password";
    public static final String TENANT_PROPERTY = "tenant";

    public static final String DEFAULT_NAME = "test-name";
    public static final String DEFAULT_USERNAME = "test-username";
    public static final String DEFAULT_PASSWORD = "test-password";
    public static final String ENCRYPTED_PASSWORD = "vault:v1:8gsHPk3c73q3U5uz6xKZ6r/f9B2Qc1IXmbvl1ddiKj0FpNxFw==";
    public static final String DEFAULT_TENANT = "test-tenant";

    public static final String AUTH_URL_PROPERTY = "auth_url";
    public static final String AUTH_HEADERS_PROPERTY = "auth_headers";
    public static final String CLIENT_SECRET_PROPERTY = "client_secret";
    public static final String CLIENT_ID_PROPERTY = "client_id";
    public static final String AUTH_TYPE_PROPERTY = "auth_type";
    public static final String GRANT_TYPE_PROPERTY = "grant_type";

    private PropertyFactory() {
    }

    /**
     * Builds the property.
     *
     * @param key the key
     * @param value the value
     * @return the property
     */
    public static Property buildProperty(String key, String value) {
        final Property property = new Property();
        property.setKey(key);
        property.setEncrypted(PW_PROPERTY.equals(key) || CLIENT_SECRET_PROPERTY.equals(key));
        property.setValue(value);

        log.debug(Constants.BUILT, property);
        return property;
    }

    /**
     * Builds the default properties.
     *
     * @return the list
     */
    public static List<Property> buildDefaultProperties() {
        return buildProperties(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_NAME, DEFAULT_TENANT);
    }

    /**
     * Builds the properties.
     *
     * @param name the name
     * @return the list
     */
    public static List<Property> buildProperties(String name) {
        return buildProperties(DEFAULT_USERNAME, DEFAULT_PASSWORD, name, DEFAULT_TENANT);
    }

    /**
     * Builds the properties.
     *
     * @param username the username
     * @param password the password
     * @param name the name
     * @param tenant the tenant
     * @return the list
     */
    public static List<Property> buildProperties(String username, String password, String name, String tenant) {
        final List<Property> properties = new ArrayList<>();
        properties.add(buildProperty(PW_PROPERTY, password));
        properties.add(buildProperty(USERNAME_PROPERTY, username));
        properties.add(buildProperty(NAME_PROPERTY, name));
        properties.add(buildProperty(TENANT_PROPERTY, tenant));

        log.debug(Constants.BUILT, properties);
        return properties;
    }

    /**
     * Builds the properties.
     *
     * @param username the username
     * @param password the password
     * @param name the name
     * @return the list
     */
    public static List<Property> buildProperties(String username, String password, String name) {
        final List<Property> properties = new ArrayList<>();
        properties.add(buildProperty(PW_PROPERTY, password));
        properties.add(buildProperty(USERNAME_PROPERTY, username));
        properties.add(buildProperty(NAME_PROPERTY, name));

        log.debug(Constants.BUILT, properties);
        return properties;
    }

    /**
     * Builds the properties.
     *
     * @param mappedProperties the mapped properties
     * @return the list
     */
    public static List<Property> buildProperties(Map<String, String> mappedProperties) {
        final List<Property> resultProperties = new ArrayList<>();
        mappedProperties.forEach((key, value) -> resultProperties.add(buildProperty(key, value)));

        log.debug(Constants.BUILT, resultProperties);
        return resultProperties;
    }

    /**
     * Builds the properties.
     *
     * @param authUrl the auth url
     * @param authHeaders the auth headers
     * @param authType the auth type
     * @param clientId the client id
     * @param clientSecret the client secret
     * @param grantType the grant type
     * @return the list
     */
    public static List<Property> buildProperties(String authUrl, String authHeaders, String authType, String clientId,
            String clientSecret,String grantType) {
        final List<Property> properties = new ArrayList<>();
        properties.add(buildProperty(AUTH_URL_PROPERTY, authUrl));
        properties.add(buildProperty(AUTH_HEADERS_PROPERTY, authHeaders));
        properties.add(buildProperty(AUTH_TYPE_PROPERTY, authType));
        properties.add(buildProperty(CLIENT_ID_PROPERTY, clientId));
        properties.add(buildProperty(CLIENT_SECRET_PROPERTY, clientSecret));
        properties.add(buildProperty(GRANT_TYPE_PROPERTY, grantType));
        log.debug(Constants.BUILT, properties);
        return properties;
    }
}