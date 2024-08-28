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
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemSubtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_FOUR;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.PROPERTY_INDEX_ZERO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostSubsystemIntegrationTest.
 */
public class PostSubsystemIntegrationTest extends BaseIntegrationTest {

    public static final String INTERNAL_ERROR_CODE_SSM_C_09 = "SSM-I-09";
    private static final String SSM_B_35 = "SSM-B-35";

    private static final String SSM_B_25 = "SSM-B-25";

    private static final String SSM_B_37 = "SSM-B-37";

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * WHEN create subsystem THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createSubsystem_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }

    /**
     * WHEN create subsystem by subsystem type name THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createSubsystemBySubsystemTypeName_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        final SubsystemType subsystemType = subsystem.getSubsystemType().toBuilder().id(null).build();
        final Subsystem updatedSubsystem = subsystem.toBuilder().subsystemType(subsystemType).build();

        // when
        response = postRequest(updatedSubsystem);

        // then
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DEFAULT_PASSWORD);
    }



    /**
     * GIVEN existing subsystem WHEN create subsystem with the same name THEN is conflict.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystem_WHEN_createSubsystemWithTheSameName_THEN_isConflict() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
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
     * @throws Exception the exception
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
        checkResponseContainsInternalErrorCode(SSM_B_25);
    }

    /**
     * WHEN create subsystem with no url THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoUrl_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem subsystem = subsystemFactory.buildDefaultSubsystem();
        subsystem.setUrl(null);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_25);
    }

    /**
     * WHEN create subsystem with no vendor THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoVendor_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem subsystem = subsystemFactory.buildDefaultSubsystem();
        subsystem.setVendor(null);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_25);
    }

    /**
     * WHEN create subsystem without subsystem type THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithoutSubsystemType_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem subsystem = subsystemFactory.buildDefaultSubsystem();
        subsystem.setSubsystemType(null);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_25);
    }

    /**
     * WHEN create subsystem without connection properties THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithoutConnectionProperties_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem subsystem = subsystemFactory.buildSubsystem(
                PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType(), Collections.<ConnectionProperties>emptyList());

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_35);
    }

    /**
     * WHEN create subsystem with multiple connection properties THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithMultipleConnectionProperties_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType(),
                ConnectionPropertiesFactory.buildConnectionProperties(2));

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_37);
    }

    /**
     * WHEN create subsystem for auth systems THEN is created.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createSubsystemForAuthSystems_THEN_isCreated() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(Constants.$_AUTH_URL, Constants.TEST,
                                Constants.OUTH_2, Constants.$_123,Constants.$_1231, Constants.$_2111)));
        final Subsystem subsystem =
                subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.AUTHENTICATION_SYSTEMS.getType(),
                        PredefinedSubsystemSubtype.OAUTH2_CLIENT_CREDENTIALS.getName(), connectionProperties);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_FOUR, Constants.$_1231);
    }

    /**
     * WHEN create subsystem for auth systems without subtype THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemForAuthSystemsWithoutSubtype_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(
                PropertyFactory.buildProperties(Constants.$_AUTH_URL, Constants.TEST,
                        Constants.OUTH_2, Constants.$_123,Constants.$_1231, Constants.$_2111)));
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM,
                PredefinedSubsystemType.AUTHENTICATION_SYSTEMS.getType(), connectionProperties);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_25);
    }

    /**
     * WHEN create subsystem for auth systems with unknown subtype THEN is not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemForAuthSystemsWithUnknownSubtype_THEN_isNotFound() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(ConnectionPropertiesFactory
                .buildConnectionProperties(PropertyFactory.buildProperties(Constants.$_AUTH_URL, Constants.TEST,
                        Constants.OUTH_2, Constants.$_123,Constants.$_1231, Constants.$_2111)));
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM,
                PredefinedSubsystemType.AUTHENTICATION_SYSTEMS.getType(), "Basic", connectionProperties);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode("SSM-J-47");
    }

    /**
     * WHEN create subsystem for domain manager with sub type THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemForDomainManagerWithSubType_THEN_isBadRequest() throws Exception {
        // given
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        subsystem.getSubsystemType().setSubtype(new Subtype((long)1, Constants.OUTH_2,"oatuth"));

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode("SSM-B-53");
    }

    /**
     * WHEN create subsystem for auth systems without mandatory connection properties THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemForAuthSystemsWithoutMandatoryConnectionProperties_THEN_isBadRequest() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                    PropertyFactory.buildProperties(Collections.singletonMap("auth_url", "/v1"))));
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM,
                PredefinedSubsystemType.AUTHENTICATION_SYSTEMS.getType(),
                PredefinedSubsystemSubtype.OAUTH2_CLIENT_CREDENTIALS.getName(), connectionProperties);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode("SSM-B-45");
        checkResponseContainsErrorData("auth_type");
    }

    /**
     * WHEN create subsystem for auth systems with multiple connection properties THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemForAuthSystemsWithMultipleConnectionProperties_THEN_isBadRequest() throws Exception {
        // when
        final ConnectionProperties connectionProperty = ConnectionPropertiesFactory
                .buildConnectionProperties(PropertyFactory.buildProperties(Constants.$_AUTH_URL,Constants.TEST,
                        Constants.OUTH_2, Constants.$_123, Constants.$_1231, Constants.$_2111));

        final List<ConnectionProperties> connectionProperties = new ArrayList<>();
        connectionProperties.add(connectionProperty);
        connectionProperties.add(connectionProperty);
        connectionProperties.add(connectionProperty);

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.AUTHENTICATION_SYSTEMS.getType(),
                PredefinedSubsystemSubtype.OAUTH2_CLIENT_CREDENTIALS.getName(), connectionProperties);

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(SSM_B_37);
    }

    /**
     * GIVEN kms service returns error WHEN create subsystem THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsServiceReturnsError_WHEN_createSubsystem_THEN_isInternalServerError() throws Exception {
        // given
        when(kmsService.encryptProperty(any())).thenThrow(new KmsServiceException(""));
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
        response = postRequest(subsystem);

        // then
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Post request.
     *
     * @param subsystem the subsystem
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions postRequest(final Subsystem subsystem) throws Exception {
        return mockMvc.perform(post(pathProperties.getV2().getSubsystemManagement().getBasePath() + "/" + Constants.SUBSYSTEMS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }

}
