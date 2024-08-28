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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.oss.orchestration.so.common.error.message.ErrorMessage;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;

/**
 * The Class ExpectedResponse.
 */
public class ExpectedResponse {

    /**
     * Gets the connection properties detail.
     *
     * @return the connection properties detail
     */
    public static ConnectionProperties getConnectionPropertiesDetail() {

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        final Subsystem subsystem = new Subsystem();

        final List<SubsystemUser> subsystemUsers = new ArrayList<>();

        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(Constants._2);
        subsystemUser.setConnectionPropsId(Constants._1);
        subsystemUsers.add(subsystemUser);

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(Constants._3);
        subsystemType.setType(Constants.$_SYBSYSTEM_TYPE);

        subsystem.setHealthCheckTime(Constants.healthCheckTime);
        subsystem.setId(Constants._1);
        subsystem.setName(Constants.subsystem_name);
        subsystem.setOperationalState(OperationalState.REACHABLE);
        subsystem.setSubsystemType(subsystemType);
        subsystem.setSubsystemTypeId(Constants.subsystemTypeId);
        subsystem.setUrl(Constants.url);

        connectionProperties.setId(Constants._1);
        connectionProperties.setProperties(PropertyFactory.buildProperties(
                Constants.$_USERNAME, Constants.$_PASSWORD, Constants.$_NAME, Constants.$_TENANT));

        connectionProperties.setSubsystem(subsystem);
        connectionProperties.setSubsystemId(Constants._1);
        connectionProperties.setSubsystemUsers(subsystemUsers);
        return connectionProperties;
    }

    /**
     * Gets the service url.
     *
     * @return the service url
     */
    public static String getServiceUrl() {
        return Constants.HTTP_LOCALHOST + Constants.PORT_NUMBER_8080 + Constants.URL_POSTFIX;
    }

    /**
     * Gets the service url v2.
     *
     * @return the service url v2
     */
    public static String getServiceUrlV2() {
        return Constants.HTTP_LOCALHOST + Constants.PORT_NUMBER_8080 + Constants.URL_POSTFIX_V2;
    }

    /**
     * Gets the service response data for get.
     *
     * @return the service response data for get
     */
    public static List<ConnectionProperties> getServiceResponseDataForGet() {

        final List<ConnectionProperties> listOfConnectionProperties = new ArrayList<>();
        final ConnectionProperties connectionProperties = createConnectionPropertiesObject();
        listOfConnectionProperties.add(connectionProperties);
        return listOfConnectionProperties;
    }

    /**
     * Gets the service response data for get with all ids.
     *
     * @return the service response data for get with all ids
     */
    public static ConnectionProperties getServiceResponseDataForGetWithAllIds() {
        return createConnectionPropertiesObject();
    }

    /**
     * Gets the service response data for put with all ids and map.
     *
     * @return the service response data for put with all ids and map
     */
    public static ConnectionProperties getServiceResponseDataForPutWithAllIdsAndMap() {
        return createConnectionPropertiesObject();
    }

    /**
     * Creates the connection properties object.
     *
     * @return the connection properties
     */
    private static ConnectionProperties createConnectionPropertiesObject() {
        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setId(Constants._1);
        connectionProperties.setProperties(PropertyFactory.buildProperties(
                Constants.$_USERNAME, Constants.$_PASSWORD, Constants.$_NAME, Constants.$_TENANT));

        final List<SubsystemUser> subsystemUsers = new ArrayList<>();
        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(Constants._2);
        subsystemUser.setConnectionPropsId(Constants._1);
        subsystemUsers.add(subsystemUser);

        connectionProperties.setSubsystemId(Constants._1);
        connectionProperties.setSubsystemUsers(subsystemUsers);
        return connectionProperties;
    }

    /**
     * Gets the subsystem url.
     *
     * @return the subsystem url
     */
    public static String getSubsystemUrl() {
        return Constants.HTTP_LOCALHOST + Constants.PORT_NUMBER_8080 + Constants.URL_PREFIX;
    }

    /**
     * Gets the subsystem url v2.
     *
     * @return the subsystem url v2
     */
    public static String getSubsystemUrlV2() {
        return Constants.HTTP_LOCALHOST + Constants.PORT_NUMBER_8080 + Constants.URL_PREFIX_V2;
    }

    /**
     * Gets the subsystem type.
     *
     * @return the subsystem type
     */
    private static SubsystemType getSubsystemType() {
        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(Constants.SUBSYSTEMTYPE_ONE_ID);
        subsystemType.setType(Constants.SUBSYSTEMTYPE_ONE_TYPE);
        subsystemType.resolveCategory();

        return subsystemType;
    }

