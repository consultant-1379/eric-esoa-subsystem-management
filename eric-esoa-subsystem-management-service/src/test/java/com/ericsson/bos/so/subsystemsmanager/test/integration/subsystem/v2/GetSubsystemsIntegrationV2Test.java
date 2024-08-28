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
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_ECM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_ENM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TENANT_FOR_TEST;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.GetSubsystemByIdIntegrationV2Test.INTERNAL_ERROR_CODE_SSM_C_09;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class GetSubsystemsIntegrationV2Test.
 */
public class GetSubsystemsIntegrationV2Test extends BaseIntegrationTest {

    /** The Constant INTERNAL_ERROR_CODE_SSM_C_43. */
    private static final String INTERNAL_ERROR_CODE_SSM_C_43 = "SSM-C-43";

    private String subsystemId;

    private String apiKey;
    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * Setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void testSetup() {

        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DEFAULT_PASSWORD, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final List<ConnectionProperties> connectionProperties2 = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties2);
    }

    /**
     * GIVEN subsystem WHEN get all subsystems THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getAllSubsystems_THEN_isOk() throws Exception {
        //given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        //when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final List<Subsystem> subsystems = extractFromResponse(response, new TypeReference<List<Subsystem>>() {});
        assertEquals(2, subsystems.size());
        verifyConnectionPropertyContainsExpectedValue(subsystems.get(0), PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
        verifyConnectionPropertyContainsExpectedValue(subsystems.get(1), PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN subsystem WHEN get subsystem by id THEN api key is returned.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemById_THEN_apiKeyIsReturned() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(ConnectionPropertiesFactory
                .buildConnectionProperties(PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE,
                    "testForTest", TENANT_FOR_TEST)));
        final List<ConnectionProperties> connectionProperties2 = Collections
                .singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, "enm", connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, "enm", connectionProperties2);
        final String subsystemId = String.valueOf(subsystem.getId());

        // when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems/" + subsystemId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(subsystem.getApiKey().toString()));

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DECRYPTED_PASSWORD_VALUE_ONE);
    }

    /**
     * GIVEN subsystem WHEN get subsystems with offset THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemsWithOffset_THEN_isOk() throws Exception {
        //given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems?offset=1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final List<Subsystem> subsystems = extractFromResponse(response, new TypeReference<List<Subsystem>>() {});
        assertEquals(1, subsystems.size());
    }

    /**
     * GIVEN subsystem WHEN get subsystems with invalid filters THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystem_WHEN_getSubsystemsWithInvalidFilters_THEN_isBadRequest() throws Exception {

        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems?filters=\"password:test\"")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isBadRequest());

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_43);
    }

    /**
     * GIVEN kms service returns error WHEN get all subsystems THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_getAllSubsystems_THEN_isInternalServerError() throws Exception {
        //given
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));
        //when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isInternalServerError());

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN subsystem WHEN get subsystem by invalid field THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemByInvalidField_THEN_isBadRequest() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems?select=\"name\"").contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isBadRequest());

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_43);
        checkResponseContainsErrorData("\"name\"");
    }

    /**
     * Builds the test subsystems.
     */
    private void buildTestSubsystems() {
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final List<ConnectionProperties> connectionProperties2 = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));

        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_ECM, connectionProperties2);

        subsystemId = String.valueOf(subsystem.getId());
        apiKey = subsystem.getApiKey().toString();
    }
}
