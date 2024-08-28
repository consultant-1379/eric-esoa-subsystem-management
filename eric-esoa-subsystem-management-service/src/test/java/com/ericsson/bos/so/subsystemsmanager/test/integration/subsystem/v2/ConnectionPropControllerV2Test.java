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
package com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.ConnectionPropControllerTest.buildConnectionPropertyByIdUrl;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.ConnectionPropControllerTest.buildConnectionPropertyUrl;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_TWO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_THREE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_TWO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_ECM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_TENANT;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.GetSubsystemByIdIntegrationV2Test.INTERNAL_ERROR_CODE_SSM_C_09;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class ConnectionPropControllerV2Test.
 */
public class ConnectionPropControllerV2Test extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN updated property value WHEN patch connection property THEN is created.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_updatedPropertyValue_WHEN_patchConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        final String updatedPropertyValue = "updated-name-value";
        patchRequestFields.put("name", updatedPropertyValue);

        // when
        response = patchConnectionPropertyV2(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(updatedPropertyValue, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_TWO).getValue());
    }

    /**
     * GIVEN subsystem WHEN patch connection property password THEN is created.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_patchConnectionPropertyPassword_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put(Constants.$_PASSWORD, DECRYPTED_PASSWORD_VALUE_TWO);

        // when
        response = patchConnectionPropertyV2(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ZERO).getValue());
    }

    /**
     * GIVEN subsystem without param WHEN patch connection property THEN is created.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystemWithoutParam_WHEN_patchConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final Map<String, String> mappedProperties = new HashMap<>();
        mappedProperties.put("userName", TEST_USERNAME);
        mappedProperties.put(Constants.$_PASSWORD, DECRYPTED_PASSWORD_VALUE_ONE);
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildProperties(mappedProperties)));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put("tenantName", TEST_UPDATED_TENANT);
        patchRequestFields.put(Constants.$_PASSWORD, DECRYPTED_PASSWORD_VALUE_TWO);
        patchRequestFields.put("wrongParam", "wrong");

        // when
        response = patchConnectionPropertyV2(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(3, returnedConnectionProperty.getProperties().size());
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ZERO).getValue());
        assertEquals(TEST_USERNAME, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ONE).getValue());
        assertEquals(TEST_UPDATED_TENANT, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_TWO).getValue());
    }

    /**
     * GIVEN subsystem WHEN get connection property by id THEN is ok.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getConnectionPropertyById_THEN_isOk() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);
        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = getConnectionPropertyByIdV2(subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(4, returnedConnectionProperty.getProperties().size());
        assertEquals(DEFAULT_PASSWORD, returnedConnectionProperty.getProperties().get(0).getValue());
    }

    /**
     * GIVEN subsystem WHEN put connection property THEN is created.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_putConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final List<ConnectionProperties> connectionProperties2 = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(
                                TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT)));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);
        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = putConnectionPropertyV2(connectionProperties2, subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(4, returnedConnectionProperty.getProperties().size());
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ZERO).getValue());
        assertEquals(TEST_UPDATED_USERNAME, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ONE).getValue());
        assertEquals(TEST_UPDATED_PROPERTY_NAME, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_TWO).getValue());
        assertEquals(TEST_UPDATED_TENANT, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_THREE).getValue());
    }

    /**
     * GIVEN subsystem WHEN post connection property THEN is created.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_postConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final List<ConnectionProperties> connectionProperties2 =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(
                                TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT)));

        final Subsystem subsystem =
                subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SubsystemFactory.DEFAULT_SUBSYSTEM_TYPE_NFVO, connectionProperties);
        final String subsystemId = String.valueOf(subsystem.getId());
        connectionProperties2.get(0).setSubsystem(subsystem);
        connectionProperties2.get(0).setSubsystemId(subsystem.getId());

        // when
        response = postConnectionPropertyV2(subsystemId, connectionProperties2);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(4, returnedConnectionProperty.getProperties().size());
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ZERO).getValue());
        assertEquals(TEST_UPDATED_USERNAME, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ONE).getValue());
        assertEquals(TEST_UPDATED_PROPERTY_NAME, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_TWO).getValue());
        assertEquals(TEST_UPDATED_TENANT, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_THREE).getValue());
    }

    /**
     * GIVEN subsystem WHEN delete connection property by id THEN is no content.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_deleteConnectionPropertyById_THEN_isNoContent() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connPropId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = deleteConnectionPropertyV2(subsystemId, connPropId);

        // then
        response.andExpect(status().isNoContent());
    }

    /**
     * GIVEN invalid subsystem id WHEN post connection property THEN is bad request.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void GIVEN_invalidSubsystemId_WHEN_postConnectionProperty_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final List<ConnectionProperties> connectionProperties2 =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);
        final String subsystemId = String.valueOf(subsystem.getId());
        connectionProperties2.get(0).setSubsystem(subsystem);
        connectionProperties2.get(0).setSubsystemId(subsystem.getId());

        // when
        response = postConnectionPropertyV2(subsystemId, connectionProperties2);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-B-37");
    }

    /**
     * GIVEN kms service returns error WHEN get connection property by id THEN is internal service error.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_getConnectionPropertyById_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);
        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = getConnectionPropertyByIdV2(subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms returns error WHEN put connection property THEN is internal service error.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsReturnsError_WHEN_putConnectionProperty_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final List<ConnectionProperties> connectionProperties2 =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(
                                TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT)));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);
        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = putConnectionPropertyV2(connectionProperties2, subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms service returns error WHEN post connection property THEN is internal service error.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_postConnectionProperty_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ECM);
        final String subsystemId = String.valueOf(subsystem.getId());

        final List<ConnectionProperties> connectionProperties2 =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        connectionProperties2.get(0).setSubsystemId(Long.parseLong(subsystemId));

        // when
        response = postConnectionPropertyV2(subsystemId, connectionProperties2);

        // then
        response.andExpect(status().isInternalServerError());
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms service returns error WHEN patch connection property password THEN is internal service error.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_patchConnectionPropertyPassword_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put(Constants.$_PASSWORD, DECRYPTED_PASSWORD_VALUE_TWO);

        // when
        response = patchConnectionPropertyV2(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Gets the connection property by id V2.
     *
     * @param subsystemId
     *            the subsystem id
     * @param connectionPropertyId
     *            the connection property id
     * @return the connection property by id V2
     * @throws Exception
     *             the exception
     */
    private ResultActions getConnectionPropertyByIdV2(String subsystemId, String connectionPropertyId) throws Exception {
        final String url = buildConnectionPropertyByIdUrl(pathProperties.getV2().getSubsystemManagement().getBasePath()
                , subsystemId, connectionPropertyId);
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Post connection property V2.
     *
     * @param subsystemId
     *            the subsystem id
     * @param connectionProperties
     *            the connection properties
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    private ResultActions postConnectionPropertyV2(String subsystemId, List<ConnectionProperties> connectionProperties) throws Exception {
        final String url = buildConnectionPropertyUrl(pathProperties.getV2().getSubsystemManagement().getBasePath(), subsystemId);
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(connectionProperties.get(0))));
    }

    /**
     * Put connection property V2.
     *
     * @param connectionProperties
     *            the connection properties
     * @param subsystemId
     *            the subsystem id
     * @param connectionPropertyId
     *            the connection property id
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    private ResultActions putConnectionPropertyV2(List<ConnectionProperties> connectionProperties, String subsystemId, String connectionPropertyId)
            throws Exception {
        final String url = buildConnectionPropertyByIdUrl(pathProperties.getV2().getSubsystemManagement().getBasePath(),
                subsystemId, connectionPropertyId);
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(connectionProperties.get(0))));
    }

    /**
     * Patch connection property V2.
     *
     * @param subsystemId
     *            the subsystem id
     * @param connectionPropertyId
     *            the connection property id
     * @param patchRequestFields
     *            the patch request fields
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    private ResultActions patchConnectionPropertyV2(String subsystemId, String connectionPropertyId, Map<String, Object> patchRequestFields)
            throws Exception {
        final String url = buildConnectionPropertyByIdUrl(pathProperties.getV2().getSubsystemManagement().getBasePath(),
                subsystemId, connectionPropertyId);
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(patchRequestFields)));
    }

    /**
     * Delete connection property V2.
     *
     * @param subsystemId
     *            the subsystem id
     * @param connectionPropertyId
     *            the connection property id
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    private ResultActions deleteConnectionPropertyV2(String subsystemId, String connectionPropertyId) throws Exception {
        final String url = buildConnectionPropertyByIdUrl(pathProperties.getV2().getSubsystemManagement().getBasePath(),
                subsystemId, connectionPropertyId);
        return mockMvc.perform(delete(url));
    }

}
