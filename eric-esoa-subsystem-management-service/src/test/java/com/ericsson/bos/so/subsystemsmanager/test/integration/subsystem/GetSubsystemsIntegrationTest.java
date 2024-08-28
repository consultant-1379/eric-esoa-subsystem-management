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
import com.ericsson.bos.so.subsystemsmanager.business.pagination.util.SortDirection;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.ConnectionPropControllerTest.SUBSYSTEMS_V1_BASE_PATH;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2.GetSubsystemByIdIntegrationV2Test.INTERNAL_ERROR_CODE_SSM_C_09;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SUBSYSTEMS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class GetSubsystemsIntegrationTest.
 */
public class GetSubsystemsIntegrationTest extends BaseIntegrationTest {

    public static final String ENCRYPTED_PASSWORD_VALUE_ONE = "vault:v1:8gsHPk3c73q3U5uz6xKZ6r/f9B2Qc1IXmbvl1ddiKj0FpNxFw==";

    public static final String DECRYPTED_PASSWORD_VALUE_ONE = "test-password-value";

    public static final String ENCRYPTED_PASSWORD_VALUE_TWO = "vault:v1:9hpHPk3c73q3U5uz6xKZ6r/f9B2Qc1IXmbvl1ddiKj0FpNxFw==";

    public static final String DECRYPTED_PASSWORD_VALUE_TWO = "updated-test-password";

    public static final String TEST_USERNAME = "test-username";

    public static final String TEST_PROPERTY_NAME = "test-property-name";

    public static final String TEST_TENANT_NAME = "test-tenant-name";

    public static final String SUBSYSTEM_TYPE_ECM = "ecm";

    public static final String SUBSYSTEM_TYPE_ENM = "enm";

    public static final String SUBSYSTEM_TYPE_NFVO = "NFVO";

    public static final String SUBSYSTEM_TYPE_DO = "DomainOrchestrator";

    public static final String TEST_UPDATED_USERNAME = "test-updated-username";

    public static final String TEST_UPDATED_PROPERTY_NAME = "test-updated-name";

    public static final String TEST_UPDATED_TENANT = "test-updated-tenant";

    public static final int PROPERTY_INDEX_ZERO = 0;

    public static final int PROPERTY_INDEX_ONE = 1;

    public static final int PROPERTY_INDEX_TWO = 2;

    public static final int PROPERTY_INDEX_THREE = 3;

    public static final int PROPERTY_INDEX_FOUR = 4;

    public static final String TENANT_FOR_TEST = "tenantForTest";