    /**
     * Gets the all subsystems.
     *
     * @param populateApiKey
     *            the populate api key
     * @return the all subsystems
     */
    public static List<Subsystem> getAllSubsystems(boolean populateApiKey) {
        final List<Subsystem> subsystemList = new ArrayList<>();
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setName(Constants.SUBSYSTEM_ONE_NAME);
        subsystemOne.setId(Constants.SUBSYSTEM_ONE_ID);
        subsystemOne.setOperationalState(Constants.SUBSYSTEM_ONE_STATE);
        subsystemOne.setSubsystemType(getSubsystemType());
        if (populateApiKey) {
            subsystemOne.setApiKey(UUID.fromString(Constants.SUBSYSTEMTYPE_API_KEY));
        } else {
            subsystemOne.setSubsystemTypeId(Constants.SUBSYSTEMTYPE_ONE_ID);
        }
        subsystemOne.setUrl(Constants.SUBSYSTEM_ONE_URL);
        subsystemOne.setConnectionProperties(Constants.SUBSYSTEM_ONE_CONNPROP);
        subsystemOne.setHealthCheckTime(Constants.SUBSYSTEM_ONE_HEALTH_CHECK_TIME);
        subsystemList.add(subsystemOne);
        return subsystemList;
    }

    /**
     * Gets the subsystem.
     *
     * @return the subsystem
     */
    public static Subsystem getSubsystem() {
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setName(Constants.SUBSYSTEM_ONE_NAME);
        subsystemOne.setId(Constants.SUBSYSTEM_ONE_ID);
        subsystemOne.setOperationalState(Constants.SUBSYSTEM_ONE_STATE);
        subsystemOne.setSubsystemType(getSubsystemType());
        subsystemOne.setSubsystemTypeId(Constants.SUBSYSTEMTYPE_ONE_ID);
        subsystemOne.setUrl(Constants.SUBSYSTEM_ONE_URL);
        subsystemOne.setConnectionProperties(Constants.SUBSYSTEM_ONE_CONNPROP);
        subsystemOne.setHealthCheckTime(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME);
        return subsystemOne;
    }

    /**
     * Gets the updated subsystem.
     *
     * @return the updated subsystem
     */
    public static Subsystem getUpdatedSubsystem() {
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setName(Constants.SUBSYSTEM_UPDATE_NAME);
        subsystemOne.setId(Constants.SUBSYSTEM_ONE_ID);
        subsystemOne.setOperationalState(Constants.SUBSYSTEM_ONE_STATE);
        subsystemOne.setSubsystemType(getSubsystemType());
        subsystemOne.setSubsystemTypeId(Constants.SUBSYSTEMTYPE_ONE_ID);
        subsystemOne.setUrl(Constants.SUBSYSTEM_ONE_URL);
        subsystemOne.setConnectionProperties(Constants.SUBSYSTEM_ONE_CONNPROP);
        subsystemOne.setHealthCheckTime(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME);
        return subsystemOne;
    }

