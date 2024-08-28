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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.GetSubsystemByIdIntegrationV2Test.INTERNAL_ERROR_CODE_SSM_C_09;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostSubsystemIntegrationTest.
 */
public class PostSubsystemIntegrationTest extends BaseIntegrationTest {

    /** The Constant INTERNAL_ERROR_CODE_SSM_B_25. */
    private static final String INTERNAL_ERROR_CODE_SSM_B_25 = "SSM-B-25";

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * WHEN post subsystem THEN is created.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_postSubsystem_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem WHEN create subsystem with the same name THEN is conflict.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_createSubsystemWithTheSameName_THEN_isConflict() throws Exception {
        // given
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode("SSM-K-07");
        checkResponseContainsErrorData(PRIMARY_SUBSYSTEM);
    }

    /**
     * WHEN create subsystem with no name THEN is bad request.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoName_THEN_isBadRequest() throws Exception {
        // when
        final Subsystem subsystem = subsystemFactory.buildDefaultSubsystem();
        subsystem.setName(null);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_25);
    }

    /**
     * WHEN create subsystem with no url THEN is bad request.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoUrl_THEN_isBadRequest() throws Exception {
        // when
        final Subsystem subsystem = subsystemFactory.buildDefaultSubsystem();
        subsystem.setUrl(null);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_25);
    }

    /**
     * WHEN create subsystem with no vendor THEN is bad request.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoVendor_THEN_isBadRequest() throws Exception {

        // when
        final Subsystem subsystem = subsystemFactory.buildDefaultSubsystem();
        subsystem.setVendor(null);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_25);
    }

    /**
     * GIVEN kms will return error WHEN post subsystem THEN is internal server error.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnErrorWHEN_postSubsystem_THEN_isInternalServerError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Post request.
     *
     * @param subsystem
     *            the subsystem
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    private ResultActions postRequest(final Subsystem subsystem) throws Exception {
        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }
}
