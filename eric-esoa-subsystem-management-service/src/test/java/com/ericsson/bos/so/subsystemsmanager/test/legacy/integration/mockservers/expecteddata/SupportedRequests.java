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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * The Class SupportedRequests.
 */
public class SupportedRequests {

    /**
     * Gets the subsystem onboarding request.
     *
     * @param name
     *            the name
     * @return the subsystem onboarding request
     */
    public static Subsystem getSubsystemOnboardingRequest(final String name) {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(userList);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(name);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 1);
        subsystemOnboardingRequest.setAdapterLink(null);

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(1L);
        subsystemType.setType("DomainManager");
        subsystemOnboardingRequest.setSubsystemType(subsystemType);
        subsystemOnboardingRequest.setVendor(Constants.VENDOR_NAME);

        return subsystemOnboardingRequest;
    }

    /**
     * Gets the subsystem onboarding request NFVO.
     *
     * @param name
     *            the name
     * @return the subsystem onboarding request NFVO
     */
    public static Subsystem getSubsystemOnboardingRequestNFVO(final String name) {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        // creating name, user-name , user password in the below line.
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(userList);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(name);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 2);
        subsystemOnboardingRequest.setAdapterLink("test-adapter-link");

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(2L);
        subsystemType.setType("NFVO");
        subsystemOnboardingRequest.setSubsystemType(subsystemType);
        subsystemOnboardingRequest.setVendor(Constants.VENDOR_NAME);

        return subsystemOnboardingRequest;
    }

    /**
     * Gets the subsystem onboarding request for same name.
     *
     * @param name
     *            the name
     * @return the subsystem onboarding request for same name
     */
    public static Subsystem getSubsystemOnboardingRequestForSameName(final String name) {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(userList);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(name);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 1);

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(1L);
        subsystemType.setType("DomainManager");
        subsystemOnboardingRequest.setSubsystemType(subsystemType);

        return subsystemOnboardingRequest;
    }

    /**
     * Gets the subsystem onboarding request with invalid subsystem type.
     *
     * @param string
     *            the string
     * @return the subsystem onboarding request with invalid subsystem type
     */
    public static Subsystem getSubsystemOnboardingRequestWithInvalidSubsystemType(final String string) {
        final Subsystem subsystem = getSubsystemOnboardingRequest("invalid Subsystem Type");
        final SubsystemType subsystemType = new SubsystemType();

        subsystemType.setId(3L);
        subsystemType.setType("FAKE");

        subsystem.setSubsystemType(subsystemType);
        return subsystem;
    }

    /**
     * Gets the subsystem onboarding request with invalid encrypted key.
     *
     * @return the subsystem onboarding request with invalid encrypted key
     */
    public static Subsystem getSubsystemOnboardingRequestWithInvalidEncryptedKey() {
        final Subsystem subsystem = getSubsystemOnboardingRequest("invalid Subsystem Type");
        subsystem.setSubsystemTypeId(Constants.INVALID_SUBSYSTEM_TYPE_ID);
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add("UNIQUE");
        subsystem.getConnectionProperties().get(0).setEncryptedKeys(encryptedKeys);
        return subsystem;
    }

    /**
     * Gets the subsystem onboarding request with partial fields.
     *
     * @return the subsystem onboarding request with partial fields
     */
    public static Subsystem getSubsystemOnboardingRequestWithPartialFields() {
        final Subsystem subsystem = getSubsystemOnboardingRequest("null Subsystem Type");
        subsystem.setSubsystemType(null);

        return subsystem;
    }

    /**
     * Gets the valid connection properties.
     *
     * @return the valid connection properties
     */
    public static ConnectionProperties getValidConnectionProperties() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(userList);
        return connectionProperties;
    }

    /**
     * Gets the in valid connection properties.
     *
     * @param extraKey
     *            the extra key
     * @return the in valid connection properties
     */
    public static List<ConnectionProperties> getInValidConnectionProperties(String extraKey) {
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final List<Property> properties = new ArrayList<>();
        final Property p1 = new Property();
        p1.setKey("key1");
        p1.setValue("value");
        final Property p2 = new Property();
        p2.setKey(extraKey);
        p2.setValue("value");
        properties.add(p1);
        properties.add(p2);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(properties);
        connectionProperties.setSubsystemUsers(userList);

        connectionPropertiesList.add(connectionProperties);
        return connectionPropertiesList;
    }

    /**
     * Gets the valid patched connection properties.
     *
     * @return the valid patched connection properties
     */
    public static ConnectionProperties getValidPatchedConnectionProperties() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildProperties(
                "patched_username", "test-password", "test-name", "test-tenant"));
        connectionProperties.setSubsystemUsers(userList);
        return connectionProperties;
    }

    /**
     * Gets the connection properties with no properties.
     *
     * @return the connection properties with no properties
     */
    public static ConnectionProperties getConnectionPropertiesWithNoProperties() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setSubsystemUsers(userList);

        return connectionProperties;
    }

    /**
     * Gets the connection properties with invalid subsystem id.
     *
     * @return the connection properties with invalid subsystem id
     */
    public static ConnectionProperties getConnectionPropertiesWithInvalidSubsystemId() {
        final ConnectionProperties connectionProperties = getValidConnectionProperties();
        connectionProperties.setSubsystemId(Constants.INVALID_SUBSYSTEM_TYPE_ID);
        return connectionProperties;
    }

    /**
     * Gets the valid subsystem user.
     *
     * @return the valid subsystem user
     */
    public static SubsystemUser getValidSubsystemUser() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(Constants._274);
        return subsystemUser;
    }

    /**
     * Creates the list of connection properties.
     *
     * @param numOfConnectionProperties
     *            the num of connection properties
     * @return the array list
     */
    public static ArrayList<ConnectionProperties> createListOfConnectionProperties(int numOfConnectionProperties) {
        final int propertiesCount = 10;

        final ArrayList<ConnectionProperties> connectionProperties = new ArrayList<>();
        for (int cpIndex = 0; cpIndex <= numOfConnectionProperties; cpIndex++) {
            final ArrayList<Property> properties = new ArrayList<>();
            final ConnectionProperties connectionProperty = new ConnectionProperties();
            for (int propertyIndex = 0; propertyIndex <= propertiesCount; propertyIndex++) {
                final Property property = new Property();
                property.setKey(RandomStringUtils.randomAlphanumeric(10));
                property.setValue(RandomStringUtils.randomAlphanumeric(10));
                properties.add(property);
            }

            properties.addAll(PropertyFactory.buildProperties("cp-" + cpIndex));

            connectionProperty.setProperties(properties);
            connectionProperties.add(connectionProperty);
        }

        return connectionProperties;
    }

}
