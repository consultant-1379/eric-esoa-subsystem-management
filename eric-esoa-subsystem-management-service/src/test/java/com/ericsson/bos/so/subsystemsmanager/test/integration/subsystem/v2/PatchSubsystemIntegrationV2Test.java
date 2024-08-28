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
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.USERNAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.DEFAULT_SUBSYSTEM_TYPE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_THREE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_TWO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_TENANT;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_UPDATED_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PatchSubsystemIntegrationV2Test.
 */
public class PatchSubsystemIntegrationV2Test extends BaseIntegrationTest {

    public static final String SUBSYSTEM_VENDOR_NAME_CISCO = "cisco";

    public static final String UPDATED_ADAPTER_LINK = "https://www.test-updated-vendor-link.com";

    public static final String EMPTY_ADAPTER_LINK = "";

    public static final String TEST_UPDATED_PASSWORD = "updated-test-password";

    public static final String INTERNAL_ERROR_CODE_SSM_C_09 = "SSM-I-09";

    private static final String SSM_B_52 = "SSM-B-52";

    private static final String SSM_A_27 = "SSM-A-27";

    private static final String SSM_B_25 = "SSM-B-25";

    private static final String SSM_K_07 = "SSM-K-07";

    private static final String SSM_B_41 = "SSM-B-41";

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem WHEN patch subsystems with updated connection property password THEN is modified.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_patchSubsystemsWithUpdatedConnectionPropertyPassword_THEN_isModified() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // persist initial subsystem
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        // create subsystem for patch request
        final Subsystem updatedSubsystem = subsystemFactory.buildDefaultSubsystem();
        final List<Property> updatedPropertyList =
                PropertyFactory.buildProperties(TEST_UPDATED_USERNAME, TEST_UPDATED_PASSWORD, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT);
        final List<ConnectionProperties> updatedConnectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(updatedPropertyList));
        updatedSubsystem.setConnectionProperties(updatedConnectionProperties);

        // when
        response = patchSubsystemV2(updatedSubsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, TEST_UPDATED_PASSWORD);
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ONE, TEST_UPDATED_USERNAME);
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_TWO, TEST_UPDATED_PROPERTY_NAME);
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_THREE, TEST_UPDATED_TENANT);
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem with vendor name THEN is modified.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithVendorName_THEN_isModified() throws Exception {
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
        response = patchSubsystemV2(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem with null link WHEN patch subsystem with new link THEN is modified.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystemWithNullLink_WHEN_patchSubsystemWithNewLink_THEN_isModified() throws Exception {
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
        response = patchSubsystemV2(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseBodyContains(UPDATED_ADAPTER_LINK);

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem with empty link WHEN patch subsystem with new link THEN is modified.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystemWithEmptyLink_WHEN_patchSubsystemWithNewLink_THEN_isModified() throws Exception {
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
        response = patchSubsystemV2(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseBodyContains(UPDATED_ADAPTER_LINK);

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem with deleted connection property THEN is modified.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithDeletedConnectionProperty_THEN_isModified() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        // persist initial subsystem
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        // create subsystem for patch request
        final Subsystem updatedSubsystem = subsystemFactory.buildDefaultSubsystem();
        final List<Property> updatedPropertyList =
                PropertyFactory.buildProperties(TEST_UPDATED_USERNAME, TEST_UPDATED_PASSWORD, TEST_UPDATED_PROPERTY_NAME, TEST_UPDATED_TENANT);
        updatedPropertyList.remove(PROPERTY_INDEX_THREE); // delete the tenant-name property at index 3
        final List<ConnectionProperties> updatedConnectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(updatedPropertyList));
        updatedSubsystem.setConnectionProperties(updatedConnectionProperties);

        //when
        response = patchSubsystemV2(updatedSubsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.connectionProperties.[0].name").value(TEST_UPDATED_PROPERTY_NAME))
                .andExpect(jsonPath("$.connectionProperties.[0].username").value(TEST_UPDATED_USERNAME))
                .andExpect(jsonPath("$.connectionProperties.[0].password").value(TEST_UPDATED_PASSWORD))
                .andExpect(jsonPath("$.connectionProperties.[0].tenant").doesNotExist());
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem with apikey THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithApikey_THEN_isBadRequest() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, Constants.DOMAIN_MANAGER);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setApiKey(UUID.randomUUID());
        subsystem.setSubsystemTypeId(1L);
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());

        // when
        response = patchSubsystemV2(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_52);
    }

    /**
     * GIVEN existing subsystem with link WHEN patch subsystem with null link THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemWithLink_WHEN_patchSubsystemWithNullLink_THEN_isBadRequest() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        subsystemSaved.setAdapterLink("adapterLink");

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setVendor("cisco");
        subsystem.setAdapterLink("");
        subsystem.setId(subsystemSaved.getId());
        subsystem.setConnectionProperties(subsystemSaved.getConnectionProperties());
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_41);
    }

    /**
     * GIVEN existing subsystem with link WHEN patch subsystem with another link THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemWithLink_WHEN_patchSubsystemWithAnotherLink_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        subsystemSaved.setAdapterLink("adapterLink");

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setVendor("cisco");
        subsystem.setAdapterLink("newAdapterLink");
        subsystem.setId(subsystemSaved.getId());
        subsystem.setConnectionProperties(subsystemSaved.getConnectionProperties());
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_41);
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem with same name THEN is conflict.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithSameName_THEN_isConflict() throws Exception {
        // given
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(SubsystemFactory.SECONDARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setId(subsystemSaved.getId());
        subsystem.setSubsystemTypeId(1L);
        subsystem.setConnectionProperties(subsystemSaved.getConnectionProperties());
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_K_07);
        checkResponseContainsErrorData(PRIMARY_SUBSYSTEM);
    }

    /**
     * GIVEN existing subsystem with vendor name WHEN patch subsystem with empty vendor THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemWithVendorName_WHEN_patchSubsystemWithEmptyVendor_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setVendor("");
        subsystem.setId(subsystemSaved.getId());
        subsystem.setSubsystemTypeId(1L);
        subsystem.setConnectionProperties(subsystemSaved.getConnectionProperties());
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_25);
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem without required connection properties THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithoutRequiredConnectionProperties_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setId(subsystemSaved.getId());
        subsystem.setSubsystemTypeId(1L);
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(USERNAME_PROPERTY, DEFAULT_USERNAME));
        final ConnectionProperties connectionProperty = ConnectionPropertiesFactory.buildConnectionProperties(subsystem, properties);
        connectionProperty.setId(subsystemSaved.getConnectionProperties().get(0).getId());
        subsystem.setConnectionProperties(Collections.singletonList(connectionProperty));
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_A_27);
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem with subsystem type THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithSubsystemType_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setId(subsystemSaved.getId());
        subsystem.setSubsystemTypeId(1L);
        subsystem.setConnectionProperties(subsystemSaved.getConnectionProperties());
        final SubsystemType subsystemType =new SubsystemType();
        subsystemType.setId((long)3);
        subsystemType.setType("DomainManager");
        subsystem.setSubsystemType(subsystemType);
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_52);
    }

    /**
     * GIVEN existing subsystem WHEN patch subsystem with sub type THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_patchSubsystemWithSubType_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE);
        subsystem.setId(subsystemSaved.getId());
        subsystem.setSubsystemTypeId(1L);
        subsystem.setConnectionProperties(subsystemSaved.getConnectionProperties());
        final SubsystemType subsystemType =new SubsystemType();
        subsystemType.setId((long)3);
        subsystemType.setType("DomainManager");
        subsystemType.setSubtype(new Subtype((long)1, "Oauth2ClientCredentials", "Oauth2 client credentials"));
        subsystem.setSubsystemType(subsystemType);
        response = patchSubsystemV2(subsystem, subsystemSaved.getId());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_52);
    }

    /**
     * GIVEN kms service returns error WHEN patch subsystem THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_patchSubsystem_THEN_isInternalServerError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));
        // persist initial subsystem
        final Subsystem initialSubsystem = subsystemFactory.buildDefaultSubsystem();
        initialSubsystem.setAdapterLink(EMPTY_ADAPTER_LINK);
        final Subsystem persistedSubsystem = subsystemFactory.persistSubsystem(initialSubsystem);

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM,
                com.ericsson.bos.so.subsystemsmanager.business.util.Constants.DOMAIN_MANAGER);
        subsystem.setVendor(SUBSYSTEM_VENDOR_NAME_CISCO);
        subsystem.setAdapterLink(UPDATED_ADAPTER_LINK);
        subsystem.setId(persistedSubsystem.getId());
        subsystem.setConnectionProperties(persistedSubsystem.getConnectionProperties());
        response = patchSubsystemV2(subsystem, persistedSubsystem.getId());

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Patch subsystem V2.
     *
     * @param subsystem the subsystem
     * @param subsystemId the subsystem id
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions patchSubsystemV2(final Subsystem subsystem, final Long subsystemId) throws Exception {
        return mockMvc.perform(patch(pathProperties.getV2().getSubsystemManagement().getBasePath() +
                "/" + Constants.SUBSYSTEMS + "/" + subsystemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }
}
