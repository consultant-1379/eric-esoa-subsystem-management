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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.fieldfilter.v2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemsServiceV2;
import com.ericsson.bos.so.subsystemsmanager.business.exception.FailedJSONProcessingException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemsManagerException;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterEntity;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterUtil;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v2.SubsystemFilterServiceV2Impl;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The Class SubsystemFilterServiceV2ImplTest.
 */
public class SubsystemFilterServiceV2ImplTest {

    public static final String FAILED_JSON_PROCESSING = "Failed to process JSON object.";

    private static final String NFVO = "NFVO";

    private static final String TEST_NAME = "test-name";

    private static final String TEST_URL = "test-url";

    private static final String _15_00_00 = "15:00:00";

    private static final Long _1 = 1L;

    private static final String fields = "id,name";

    private static final String field = "name";

    private static final String invalidField = "foo";

    private static final String invalidFields = "foo,bar";

    @Mock
    SubsystemFilterUtil subsystemExtractFieldsUtil;

    @Mock
    SubsystemsServiceV2 subsystemsServiceV2;

    @Mock
    KmsServiceImpl kmsService;

    @Mock
    EncryptDecryptConnectionPropertiesUtil encryptDecryptConnectionPropertiesUtil;

    @InjectMocks
    SubsystemFilterServiceImpl subsystemFilterServiceImpl;

    @InjectMocks
    SubsystemFilterServiceV2Impl subsystemFilterServiceV2Impl;

    /**
     * Initialize.
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Filter response fields test.
     */
    @Test
    public void filterResponseFieldsTest() {
        final List<SubsystemFilterEntity> subsystemJsonFilterList = new ArrayList<SubsystemFilterEntity>();
        final SubsystemFilterEntity subsystemFilterEntity = createSubsystemFilterEntity();
        subsystemJsonFilterList.add(subsystemFilterEntity);

        final List<Subsystem> listOfSubsystems = new ArrayList<Subsystem>();
        final Subsystem subsystemOne = createSubsystem();

        listOfSubsystems.add(subsystemOne);

        when(subsystemsServiceV2.getAllSubsystems(Constants.TENANT_NAME)).thenReturn(listOfSubsystems);
        subsystemFilterServiceV2Impl.filterResponseFields(fields, Constants.TENANT_NAME);
        when(kmsService.decryptProperty(subsystemOne.getConnectionProperties().toString()))
                .thenReturn(subsystemOne.getConnectionProperties().get(0).getProperties().get(0).getValue());
        verify(subsystemExtractFieldsUtil, times(1)).extractFieldsFromSubsystemList(any(), any());
    }

    /**
     * Gets the subsystem by id as filter entity test.
     */
    @Test
    public void getByIdAsFilterEntityTest() {
        final Subsystem subsystem = createSubsystem();
        final String subsystemId = subsystem.getId().toString();

        when(subsystemsServiceV2.getSubsystemById(subsystemId, Constants.TENANT_NAME)).thenReturn(createSubsystem());
        final SubsystemFilterEntity result = subsystemFilterServiceV2Impl.getByIdAsFilterEntity(subsystemId, Constants.TENANT_NAME);
        assertEquals(result.getId().toString(), subsystem.getId().toString());
        assertEquals(result.getHealthCheckTime(), subsystem.getHealthCheckTime());
        assertEquals(result.getName(), subsystem.getName());
        assertEquals(result.getOperationalState(), subsystem.getOperationalState());
        assertEquals(result.getSubsystemType(), subsystem.getSubsystemType());
        assertEquals(result.getApiKey(), subsystem.getApiKey());
        assertEquals(result.getUrl(), subsystem.getUrl());

    }

    /**
     * Gets all the subsystems as filter entities test.
     */
    @Test
    public void getAllAsFilterEntitiesTest() {
        final Subsystem subsystem = createSubsystem();
        final List<Subsystem> listOfSubsystems = new ArrayList<Subsystem>();
        listOfSubsystems.add(subsystem);

        when(subsystemsServiceV2.getAllSubsystems(Constants.TENANT_NAME)).thenReturn(listOfSubsystems);

        final List<SubsystemFilterEntity> result = subsystemFilterServiceImpl.getAllAsFilterEntities(listOfSubsystems, true);
        assertEquals(result.get(0).getId(), listOfSubsystems.get(0).getId());

        assertEquals(result.get(0).getId().toString(), listOfSubsystems.get(0).getId().toString());
        assertEquals(result.get(0).getHealthCheckTime(), listOfSubsystems.get(0).getHealthCheckTime());
        assertEquals(result.get(0).getName(), listOfSubsystems.get(0).getName());
        assertEquals(result.get(0).getOperationalState(), listOfSubsystems.get(0).getOperationalState());
        assertEquals(result.get(0).getSubsystemType(), listOfSubsystems.get(0).getSubsystemType());
        assertEquals(result.get(0).getApiKey(), listOfSubsystems.get(0).getApiKey());
        assertEquals(result.get(0).getUrl(), listOfSubsystems.get(0).getUrl());
    }

