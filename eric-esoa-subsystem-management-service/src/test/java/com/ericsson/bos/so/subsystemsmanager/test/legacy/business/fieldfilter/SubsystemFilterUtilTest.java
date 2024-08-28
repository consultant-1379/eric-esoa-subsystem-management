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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.fieldfilter;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemsManagerException;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterEntity;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterUtil;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The Class SubsystemFilterUtilTest.
 */
public class SubsystemFilterUtilTest {
    private static final String NFVO = "NFVO";
    private static final String TEST_NAME = "test-name";
    private static final String TEST_URL = "test-url";
    private static final String _15_00_00 = "15:00:00";
    private static final Long _1 = 1L;

    @Mock
    SubsystemFilterUtil subsystemFilterUtil;

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        subsystemFilterUtil = new SubsystemFilterUtil();
    }

    /**
     * Extract fields from subnet test.
     */
    @Test
    public void extractFieldsFromSubnetTest() {
        try {
            final String fields = "id,name";
            final SubsystemFilterEntity subsystemFilterEntity = createSubsystem();

            final Subsystem result = subsystemFilterUtil.extractFieldsFromSubsystem(fields, subsystemFilterEntity);

            assertEquals(result.getId(), subsystemFilterEntity.getId());
            assertEquals(result.getName(), subsystemFilterEntity.getName());
        } catch (SubsystemsManagerException subsystemsManagerException) {
            throw subsystemsManagerException;
        }
    }

    /**
     * Extract fields from subnet list test.
     */
    @Test
    public void extractFieldsFromSubnetListTest() {
        try {
            final String fields = "id,name";
            final SubsystemFilterEntity subsystemFilterEntity = createSubsystem();

            final List<SubsystemFilterEntity> subsystemsList = new ArrayList<>();
            subsystemsList.add(subsystemFilterEntity);

            final List<Subsystem> result = subsystemFilterUtil.extractFieldsFromSubsystemList(fields, subsystemsList);

            assertEquals(result.get(0).getId(), subsystemFilterEntity.getId());
            assertEquals(result.get(0).getName(), subsystemFilterEntity.getName());
        } catch (SubsystemsManagerException subsystemsManagerException) {
            throw subsystemsManagerException;
        }
    }

    /**
     * Extract fields from connection properties test.
     */
    @Test
    public void extractFieldsFromConnectionPropertiesTest() {
        try {
            final String fields = "connectionProperties";
            final SubsystemFilterEntity subsystemFilterEntity = createSubsystem();
            subsystemFilterEntity.setId(null);
            subsystemFilterEntity.setName(null);
            subsystemFilterEntity.setOperationalState(null);
            subsystemFilterEntity.setSubsystemType(null);
            subsystemFilterEntity.setSubsystemTypeId(null);
            subsystemFilterEntity.setUrl(null);
            subsystemFilterEntity.setHealthCheckTime(null);

            final List<SubsystemFilterEntity> subsystemsList = new ArrayList<SubsystemFilterEntity>();
            subsystemsList.add(subsystemFilterEntity);
            final List<Object> result = subsystemFilterUtil.extractFieldsFromConnectionProperties(fields, subsystemsList);
            final String connection = result.get(0).toString();
            assertEquals(connection, subsystemFilterEntity.toString());
        } catch (SubsystemsManagerException subsystemsManagerException) {
            throw subsystemsManagerException;
        }
    }

    private SubsystemFilterEntity createSubsystem() {

        final SubsystemFilterEntity subsystemFilterEntity = new SubsystemFilterEntity();

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(_1);
        subsystemType.setType(NFVO);

        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(_1);
        subsystemUser.setConnectionPropsId(_1);
        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setId(_1);
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystemUsers(usersSet);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        subsystemFilterEntity.setId(_1);
        subsystemFilterEntity.setConnectionProperties(connPropsList);
        subsystemFilterEntity.setHealthCheckTime(_15_00_00);
        subsystemFilterEntity.setName(TEST_NAME);
        subsystemFilterEntity.setOperationalState(OperationalState.REACHABLE);
        subsystemFilterEntity.setUrl(TEST_URL);
        subsystemFilterEntity.setSubsystemTypeId(subsystemType.getId());
        return subsystemFilterEntity;
    }

}
