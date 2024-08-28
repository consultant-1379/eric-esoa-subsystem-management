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
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class DeleteCreateSubsystemUserTest.
 */
public class DeleteCreateSubsystemUserTest extends BaseIntegrationTest {

    @Autowired
    private SubsystemRepository subsystemRepository;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem users not saved WHEN delete subsystem user THEN error response return.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemUsersNotSaved_WHEN_deleteSubsystemUser_THEN_errorResponseReturn() throws Exception {
        // given
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory
                        .buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, "DomainManager", connectionProperties);
        // when
        response = mockMvc.perform(delete(ExpectedResponse.getServiceUrl() + "/subsystems/"
                + subsystemSaved.getId() + "/connection-properties/"
                + subsystemSaved.getConnectionProperties().get(0).getId() + "/subsystem-users/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-J-18");
        checkResponseContainsErrorData( "1");
    }

    /**
     * GIVEN subsystem user WHEN delete subsystem user THEN positive response return.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemUser_WHEN_deleteSubsystemUser_THEN_positiveResponseReturn() throws Exception {
        // given
        final ConnectionProperties connectionProperties =
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(Collections.singletonList(new SubsystemUser())); // simulate connection property that is in use

        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM,
                "DomainManager", Collections.singletonList(connectionProperties));

        // when
        response = mockMvc.perform(delete(ExpectedResponse.getServiceUrl() + "/subsystems/"
                + subsystemSaved.getId() + "/connection-properties/"
                + subsystemSaved.getConnectionProperties().get(0).getId() + "/subsystem-users/"
                + subsystemSaved.getConnectionProperties().get(0).getSubsystemUsers().get(0).getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isNoContent());
    }

    /**
     * GIVEN subsystem WHEN create subsystem user THEN positive response return.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystem_WHEN_createSubsystemUser_THEN_positiveResponseReturn() throws Exception {
        // given
        final ConnectionProperties connectionProperties =
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(Collections.singletonList(new SubsystemUser())); // simulate connection property that is in use

        final Subsystem subsystemSaved = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM,
                "DomainManager", Collections.singletonList(connectionProperties));

        // when
        response = mockMvc.perform(post(ExpectedResponse.getServiceUrl() + "/subsystems/"
                + subsystemSaved.getId() + "/connection-properties/"
                + subsystemSaved.getConnectionProperties().get(0).getId() + "/subsystem-users")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isCreated());
    }
}
