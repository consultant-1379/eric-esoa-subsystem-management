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
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType.DOMAIN_ORCHESTRATOR;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.ACCESS_KEY_ID;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.AUTH_BODY;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.AUTH_KEY;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SECRET_ACCESS_KEY;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_API_KEY;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_AWS;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_BASIC_AUTH;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_BASIC_AUTH_TOKEN;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_BEARER;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_CAI3G;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_COOKIE;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_AUTH_TYPE_NO_AUTH;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.TOKEN_REF;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.VENDOR_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.AUTH_HEADERS_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.AUTH_URL_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.PW_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.USERNAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.DEFAULT_ADAPTER_LINK;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.SubsystemTypeValidationIntegrationTest.ADAPTER_LINK_1;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class ConnectionPropertiesAuthValidationTest.
 */
public class ConnectionPropertiesAuthValidationTest extends BaseIntegrationTest {

    private static final String AUTH_TYPE = "auth_type";

    private static final String INTERNAL_ERROR_CODE_SSM_B_45 = "SSM-B-45";

    @SpyBean
    private AdaptersLinksService adapterLinksService;

    @MockBean
    private KmsServiceImpl kmsServiceImpl;

    /**
     * Setup tests.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void setup() throws IOException {

        final List<String> adapterLinks = Arrays.asList(ADAPTER_LINK_1, DEFAULT_ADAPTER_LINK);
        doReturn(adapterLinks).when(adapterLinksService).fetchAdapterLinksByType(PredefinedSubsystemType.DOMAIN_ORCHESTRATOR.getType());
    }

    /**
     * WHEN create orchestrator subsystem with cookie THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithCookie_THEN_isSuccessful() throws Exception {
        // given

        // when
        final List<Property> properties = PropertyFactory.buildProperties(
                ImmutableMap.of(PW_PROPERTY, DEFAULT_PASSWORD, USERNAME_PROPERTY, DEFAULT_USERNAME,
                AUTH_URL_PROPERTY, "/auth/v1",
                AUTH_HEADERS_PROPERTY, Constants.AUTH_HEADERS_VAL,
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_COOKIE) );
        final Property property = new Property();
        property.setKey(AUTH_KEY);
        property.setValue("jsessionid");
        properties.add(property);
        final Property property1 = new Property();
        property1.setKey(AUTH_BODY);
        property1.setValue("{}");
        properties.add(property1);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_COOKIE + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with bearer THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithBearer_THEN_isSuccessful() throws Exception {
        // given

        // when
        final ImmutableMap<String,String> propMap = ImmutableMap.<String, String>builder()
                .put(USERNAME_PROPERTY, DEFAULT_USERNAME)
                .put(PW_PROPERTY, DEFAULT_PASSWORD)
                .put(AUTH_URL_PROPERTY, "/token")
                .put(AUTH_BODY, "{\"client_id\":\"Ericsson\",\"client_secret\":\"P4sS.w0rd\",\"grant_type\":\"complete\"}")
                .put(AUTH_HEADERS_PROPERTY, Constants.AUTH_HEADERS_VAL)
                .put(AUTH_KEY, "access_token")
                .put(TOKEN_REF, ".access_token")
                .put(AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_BEARER)
                .build();
        final List<Property> properties = PropertyFactory.buildProperties(propMap);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_BEARER + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with basic THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithBasic_THEN_isSuccessful() throws Exception {
        // given

        // when
        final ImmutableMap<String,String> propMap = ImmutableMap.<String, String>builder()
                .put(USERNAME_PROPERTY, DEFAULT_USERNAME)
                .put(PW_PROPERTY, DEFAULT_PASSWORD)
                .put(AUTH_KEY, "anyKey")
                .put(AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_BASIC_AUTH)
                .build();
        final List<Property> properties = PropertyFactory.buildProperties(propMap);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_BASIC_AUTH + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with basic token THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithBasicToken_THEN_isSuccessful() throws Exception {
        // given

        // when
        final ImmutableMap<String,String> propMap = ImmutableMap.<String, String>builder()
                .put(USERNAME_PROPERTY, DEFAULT_USERNAME)
                .put(PW_PROPERTY, DEFAULT_PASSWORD)
                .put(AUTH_URL_PROPERTY, "/token")
                .put(AUTH_HEADERS_PROPERTY, Constants.AUTH_HEADERS_VAL)
                .put(AUTH_KEY, "access_token")
                .put(TOKEN_REF, ".status.credentials")
                .put(AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_BASIC_AUTH_TOKEN)
                .build();
        final List<Property> properties = PropertyFactory.buildProperties(propMap);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_BASIC_AUTH_TOKEN + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with apy key THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithApyKey_THEN_isSuccessful() throws Exception {
        // given

        // when
        response = postRequest(createSubsystem(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                TOKEN_REF, "12345",
                AUTH_KEY, "apikey",
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_API_KEY));

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_API_KEY + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with aws THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithAws_THEN_isSuccessful() throws Exception {
        // given
        doReturn(DEFAULT_PASSWORD).when(kmsServiceImpl).encryptProperty(DEFAULT_PASSWORD);
        // when
        response = postRequest(createSubsystem(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                ACCESS_KEY_ID, "AKIAUAU6USY22Z4HMPPT",
                SECRET_ACCESS_KEY, "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",
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_AWS));

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_AWS + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with no auth THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithNoAuth_THEN_isSuccessful() throws Exception {
        // given

        // when
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(PW_PROPERTY, DEFAULT_PASSWORD,
                USERNAME_PROPERTY, DEFAULT_USERNAME,
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_NO_AUTH) );
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_NO_AUTH + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with not managed THEN is successful.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createOrchestratorSubsystemWithNotManaged_THEN_isSuccessful() throws Exception {
        // given

        // when
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(PW_PROPERTY, DEFAULT_PASSWORD,
                USERNAME_PROPERTY, DEFAULT_USERNAME,
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_CAI3G) );
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));;

        // and
        final String responseAsString = response.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseAsString)
                .contains(DOMAIN_ORCHESTRATOR.getType())
                .contains(Constants.$_BACKSLASH + AUTH_TYPE + Constants.$_FBSLASH + SUBSYSTEM_AUTH_TYPE_CAI3G + Constants.$_BACKSLASH);
    }

    /**
     * WHEN create orchestrator subsystem with wrong cookie THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWithWrongCookie_THEN_isBadRequest() throws Exception {
        // when
        response = postRequest(createSubsystem(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                AUTH_URL_PROPERTY, "/auth/v1",
                SECRET_ACCESS_KEY, "VX9wbXcMN+9eXM8h7Sfd7S+UJx8qWEq2ZncRNfnX",
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_COOKIE));

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    //wrong cases


    /**
     * WHEN create orchestrator subsystem with wrong bearer THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWithWrongBearer_THEN_isBadRequest() throws Exception {
        // when
        final ImmutableMap<String,String> propMap = ImmutableMap.<String, String>builder()
                .put(USERNAME_PROPERTY, DEFAULT_USERNAME)
                .put(PW_PROPERTY, DEFAULT_PASSWORD)
                .put(AUTH_URL_PROPERTY, "/token")
                .put(AUTH_HEADERS_PROPERTY, Constants.AUTH_HEADERS_VAL)
                .put(TOKEN_REF, ".access_token")
                .put(AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_BEARER)
                .build();
        final List<Property> properties = PropertyFactory.buildProperties(propMap);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    /**
     * WHEN create orchestrator subsystem wrong with basic THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWrongWithBasic_THEN_isBadRequest() throws Exception {
        // when
        final ImmutableMap<String,String> propMap = ImmutableMap.<String, String>builder()
                .put(USERNAME_PROPERTY, DEFAULT_USERNAME)
                .put(PW_PROPERTY, DEFAULT_PASSWORD)
                .put(AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_BASIC_AUTH)
                .build();
        final List<Property> properties = PropertyFactory.buildProperties(propMap);
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    /**
     * WHEN create orchestrator subsystem with wrong basic token THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWithWrongBasicToken_THEN_isBadRequest() throws Exception {
        // when insert THENot expected AUTH_BODY
        response = postRequest(createSubsystem(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                AUTH_BODY, ".status.credentials",
                AUTH_KEY, "access_token",
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_BASIC_AUTH_TOKEN));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    /**
     * WHEN create orchestrator subsystem with wrong apy key THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWithWrongApyKey_THEN_isBadRequest() throws Exception {
        // when
        response = postRequest(createSubsystem(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                AUTH_HEADERS_PROPERTY, Constants.AUTH_HEADERS_VAL,
                AUTH_KEY, "apikey",
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_API_KEY));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    /**
     * WHEN create orchestrator subsystem with wrong aws THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWithWrongAws_THEN_isBadRequest() throws Exception {
        // when
        response = postRequest(createSubsystem(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                ACCESS_KEY_ID, "AKIAUAU6USY22Z4HMPPT",
                AUTH_KEY, "apikey",
                AUTH_TYPE, SUBSYSTEM_AUTH_TYPE_AWS));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    /**
     * WHEN create orchestrator subsystem with no type THEN is bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createOrchestratorSubsystemWithNoType_THEN_isBadRequest() throws Exception {
        // when
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(PW_PROPERTY, DEFAULT_PASSWORD,
                USERNAME_PROPERTY, DEFAULT_USERNAME) );
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_B_45);
    }

    /**
     * Post request.
     *
     * @param subsystem the subsystem
     * @return the result actions
     * @throws Exception the exception
     */
    private ResultActions postRequest(final Subsystem subsystem) throws Exception {
        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }

    /**
     * Creates the subsystem.
     *
     * @param p1 the p1
     * @param v1 the v1
     * @param p2 the p2
     * @param v2 the v2
     * @param p3 the p3
     * @param v3 the v3
     * @param p4 the p4
     * @param v4 the v4
     * @param p5 the p5
     * @param v5 the v5
     * @return the subsystem
     */
    private Subsystem createSubsystem(final String p1, final String v1, final String p2, final String v2,
            final String p3, final String v3, final String p4, final String v4, final String p5, final String v5) {
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(p1, v1,
                p2, v2, p3, v3, p4, v4, p5, v5) );

        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DOMAIN_ORCHESTRATOR.getType(), connectionProperties);
        subsystem.setVendor(VENDOR_NAME);
        return subsystem;
    }

}
