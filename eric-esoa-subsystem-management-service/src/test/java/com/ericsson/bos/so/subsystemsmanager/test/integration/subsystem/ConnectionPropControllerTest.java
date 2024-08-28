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
package com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.DEFAULT_SUBSYSTEM_TYPE_NFVO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_TWO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_TENANT;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.GetSubsystemByIdIntegrationV2Test.INTERNAL_ERROR_CODE_SSM_C_09;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.CONN_PROP_URL;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SUBSYSTEMS;
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
 * The Class ConnectionPropControllerTest.
 */
public class ConnectionPropControllerTest extends BaseIntegrationTest {

    public static final String SUBSYSTEMS_V1_BASE_PATH = Constants.HTTP_LOCALHOST + Constants.PORT_NUMBER_8080 + Constants.URL_PREFIX;

    private static final String TEST_USERNAME_KEY = "userName";

    private static final String TEST_PASSWORD_KEY = "password";

    private static final String TEST_TENANT_NAME_KEY = "tenantName";

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN new subsystem id property WHEN patch connection property THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_newSubsystemIdProperty_WHEN_patchConnectionProperty_THEN_isCreated() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put("username", TEST_UPDATED_USERNAME);

        // when
        response = patchConnectionPropertyV1(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(TEST_UPDATED_USERNAME, returnedConnectionProperty.getProperties().get(PROPERTY_INDEX_ONE).getValue());
    }

