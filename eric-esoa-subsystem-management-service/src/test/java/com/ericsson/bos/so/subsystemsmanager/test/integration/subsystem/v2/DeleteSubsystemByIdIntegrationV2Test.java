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
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * The Class DeleteSubsystemByIdIntegrationV2Test.
 */
public class DeleteSubsystemByIdIntegrationV2Test extends BaseIntegrationTest {

    @Autowired
    private SubsystemRepository subsystemRepository;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem in use WHEN delete subsystem THEN error response.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void GIVEN_subsystemInUse_WHEN_deleteSubsystem_THEN_errorResponse() throws Exception {
        // given
        final ConnectionProperties connectionProperties = ConnectionPropertiesFactory.buildConnectionProperties(
                PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(Collections.singletonList(new SubsystemUser())); // simulate connection property that is in use
        final Subsystem subsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, Collections.singletonList(connectionProperties));

        // when
        response = mockMvc.perform(delete(pathProperties.getV2().getSubsystemManagement().getBasePath() +Constants.$_SLASH+
                Constants.SUBSYSTEMS +Constants.$_SLASH+ subsystem.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-H-12");
        checkResponseContainsErrorData(PRIMARY_SUBSYSTEM);

        assertThat(subsystemRepository.findById(subsystem.getId())).isPresent();
    }

    /**
     * GIVEN subsystem not exist WHEN delete subsystem THEN error response.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void GIVEN_subsystemNotExist_WHEN_deleteSubsystem_THEN_errorResponse() throws Exception {
        // given
        final String subsystemId = "1";

        // when
        response = mockMvc
                .perform(delete(pathProperties.getV2().getSubsystemManagement().getBasePath() +Constants.$_SLASH+
                        Constants.SUBSYSTEMS +Constants.$_SLASH+ subsystemId)
                                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-J-13");
    }
}