    /**
     * Gets the subsystem user.
     *
     * @return the subsystem user
     */
    public static SubsystemUser getSubsystemUser() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(Constants.SUBSYSTEM_USER_ID);
        subsystemUser.setConnectionPropsId(Constants.SUBSYSTEM_USER_CONNPROP_ID);
        subsystemUser.setConnectionProperties(Constants.SUBSYSTEM_USER_CONNPROP);
        return subsystemUser;
    }

    /**
     * Gets the paginated subsystems.
     *
     * @param populateApiKey
     *            the populate api key
     * @return the paginated subsystems
     */
    public static SubsystemList getPaginatedSubsystems(boolean populateApiKey) {
        final ConnectionProperties connPropOne = new ConnectionProperties();
        connPropOne.setId(Constants.CONN_PROP_ID_1);
        connPropOne.setSubsystemId(Constants.CONN_PROP_SUBSYSTEM_ID);
        connPropOne.setProperties(PropertyFactory.buildProperties(
                Constants.CONN_PROP_USERNAME_1, Constants.CONN_PROP_PASSWORD_1,
                Constants.CONN_PROP_NAME_1, Constants.CONN_PROP_TENANT_1));

        final SubsystemList subsystemList = new SubsystemList();
        final List<Subsystem> list = new ArrayList<>();
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setName(Constants.SUBSYSTEM_ONE_NAME);
        subsystemOne.setId(Constants.SUBSYSTEM_ONE_ID);
        subsystemOne.setOperationalState(Constants.SUBSYSTEM_ONE_STATE);
        subsystemOne.setSubsystemType(getSubsystemType());
        if (populateApiKey) {
            subsystemOne.setApiKey(UUID.fromString(Constants.SUBSYSTEMTYPE_API_KEY));
        } else {
            subsystemOne.setSubsystemTypeId(Constants.SUBSYSTEMTYPE_ONE_ID);
        }
        subsystemOne.setUrl(Constants.SUBSYSTEM_ONE_URL);
        subsystemOne.setConnectionProperties(Constants.SUBSYSTEM_ONE_CONNPROP);
        subsystemOne.setHealthCheckTime(Constants.SUBSYSTEM_ONE_HEALTH_CHECK_TIME);
        subsystemOne.setConnectionProperties(Collections.singletonList(connPropOne));
        list.add(subsystemOne);
        subsystemList.setItems(list);
        subsystemList.setTotal(list.size());
        return subsystemList;
    }

    /**
     * Gets the paginated subsystems list.
     *
     * @param populateApiKey
     *            the populate api key
     * @return the paginated subsystems list
     */
    public static List<Subsystem> getPaginatedSubsystemsList(boolean populateApiKey) {
        final ConnectionProperties connPropOne = new ConnectionProperties();
        connPropOne.setId(Constants.CONN_PROP_ID_1);
        connPropOne.setSubsystemId(Constants.CONN_PROP_SUBSYSTEM_ID);
        connPropOne.setProperties(PropertyFactory.buildProperties(
                Constants.CONN_PROP_USERNAME_1, Constants.CONN_PROP_PASSWORD_1,
                Constants.CONN_PROP_NAME_1, Constants.CONN_PROP_TENANT_1));
        final List<Subsystem> list = new ArrayList<>();
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setName(Constants.SUBSYSTEM_ONE_NAME);
        subsystemOne.setId(Constants.SUBSYSTEM_ONE_ID);
        subsystemOne.setOperationalState(Constants.SUBSYSTEM_ONE_STATE);
        subsystemOne.setSubsystemType(getSubsystemType());
        if (populateApiKey) {
            subsystemOne.setApiKey(UUID.fromString(Constants.SUBSYSTEMTYPE_API_KEY));
        } else {
            subsystemOne.setSubsystemTypeId(Constants.SUBSYSTEMTYPE_ONE_ID);
        }
        subsystemOne.setUrl(Constants.SUBSYSTEM_ONE_URL);
        subsystemOne.setConnectionProperties(Constants.SUBSYSTEM_ONE_CONNPROP);
        subsystemOne.setHealthCheckTime(Constants.SUBSYSTEM_ONE_HEALTH_CHECK_TIME);
        subsystemOne.setConnectionProperties(Arrays.asList(connPropOne));
        list.add(subsystemOne);
        return list;
    }

    /**
     * Gets the filtered id.
     *
     * @return the filtered id
     */
    public static List<Object> getFilteredId() {
        final List<Object> filteredId = new ArrayList<>();
        filteredId.add(Constants.FILTERED_ID_1);
        filteredId.add(Constants.FILTERED_ID_2);
        return filteredId;
    }

    /**
     * Gets the filtered fields.
     *
     * @return the filtered fields
     */
    public static List<Subsystem> getFilteredFields() {
        final List<Subsystem> filteredSubsystems = new ArrayList<>();
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setName(Constants.SUBSYSTEM_ONE_NAME);
        subsystemOne.setId(Constants.SUBSYSTEM_ONE_ID);
        filteredSubsystems.add(subsystemOne);
        return filteredSubsystems;
    }

    /**
     * Gets the filtered response by not exist field.
     *
     * @return the filtered response by not exist field
     */
    public static List<Object> getFilteredResponseByNotExistField() {
        return new ArrayList<>();
    }

    /**
     * Gets the ids.
     *
     * @return the ids
     */
    public static List<String> getIds() {
        final List<String> ids = new LinkedList<>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        return ids;
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public static List<Object> getFields() {
        final String field = "subsystemOne";
        final List<Object> listOfFields = new ArrayList<>();
        listOfFields.add(field);
        return listOfFields;
    }

    /**
     * Gets the multiple fields.
     *
     * @return the multiple fields
     */
    public static List<Object> getMultipleFields() {
        String field = "subsystemOne";
        final List<Object> listOfFields = new ArrayList<>();
        listOfFields.add(field);
        field = "1";
        listOfFields.add(field);
        return listOfFields;
    }

    /**
     * Gets the null error message.
     *
     * @return the null error message
     */
    public static ErrorMessage getNullErrorMessage() {
        return null;
    }
}