    /**
     * Filter response fields test with subsystem id and two more fields.
     */
    @Test
    public void filterResponseFieldsTestWithSubsystemIdAndTwoFields() {

        final String subsystemId = createSubsystem().getId().toString();

        final List<Subsystem> listOfSubsystems = new ArrayList<Subsystem>();
        final Subsystem subsystemOne = createSubsystem();
        listOfSubsystems.add(subsystemOne);

        when(subsystemsServiceV2.getSubsystemById(subsystemId, Constants.TENANT_NAME)).thenReturn(createSubsystem());

        subsystemFilterServiceV2Impl.filterResponseSingleFieldFromKnownSubsystem(fields, subsystemId, Constants.TENANT_NAME);
        verify(subsystemExtractFieldsUtil, times(1)).extractSingleFieldFromSubsystemList(any(), any());
    }

    /**
     * Filter response fields test with subsystem id and one field.
     */
    @Test
    public void filterResponseFieldsTestWithSubsystemIdAndOneField() {

        final String subsystemId = createSubsystem().getId().toString();

        final List<Subsystem> listOfSubsystems = new ArrayList<Subsystem>();
        final Subsystem subsystemOne = createSubsystem();
        listOfSubsystems.add(subsystemOne);

        when(subsystemsServiceV2.getSubsystemById(subsystemId, Constants.TENANT_NAME)).thenReturn(createSubsystem());

        subsystemFilterServiceV2Impl.filterResponseSingleFieldFromKnownSubsystem(field, subsystemId, Constants.TENANT_NAME);
        verify(subsystemExtractFieldsUtil, times(1)).extractSingleFieldFromSubsystemList(any(), any());
    }

    /**
     * Filter response fields test with subsystem id and two invalid fields.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    public void filterResponseFieldsTestWithSubsystemIdAndTwoInvalidFields() throws JsonProcessingException {

        final String subsystemId = createSubsystem().getId().toString();

        final List<Subsystem> listOfSubsystems = new ArrayList<Subsystem>();
        final Subsystem subsystemOne = createSubsystem();
        listOfSubsystems.add(subsystemOne);

        when(subsystemsServiceV2.getSubsystemById(subsystemId, Constants.TENANT_NAME)).thenReturn(createSubsystem());
        when(subsystemExtractFieldsUtil.extractSingleFieldFromSubsystemList(any(), any())).thenThrow(new FailedJSONProcessingException());
        try {
            subsystemFilterServiceV2Impl.filterResponseSingleFieldFromKnownSubsystem(invalidFields, subsystemId, Constants.TENANT_NAME);
        } catch (SubsystemsManagerException ex) {
            assertThat(ex.getInternalErrorCode(), is("SSM-C-06"));
        }
        verify(subsystemExtractFieldsUtil, times(1)).extractSingleFieldFromSubsystemList(any(), any());
    }

    /**
     * Filter response fields test with subsystem id and one invalid field.
     */
    @Test
    public void filterResponseFieldsTestWithSubsystemIdAndOneInvalidField() {

        final String subsystemId = createSubsystem().getId().toString();

        final List<Subsystem> listOfSubsystems = new ArrayList<Subsystem>();
        final Subsystem subsystemOne = createSubsystem();
        listOfSubsystems.add(subsystemOne);

        when(subsystemsServiceV2.getSubsystemById(subsystemId, Constants.TENANT_NAME)).thenReturn(createSubsystem());
        when(subsystemExtractFieldsUtil.extractSingleFieldFromSubsystemList(any(), any())).thenThrow(new FailedJSONProcessingException());

        try {
            subsystemFilterServiceV2Impl.filterResponseSingleFieldFromKnownSubsystem(invalidField, subsystemId, Constants.TENANT_NAME);
        } catch (SubsystemsManagerException ex) {
            assertThat(ex.getInternalErrorCode(), is("SSM-C-06"));
        }
        verify(subsystemExtractFieldsUtil, times(1)).extractSingleFieldFromSubsystemList(any(), any());
    }

    /**
     * Creates the subsystem.
     *
     * @return the subsystem
     */
    private Subsystem createSubsystem() {
        final Subsystem subsystemOne = new Subsystem();

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

        subsystemOne.setId(_1);
        subsystemOne.setConnectionProperties(connPropsList);
        subsystemOne.setHealthCheckTime(_15_00_00);
        subsystemOne.setName(TEST_NAME);
        subsystemOne.setOperationalState(OperationalState.REACHABLE);
        subsystemOne.setUrl(TEST_URL);
        subsystemOne.setApiKey(UUID.fromString("ea5a2045-225f-4819-87d9-9bc388639354"));
        return subsystemOne;
    }

    /**
     * Creates the subsystem filter entity.
     *
     * @return the subsystem filter entity
     */
    private SubsystemFilterEntity createSubsystemFilterEntity() {

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
        subsystemFilterEntity.setApiKey(UUID.fromString("ea5a2045-225f-4819-87d9-9bc388639354"));
        return subsystemFilterEntity;
    }
}
