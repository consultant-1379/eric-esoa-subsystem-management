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
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_ECM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_ENM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class GetSubsystemByIdIntegrationV2Test.
 */
public class GetSubsystemByIdIntegrationV2Test extends BaseIntegrationTest {

    public static final String INTERNAL_ERROR_CODE_SSM_C_09 = "SSM-I-09";

    private static final String INTERNAL_ERROR_CODE_SSM_C_43 = "SSM-C-43";

    private String subsystemId;

    private String apiKey;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN invalid subsystem WHEN get subsystem by id THEN is not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_InvalidSubsystem_WHEN_getSubsystemById_THEN_isNotFound() throws Exception {
        // given
        final String invalidSubsystemId = "5";
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        // when
        response = getSubsystemsV2(invalidSubsystemId);

        // then
        response.andExpect(status().isNotFound());
    }

    /**
     * GIVEN invalid apikey WHEN get subsystem by id THEN is not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_InvalidApikey_WHEN_getSubsystemById_THEN_isNotFound() throws Exception {
        // given
        final String invalidApiKey = UUID.randomUUID().toString();
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        // when
        response = getSubsystemsV2(invalidApiKey);

        // then
        response.andExpect(status().isNotFound());
    }

    /**
     * GIVEN kms service returns error WHEN get subsystem by id THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_getSubsystemById_THEN_isInternalServiceError() throws Exception {
        // given
        buildTestSubsystems();
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));
        // when
        response = getSubsystemsV2(subsystemId);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN subsystem WHEN get subsystem by api key THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemByApiKey_THEN_isOk() throws Exception {
        // given
        buildTestSubsystems();
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // when
        response = getSubsystemsV2(apiKey);

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(PRIMARY_SUBSYSTEM));

        // and
        final Subsystem subsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(subsystem, PROPERTY_INDEX_ZERO, DECRYPTED_PASSWORD_VALUE_ONE);
    }

    /**
     * GIVEN kms service returns error WHEN get subsystem by api key THEN is internal service error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_getSubsystemByApiKey_THEN_isInternalServiceError() throws Exception {
        // given
        buildTestSubsystems();
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));
        // when
        response = getSubsystemsV2(apiKey);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }


    /**
     * Gets the subsystems V2.
     *
     * @param id the id
     * @return the subsystems V2
     * @throws Exception the exception
     */
    private ResultActions getSubsystemsV2(String id) throws Exception {
        return mockMvc.perform(get(
                pathProperties.getV2().getSubsystemManagement().getBasePath() + "/" +
                        Constants.SUBSYSTEMS + "/" + id + "?tenantName=" + TEST_TENANT_NAME));
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
