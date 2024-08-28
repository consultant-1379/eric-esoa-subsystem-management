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

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
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
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.PatchSubsystemIntegrationV2Test.EMPTY_ADAPTER_LINK;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.PatchSubsystemIntegrationV2Test.SUBSYSTEM_VENDOR_NAME_CISCO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.PatchSubsystemIntegrationV2Test.UPDATED_ADAPTER_LINK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PatchSubsystemIntegrationTest.
 */
public class PatchSubsystemIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN existing subsystem WHEN patch subsystem with vendor name THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithVendorName_THEN_isOk() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // persist initial subsystem
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, Constants.DOMAIN_MANAGER);
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setSubsystemTypeId(2L);
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());

        // when
        response = patchRequest(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem with null link WHEN patch subsystem with new link THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystemWithNullLink_WHEN_patchSubsystemWithNewLink_THEN_isOk() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // persist initial subsystem
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        // build subsystem for patch request
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, Constants.DOMAIN_MANAGER);
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setAdapterLink(UPDATED_ADAPTER_LINK);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());

        // when
        response = patchRequest(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseBodyContains(UPDATED_ADAPTER_LINK);

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem with empty link WHEN patch subsystem with new link THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystemWithEmptyLink_WHEN_patchSubsystemWithNewLink_THEN_isOk() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // persist initial subsystem
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        initialSubsystem.setAdapterLink(EMPTY_ADAPTER_LINK);
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, Constants.DOMAIN_MANAGER);
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setAdapterLink(UPDATED_ADAPTER_LINK);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());
        response = patchRequest(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseBodyContains(UPDATED_ADAPTER_LINK);

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem with link WHEN patch subsystem with null link THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemWithLink_WHEN_patchSubsystemWithNullLink_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        initialSubsystem.setAdapterLink(EMPTY_ADAPTER_LINK);
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);
        persistedSubsystem.setAdapterLink("adapterLink");

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, "DomainManager");
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setAdapterLink(EMPTY_ADAPTER_LINK);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());
        response = patchRequest(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode("SSM-B-41");
    }

    /**
     * GIVEN existing subsystem with link WHEN patch subsystem with another link THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemWithLink_WHEN_patchSubsystemWithAnotherLink_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        initialSubsystem.setAdapterLink(EMPTY_ADAPTER_LINK);
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);
        persistedSubsystem.setAdapterLink("adapterLink");

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, "DomainManager");
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setAdapterLink("newAdapterLink");
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());
        response = patchRequest(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode("SSM-B-41");
    }

    /**
     * GIVEN kms will return error WHEN patch subsystem THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_patchSubsystem_THEN_isInternalServerError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, Constants.DOMAIN_MANAGER);
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setSubsystemTypeId(2L);
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());

        // when
        response = patchRequest(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Patch request.
     *
     * @param subsystem the subsystem
     * @param subsystemId the subsystem id
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions patchRequest(final Subsystem subsystem, final Long subsystemId) throws Exception {
        return mockMvc.perform(patch(ExpectedResponse.getServiceUrl() + "/subsystems/" + subsystemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }

}
