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
package com.ericsson.bos.so.subsystemsmanager.test.integration.kms;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.test.integration.KmsIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_ECM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.GetSubsystemByIdIntegrationV2Test.INTERNAL_ERROR_CODE_SSM_C_09;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class KmsRetryIntegrationTest.
 */
@TestPropertySource(properties = { "spring.cloud.vault.enabled=true"})
public class KmsRetryIntegrationTest extends KmsIntegrationTest {


    /**
     * Test setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void testSetup() throws IOException {
        setupKmsMockWebServer();
    }

    /**
     * Test tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void testTearDown() throws Exception {
        closeKmsMockWebServer();
    }

    /**
     * GIVEN kms returns 5xx server errors WHEN vault client retries THEN expected response.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsReturns5xxServerErrors_WHEN_vaultClientRetries_THEN_expectedResponse() throws Exception {
        // given
        mockKms5xxResponsesToTriggerRetry();
        buildAndPersistTestSubsystems();

        // when
        response = mockMvc.perform(get(pathProperties.getV2().getSubsystemManagement().getBasePath() + "/" + Constants.SUBSYSTEMS)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final List<Subsystem> returnedSubsystems = extractFromResponse(response, new TypeReference<List<Subsystem>>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystems.get(0), PROPERTY_INDEX_ZERO, DECRYPTED_PASSWORD_VALUE_ONE);
    }

    /**
     * GIVEN kms returns 5xx server errors WHEN kms client exhausts the retry mechanism THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsReturns5xxServerErrors_WHEN_KmsClientExhaustsTheRetryMechanism_THEN_isInternalServiceError() throws Exception {
        // given
        mockFailedKmsEncryptRequestsForRetry();
        buildAndPersistTestSubsystems();

        // when
        response = mockMvc.perform(get(pathProperties.getV2().getSubsystemManagement().getBasePath() + "/" + Constants.SUBSYSTEMS)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms returns 4xx server error WHEN get subsystems THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsReturns4xxServerError_WHEN_getSubsystems_THEN_isInternalServiceError() throws Exception {
        // given
        mockKmsBadRequestResponse();
        buildAndPersistTestSubsystems();

        // when
        response = mockMvc.perform(get(pathProperties.getV2().getSubsystemManagement().getBasePath() + "/" + Constants.SUBSYSTEMS)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Mock kms 5xx responses to trigger retry.
     */
    private void mockKms5xxResponsesToTriggerRetry() {
        // each decrypt request will return two 5xx status code responses, the third attempt will return ok

        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.SERVICE_UNAVAILABLE, serviceUnavailable));
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.BAD_GATEWAY, badGateway));
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.OK, vaultDecryptResponse));

        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.SERVICE_UNAVAILABLE, serviceUnavailable));
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.BAD_GATEWAY, badGateway));
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.OK, vaultDecryptResponse));
    }

    /**
     * Mock failed kms encrypt requests for retry.
     */
    private void mockFailedKmsEncryptRequestsForRetry() {
        // the initial decrypt request will return variations of 5xx status codes to exhaust the retry mechanism
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.INTERNAL_SERVER_ERROR, internalServerError));
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.BAD_GATEWAY, badGateway));
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.INTERNAL_SERVER_ERROR, internalServerError));
    }

    /**
     * Builds the and persist test subsystems.
     */
    private void buildAndPersistTestSubsystems() {
        final List<Property> propertyListOne =
                PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<Property> propertyListTwo = PropertyFactory.buildDefaultProperties();
        final List<ConnectionProperties> connectionPropertiesOne =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final List<ConnectionProperties> connectionPropertiesTwo =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListTwo));

        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ECM, connectionPropertiesOne);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_ECM, connectionPropertiesTwo);
    }

}