    private static final String FILTERED_SUBSYSTEM_NAME = PRIMARY_SUBSYSTEM;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem WHEN get subsystems THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystems_THEN_isOk() throws Exception {
        // given
        buildAndPersistTestSubsystems();
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        // when
        response = mockMvc.perform(get(SUBSYSTEMS_V1_BASE_PATH + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final List<Subsystem> returnedSubsystems = extractFromResponse(response, new TypeReference<List<Subsystem>>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystems.get(0), PROPERTY_INDEX_ZERO, DECRYPTED_PASSWORD_VALUE_ONE);
    }

    /**
     * GIVEN subsystem WHEN get subsystems with offset THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemsWithOffset_THEN_isOk() throws Exception {
        // given
        buildAndPersistTestSubsystems();

        // when
        response = mockMvc.perform(get(SUBSYSTEMS_V1_BASE_PATH + "/subsystems?offset=100")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final List<Subsystem> returnedSubsystems = extractFromResponse(response, new TypeReference<List<Subsystem>>() {});
        assertEquals(0, returnedSubsystems.size());
    }

    /**
     * GIVEN subsystem WHEN get subsystems with filter param THEN is ok.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemsWithFilterParam_THEN_isOk() throws Exception {
        // given
        buildAndPersistTestSubsystems();
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);

        // build url with filter by subsystem name param
        final String filters = String.format("{\"name\": \"%s\"}", FILTERED_SUBSYSTEM_NAME);
        final String url = buildSubsystemUrlWithFilterParam();

        // when
        response = mockMvc.perform(get(url, filters).contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        final List<Subsystem> returnedSubsystems = extractFromResponse(response, new TypeReference<List<Subsystem>>() {});
        assertEquals(1, returnedSubsystems.size());
        assertEquals(FILTERED_SUBSYSTEM_NAME, returnedSubsystems.get(0).getName());

        // and
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystems.get(0), PROPERTY_INDEX_ZERO, DECRYPTED_PASSWORD_VALUE_ONE);
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
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, "testForTest", TENANT_FOR_TEST)));
        final List<ConnectionProperties> connectionProperties2 = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties2);
        final String apiKey = subsystem.getApiKey().toString();

        // when
        response = getSubsystemById(apiKey, TENANT_FOR_TEST);

        // then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(subsystem.getName()));

        // and
        final Subsystem returnedSubsystem = extractFromResponse(response, new TypeReference<Subsystem>() {});
        verifyConnectionPropertyContainsExpectedValue(returnedSubsystem, PROPERTY_INDEX_ZERO, DECRYPTED_PASSWORD_VALUE_ONE);
    }

    /**
     * GIVEN kms will return error WHEN get all subsystems THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_getAllSubsystems_THEN_isInternalServerError() throws Exception {
        // given
        buildAndPersistTestSubsystems();
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));
        // when
        response = mockMvc.perform(get(SUBSYSTEMS_V1_BASE_PATH + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * GIVEN kms will return error WHEN get subsystem by id THEN is internal server error.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsWillReturnError_WHEN_getSubsystemById_THEN_isInternalServerError() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenThrow(new KmsServiceException(""));
        final List<ConnectionProperties> connectionProperties = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, "testForTest", TENANT_FOR_TEST)));
        final List<ConnectionProperties> connectionProperties2 = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_ENM, connectionProperties2);
        final String subsystemId = String.valueOf(subsystem.getId());

        // when
        response = getSubsystemById(subsystemId, TENANT_FOR_TEST);

        // then
        response.andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // and
        checkResponseContainsInternalErrorCode(INTERNAL_ERROR_CODE_SSM_C_09);
    }

    /**
     * Builds the and persist test subsystems.
     */
    private void buildAndPersistTestSubsystems() {
        final List<Property> propertyListOne = PropertyFactory.buildProperties(
                TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME);
        final List<Property> propertyListTwo = PropertyFactory.buildDefaultProperties();
        final List<ConnectionProperties> connectionPropertiesOne =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListOne));
        final List<ConnectionProperties> connectionPropertiesTwo =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(propertyListTwo));
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_ECM, connectionPropertiesOne);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_ECM, connectionPropertiesTwo);
    }

    /**
     * Builds the subsystem url with filter param.
     *
     * @return the string
     */
    private static String buildSubsystemUrlWithFilterParam() {
        return UriComponentsBuilder.fromUriString(SUBSYSTEMS_V1_BASE_PATH)
                .pathSegment(SUBSYSTEMS)
                .queryParam("offset", 0)
                .queryParam("limit", 10)
                .queryParam("sortAttr", "name")
                .queryParam("sortDir", SortDirection.ASC)
                .queryParam("filters", "{filters}")
                .build()
                .toString();
    }

    /**
     * Gets the subsystem by id.
     *
     * @param subsystemId the subsystem id
     * @param tenantName the tenant name
     * @return the subsystem by id
     * @throws Exception the exception
     */
    private ResultActions getSubsystemById(String subsystemId, String tenantName) throws Exception {
        final String url =  UriComponentsBuilder.fromUriString(SUBSYSTEMS_V1_BASE_PATH)
                .pathSegment(SUBSYSTEMS, subsystemId)
                .queryParam("tenantName", TENANT_FOR_TEST)
                .build()
                .toString();
        return mockMvc.perform(get(url));
    }

}
