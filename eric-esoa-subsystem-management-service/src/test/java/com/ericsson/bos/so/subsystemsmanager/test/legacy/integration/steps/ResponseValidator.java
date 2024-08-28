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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * The Class ResponseValidator.
 */
public class ResponseValidator {

    /**
     * Assert expected.
     *
     * @param expectedResponseList
     *            the expected response list
     * @param actualResponseList
     *            the actual response list
     */
    public static void assertExpected(final List<Subsystem> expectedResponseList, final List<Subsystem> actualResponseList) {
        assertNotNull(expectedResponseList);
        assertNotNull(actualResponseList);
        final Subsystem expectedSubsystem = expectedResponseList.get(0);
        final Subsystem actualSubsystem = actualResponseList.get(0);
        final List<ConnectionProperties> expectedConnPropsList = expectedResponseList.get(0).getConnectionProperties();
        final List<ConnectionProperties> actualConnPropsList = actualResponseList.get(0).getConnectionProperties();

        final List<SubsystemUser> expectedUserSet = expectedResponseList.get(0).getConnectionProperties().get(0).getSubsystemUsers();
        final List<SubsystemUser> actualUserSet = actualResponseList.get(0).getConnectionProperties().get(0).getSubsystemUsers();

        assertEquals(expectedSubsystem.getName(), actualSubsystem.getName());
        assertEquals(expectedSubsystem.getOperationalState(), actualSubsystem.getOperationalState());
        assertEquals(expectedSubsystem.getSubsystemType(), actualSubsystem.getSubsystemType());
        assertEquals(expectedSubsystem.getSubsystemTypeId(), actualSubsystem.getSubsystemTypeId());
        assertEquals(expectedSubsystem.getUrl(), actualSubsystem.getUrl());
        assertEquals(expectedSubsystem.getAdapterLink(), actualSubsystem.getAdapterLink());
        assertEquals(expectedConnPropsList.size(), actualSubsystem.getConnectionProperties().size());
        assertEquals(expectedConnPropsList.get(0).getProperties().get(2).getValue(), actualConnPropsList.get(0).getProperties().get(2).getValue());
        assertEquals(expectedConnPropsList.get(0).getProperties().get(1).getValue(), actualConnPropsList.get(0).getProperties().get(1).getValue());
        assertEquals(expectedConnPropsList.get(0).getProperties().get(0).getValue(), actualConnPropsList.get(0).getProperties().get(0).getValue());
        assertEquals(expectedUserSet.size(), actualUserSet.size());
        assertEquals(expectedSubsystem.getVendor(), actualSubsystem.getVendor());
    }