    /**
     * GIVEN new password property WHEN patch connection property THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_newPasswordProperty_WHEN_patchConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put(TEST_PASSWORD_KEY, DECRYPTED_PASSWORD_VALUE_TWO);

        // when
        response = patchConnectionPropertyV1(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(returnedConnectionProperty.getSubsystemId(), subsystem.getId());
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(0).getValue());
    }

    /**
     * GIVEN invalid property WHEN patch connection property THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_invalidProperty_WHEN_patchConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final Map<String, String> mappedProperties = new HashMap<>();
        mappedProperties.put(TEST_USERNAME_KEY, TEST_USERNAME);
        mappedProperties.put(TEST_PASSWORD_KEY, DECRYPTED_PASSWORD_VALUE_ONE);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(
                        ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildProperties(mappedProperties)));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put(TEST_TENANT_NAME_KEY, TEST_UPDATED_TENANT);
        patchRequestFields.put(TEST_PASSWORD_KEY, DECRYPTED_PASSWORD_VALUE_TWO);
        patchRequestFields.put("invalid-property-key", "invalid-property-value");

        // when
        response = patchConnectionPropertyV1(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and the invalid property is not present
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(3, returnedConnectionProperty.getProperties().size());
        assertEquals(TEST_PASSWORD_KEY, returnedConnectionProperty.getProperties().get(0).getKey());
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(0).getValue());
        assertEquals(TEST_USERNAME_KEY, returnedConnectionProperty.getProperties().get(1).getKey());
        assertEquals(TEST_USERNAME, returnedConnectionProperty.getProperties().get(1).getValue());
        assertEquals(TEST_TENANT_NAME_KEY, returnedConnectionProperty.getProperties().get(2).getKey());
        assertEquals(TEST_UPDATED_TENANT, returnedConnectionProperty.getProperties().get(2).getValue());
    }

    /**
     * GIVEN subsystem WHEN get connection property THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getConnectionProperty_THEN_isOk() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = getConnectionPropertyByIdV1(subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(4, returnedConnectionProperty.getProperties().size());
        assertEquals(DECRYPTED_PASSWORD_VALUE_ONE, returnedConnectionProperty.getProperties().get(0).getValue());
    }

    /**
     * GIVEN subsystem WHEN put connection property THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_putConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<ConnectionProperties> connectionPropertiesOne =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final List<ConnectionProperties> connectionPropertiesTwo =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory
                        .buildProperties(TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT)));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionPropertiesOne);
        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionPropertiesOne.get(0).getId());

        // when
        response = putConnectionPropertyV1(connectionPropertiesTwo, subsystemId, connectionPropertyId);

        //then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(0).getValue());
        assertEquals(TEST_UPDATED_USERNAME, returnedConnectionProperty.getProperties().get(1).getValue());
        assertEquals(TEST_UPDATED_PROPERTY_NAME, returnedConnectionProperty.getProperties().get(2).getValue());
        assertEquals(TEST_UPDATED_TENANT, returnedConnectionProperty.getProperties().get(3).getValue());
    }

    /**
     * GIVEN subsystem WHEN post connection property THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_postConnectionProperty_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        final List<Property> propertyListOne =
                PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<Property> propertyListTwo =
                PropertyFactory.buildProperties(
                        TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT);
        final List<ConnectionProperties> connectionPropertiesOne =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final List<ConnectionProperties> connectionPropertiesTwo =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListTwo));

        final Subsystem subsystem =
                subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE_NFVO, connectionPropertiesOne);
        final String subsystemId = String.valueOf(subsystem.getId());
        connectionPropertiesTwo.get(0).setSubsystem(subsystem);
        connectionPropertiesTwo.get(0).setSubsystemId(subsystem.getId());

        // when
        response = postConnectionPropertyV1(connectionPropertiesTwo, subsystemId);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final ConnectionProperties returnedConnectionProperty = extractFromResponse(response, new TypeReference<ConnectionProperties>() {});
        assertEquals(DECRYPTED_PASSWORD_VALUE_TWO, returnedConnectionProperty.getProperties().get(0).getValue());
        assertEquals(TEST_UPDATED_USERNAME, returnedConnectionProperty.getProperties().get(1).getValue());
        assertEquals(TEST_UPDATED_PROPERTY_NAME, returnedConnectionProperty.getProperties().get(2).getValue());
        assertEquals(TEST_UPDATED_TENANT, returnedConnectionProperty.getProperties().get(3).getValue());
    }

    /**
     * GIVEN subsystem WHEN delete connection property THEN is no content.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystem_WHEN_deleteConnectionProperty_THEN_isNoContent() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connPropId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = deleteConnectionPropertyV1(subsystemId, connPropId);

        // then
        response.andExpect(status().isNoContent());
    }

    /**
     * GIVEN kms will return error WHEN get connection property THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_getConnectionProperty_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());

        // when
        response = getConnectionPropertyByIdV1(subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms will return error WHEN patch connection property THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_patchConnectionProperty_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionProperties);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionProperties.get(0).getId());
        final Map<String, Object> patchRequestFields = new HashMap<>();
        patchRequestFields.put(TEST_PASSWORD_KEY, DECRYPTED_PASSWORD_VALUE_TWO);

        // when
        response = patchConnectionPropertyV1(subsystemId, connectionPropertyId, patchRequestFields);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms will return error WHEN put connection property THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_putConnectionProperty_THEN_isInternalServiceError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<ConnectionProperties> connectionPropertiesOne =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final List<ConnectionProperties> connectionPropertiesTwo =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory
                        .buildProperties(TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT)));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, connectionPropertiesOne);

        final String subsystemId = String.valueOf(subsystem.getId());
        final String connectionPropertyId = String.valueOf(connectionPropertiesOne.get(0).getId());

        // when
        response = putConnectionPropertyV1(connectionPropertiesTwo, subsystemId, connectionPropertyId);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms will return error WHEN post connection property THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_postConnectionProperty_THEN_isInternalServerError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));

        final List<Property> propertyListOne =
                PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<Property> propertyListTwo =
                PropertyFactory.buildProperties(TEST_UPDATED_USERNAME, DECRYPTED_PASSWORD_VALUE_TWO, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT);
        final List<ConnectionProperties> connectionPropertiesOne =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final List<ConnectionProperties> connectionPropertiesTwo =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListTwo));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE_NFVO, connectionPropertiesOne);
        final String subsystemId = String.valueOf(subsystem.getId());
        connectionPropertiesTwo.get(0).setSubsystem(subsystem);
        connectionPropertiesTwo.get(0).setSubsystemId(subsystem.getId());

        // when
        response = postConnectionPropertyV1(connectionPropertiesTwo, subsystemId);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Gets the connection property by id V1.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertyId the connection property id
     * @return the connection property by id V1
     * @throws Exception the exception
     */
    private ResultActions getConnectionPropertyByIdV1(String subsystemId, String connectionPropertyId) throws Exception {
        final String url = buildConnectionPropertyByIdUrl(SUBSYSTEMS_V1_BASE_PATH, subsystemId, connectionPropertyId);

        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Put connection property V1.
     *
     * @param connectionProperties the connection properties
     * @param subsystemId the subsystem id
     * @param connectionPropertyId the connection property id
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions putConnectionPropertyV1(List<ConnectionProperties> connectionProperties, 
            String subsystemId, String connectionPropertyId) throws Exception {
        final String url = buildConnectionPropertyByIdUrl(SUBSYSTEMS_V1_BASE_PATH, subsystemId, connectionPropertyId);
        return mockMvc.perform(put(url)
                .content(new ObjectMapper().writeValueAsString(connectionProperties.get(0)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Post connection property V1.
     *
     * @param connectionProperties the connection properties
     * @param subsystemId the subsystem id
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions postConnectionPropertyV1(List<ConnectionProperties> connectionProperties, String subsystemId) throws Exception {
        final String url = buildConnectionPropertyUrl(SUBSYSTEMS_V1_BASE_PATH, subsystemId);
        return mockMvc.perform(post(url)
                .content(new ObjectMapper().writeValueAsString(connectionProperties.get(0)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Patch connection property V1.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertyId the connection property id
     * @param patchRequestFields the patch request fields
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions patchConnectionPropertyV1(String subsystemId,
            String connectionPropertyId, Map<String, Object> patchRequestFields) throws Exception {
        final String url = buildConnectionPropertyByIdUrl(SUBSYSTEMS_V1_BASE_PATH, subsystemId, connectionPropertyId);
        return mockMvc.perform(patch(url)
                .content(new ObjectMapper().writeValueAsString(patchRequestFields))
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Delete connection property V1.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertyId the connection property id
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions deleteConnectionPropertyV1(String subsystemId, String connectionPropertyId) throws Exception {
        final String url = buildConnectionPropertyByIdUrl(SUBSYSTEMS_V1_BASE_PATH, subsystemId, connectionPropertyId);
        return mockMvc.perform(delete(url));
    }

    /**
     * Builds the connection property by id url.
     *
     * @param basePath the base path
     * @param subsystemId the subsystem id
     * @param connectionPropertyId the connection property id
     * @return the string
     */
    public static String buildConnectionPropertyByIdUrl(String basePath, String subsystemId, String connectionPropertyId) {
        return UriComponentsBuilder.fromUriString(basePath)
                .pathSegment(SUBSYSTEMS, subsystemId, CONN_PROP_URL, connectionPropertyId)
                .build()
                .toString();
    }

    /**
     * Builds the connection property url.
     *
     * @param basePath the base path
     * @param subsystemId the subsystem id
     * @return the string
     */
    public static String buildConnectionPropertyUrl(String basePath, String subsystemId) {
        return UriComponentsBuilder.fromUriString(basePath)
                .pathSegment(SUBSYSTEMS, subsystemId, CONN_PROP_URL)
                .build()
                .toString();
    }

}
