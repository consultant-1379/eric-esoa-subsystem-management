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
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.PW_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.USERNAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.DEFAULT_SUBSYSTEM_TYPE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * The Class ConnectionPropertiesValidationTest.
 */
public class ConnectionPropertiesValidationTest extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * WHEN create subsystem with no properties THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoProperties_THEN_badRequestResponse() throws Exception {
        // when
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(Collections.emptyList()));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-A-27");
    }

    /**
     * WHEN create subsystem with empty property THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithEmptyProperty_THEN_badRequestResponse() throws Exception {
        // when
        final String emptyPropertyName = "empty-property";
        final List<Property> properties = PropertyFactory.buildDefaultProperties();
        properties.add(PropertyFactory.buildProperty(emptyPropertyName, ""));
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-B-33");
    }

    /**
     * WHEN create subsystem with duplicate property THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithDuplicateProperty_THEN_badRequestResponse() throws Exception {
        // when
        final String propertyName = "tenant";
        final List<Property> properties = PropertyFactory.buildDefaultProperties();
        properties.add(PropertyFactory.buildProperty(propertyName, "extra-tenant"));
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-B-32");
    }

    /**
     * WHEN create subsystem with no username property THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoUsernameProperty_THEN_badRequestResponse() throws Exception {
        // when
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(PW_PROPERTY, DEFAULT_PASSWORD));
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-A-27");
    }

    /**
     * WHEN create subsystem with no password property THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createSubsystemWithNoPasswordProperty_THEN_badRequestResponse() throws Exception {
        // when
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(USERNAME_PROPERTY, DEFAULT_USERNAME));
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, DEFAULT_SUBSYSTEM_TYPE, connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-A-27");
    }

    private ResultActions postRequest(final Subsystem subsystem) throws Exception {
        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }

}