    /**
     * Assert expected.
     *
     * @param expectedStatusCode
     *            the expected status code
     * @param actualStatusCode
     *            the actual status code
     */
    public static void assertExpected(final HttpStatusCode expectedStatusCode, final HttpStatusCode actualStatusCode) {
        assertNotNull(actualStatusCode);
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    /**
     * Assert expected.
     *
     * @param expectedExceptionMsg
     *            the expected exception msg
     * @param actualExceptionMsg
     *            the actual exception msg
     */
    public static void assertExpected(final String expectedExceptionMsg, final String actualExceptionMsg) {
        assertNotNull(expectedExceptionMsg);
        assertNotNull(actualExceptionMsg);
        assertTrue(actualExceptionMsg.contains(expectedExceptionMsg));
    }

    /**
     * Assert expected connection properties.
     *
     * @param allConnPropsList
     *            the all connection properties list
     * @param actualGetConnPropsResponseList
     *            the actual get connection properties response list
     */
    public static void assertExpectedConnProps(final List<ConnectionProperties> allConnPropsList,
            final List<ConnectionProperties> actualGetConnPropsResponseList) {
        assertNotNull(allConnPropsList);
        assertNotNull(actualGetConnPropsResponseList);

        final List<SubsystemUser> expectedUserSet = allConnPropsList.get(0).getSubsystemUsers();
        final List<SubsystemUser> actualUserSet = actualGetConnPropsResponseList.get(0).getSubsystemUsers();

        assertEquals(allConnPropsList.size(), actualGetConnPropsResponseList.size());

        final List<String> allConnPropsListPropertiesKeys = allConnPropsList.get(0).getProperties().stream().map(
                Property::getKey).sorted().collect(Collectors.toList());
        final List<String> actualGetConnPropsResponseListPropertiesKeys =
                actualGetConnPropsResponseList.get(0).getProperties().stream().map(Property::getKey).sorted().collect(Collectors.toList());
        assertEquals(allConnPropsListPropertiesKeys, actualGetConnPropsResponseListPropertiesKeys);

        assertEquals(expectedUserSet.size(), actualUserSet.size());
    }

    /**
     * Assert expected connection properties.
     *
     * @param allConnProps
     *            the all connection properties
     * @param connectionProperties
     *            the connection properties
     */
    public static void assertExpectedConnProps(final ConnectionProperties allConnProps, final ConnectionProperties connectionProperties) {
        final List<ConnectionProperties> allConnPropsList = new ArrayList<>();
        allConnPropsList.add(allConnProps);
        final List<ConnectionProperties> actualConnectionProperties = new ArrayList<>();
        actualConnectionProperties.add(connectionProperties);
        assertExpectedConnProps(allConnPropsList, actualConnectionProperties);
    }

    /**
     * Assert expected subsystem users.
     *
     * @param subsystemUser
     *            the subsystem user
     * @param subsystemUser2
     *            the subsystem user 2
     */
    public static void assertExpectedSubsystemUsers(final SubsystemUser subsystemUser, final SubsystemUser subsystemUser2) {
        assertNotNull(subsystemUser);
        assertNotNull(subsystemUser2);
        assertEquals(subsystemUser.getId(), subsystemUser2.getId());
    }

    /**
     * Assert expected.
     *
     * @param expectedSubsystemList
     *            the expected subsystem list
     * @param actualSubsystemList
     *            the actual subsystem list
     */
    public static void assertExpected(SubsystemList expectedSubsystemList, SubsystemList actualSubsystemList) {
        assertNotNull(actualSubsystemList);
        assertNotNull(actualSubsystemList.getItems());
        assertNotNull(actualSubsystemList.getTotal());
        assertEquals(expectedSubsystemList.getTotal(), actualSubsystemList.getTotal());
        assertEquals(expectedSubsystemList.getItems().size(), actualSubsystemList.getItems().size());

        for (int i = 0; i < actualSubsystemList.getTotal(); i++) {
            assertEquals(expectedSubsystemList.getItems().get(0).getName(), actualSubsystemList.getItems().get(0).getName());
            assertEquals(expectedSubsystemList.getItems().get(0).getHealthCheckTime(), actualSubsystemList.getItems().get(0).getHealthCheckTime());
            assertEquals(expectedSubsystemList.getItems().get(0).getOperationalState(), actualSubsystemList.getItems().get(0).getOperationalState());
            assertEquals(expectedSubsystemList.getItems().get(0).getSubsystemType(), actualSubsystemList.getItems().get(0).getSubsystemType());
            assertEquals(expectedSubsystemList.getItems().get(0).getSubsystemTypeId(), actualSubsystemList.getItems().get(0).getSubsystemTypeId());
            assertEquals(expectedSubsystemList.getItems().get(0).getUrl(), actualSubsystemList.getItems().get(0).getUrl());
            assertEquals(expectedSubsystemList.getItems().get(0).getAdapterLink(), actualSubsystemList.getItems().get(0).getAdapterLink());
        }
        final List<ConnectionProperties> actualConnPropList = actualSubsystemList.getItems().get(0).getConnectionProperties();
        final List<ConnectionProperties> expectedConnPropList = expectedSubsystemList.getItems().get(0).getConnectionProperties();
        assertEquals(expectedConnPropList.size(), actualConnPropList.size());
        for (int i = 0; i < actualConnPropList.size(); i++) {
            assertEquals(expectedConnPropList.get(0).getProperties().get(2).getValue(), actualConnPropList.get(0).getProperties().get(2).getValue());
            assertEquals(expectedConnPropList.get(0).getProperties().get(1).getValue(), actualConnPropList.get(0).getProperties().get(1).getValue());
            assertEquals(expectedConnPropList.get(0).getProperties().get(0).getValue(), actualConnPropList.get(0).getProperties().get(0).getValue());
        }
        final List<SubsystemUser> actualSubsystemUser = actualConnPropList.get(0).getSubsystemUsers();
        final List<SubsystemUser> expectedSubsystemUser = expectedConnPropList.get(0).getSubsystemUsers();
        assertEquals(expectedSubsystemUser.size(), actualSubsystemUser.size());
        for (int i = 0; i < actualSubsystemUser.size(); i++) {
            assertEquals(expectedSubsystemUser.get(0).getId(), actualSubsystemUser.get(0).getId());
        }
    }

    /**
     * Assert expected id.
     *
     * @param expectedIdList
     *            the expected id list
     * @param actualIdList
     *            the actual id list
     */
    public static void assertExpectedId(List<Long> expectedIdList, List<Object> actualIdList) {
        assertEquals(expectedIdList.size(), actualIdList.size());
        for (int i = 0; i < actualIdList.size(); i++) {
            final long id = expectedIdList.get(i);
            assertEquals((int) (id), actualIdList.get(i));
        }
    }

    /**
     * Assert expected id and name.
     *
     * @param actualSubsystemList
     *            the actual subsystem list
     * @param expectedSubsystemList
     *            the expected subsystem list
     */
    public static void assertExpectedIdAndName(List<Subsystem> actualSubsystemList,
            List<Subsystem> expectedSubsystemList) {
        assertEquals(actualSubsystemList.size(), expectedSubsystemList.size());
        for (int i = 0; i < actualSubsystemList.size(); i++) {
            assertEquals(expectedSubsystemList.get(i).getId(), actualSubsystemList.get(i).getId());
            assertEquals(expectedSubsystemList.get(i).getName(), actualSubsystemList.get(i).getName());
        }
    }

    /**
     * Assert expected.
     *
     * @param allSubsystems
     *            the all subsystems
     * @param header
     *            the header
     */
    public static void assertExpected(List<Subsystem> allSubsystems, HttpHeaders header) {
        final String total = header.get("total").get(0);
        assertNotNull(total);
        assertEquals(String.valueOf(allSubsystems.size()), total);
    }
}
