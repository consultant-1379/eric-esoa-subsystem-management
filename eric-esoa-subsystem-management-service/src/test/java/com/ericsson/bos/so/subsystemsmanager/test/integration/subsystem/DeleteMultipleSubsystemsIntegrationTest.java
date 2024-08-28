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
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.EXTRA_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;


/**
 * The Class DeleteMultipleSubsystemsIntegrationTest.
 */
public class DeleteMultipleSubsystemsIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    private Subsystem primarySubsystem;
    private Subsystem secondarySubsystem;
    private Subsystem subsystemWithUserReferences;

    /**
     * Persist subsystems.
     */
    @Before
    public void persistSubsystems() {
        primarySubsystem = subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, "ecm");
        secondarySubsystem = subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, "enm");

        final ConnectionProperties connectionProperties =
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(Collections.singletonList(new SubsystemUser())); // simulate connection property that is in use

        subsystemWithUserReferences = subsystemFactory.persistSubsystem(EXTRA_SUBSYSTEM, "enm", Collections.singletonList(connectionProperties));
    }

    /**
     * GIVEN multiple subsystems which are not in use by any active service WHEN delete subsystems THEN subsystems deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_multipleSubsystemsNotInUseByActiveService_WHEN_deleteSubsystems_THEN_subsystemsDeleted() throws Exception {
        response = performMvcRequest(primarySubsystem.getId(), secondarySubsystem.getId());

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * GIVEN subsystem in use by active service WHEN delete subsystem THEN error response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemInUseByActiveService_WHEN_deleteSubsystem_THEN_errorResponse() throws Exception {
        response = performMvcRequest(primarySubsystem.getId(), subsystemWithUserReferences.getId());
        response.andExpect(MockMvcResultMatchers.status().isConflict());
        checkResponseContainsInternalErrorCode("SSM-H-12");
        checkResponseContainsErrorData(EXTRA_SUBSYSTEM);
    }

    private ResultActions performMvcRequest(Long... subsystemIds) throws Exception {
        final String deleteRequestBody = new ObjectMapper().writeValueAsString(subsystemIds);
        return mockMvc.perform(MockMvcRequestBuilders.delete(ExpectedResponse.getServiceUrl() + "/" + Constants.SUBSYSTEMS)
                .content(deleteRequestBody)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
