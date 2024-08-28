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
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class SubsystemsServiceExceptionIntegrationV2Test.
 */
public class SubsystemsServiceExceptionIntegrationV2Test extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * Setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void testSetup() {

        final List<ConnectionProperties> connectionProperties = List.of(ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DECRYPTED_PASSWORD_VALUE_ONE, TEST_PROPERTY_NAME, TEST_TENANT_NAME)));
        final List<ConnectionProperties> connectionProperties2 = Collections.singletonList(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()));
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, "enm", connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, "enm", connectionProperties2);
    }

    /**
     * GIVEN subsystem WHEN get subsystem by invalid field THEN bad request response returned.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_subsystem_WHEN_getSubsystemByInvalidField_THEN_badRequestResponseReturned() throws Exception {
        // given
        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        // when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems?select=\"name\"").contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-C-43");
        checkResponseContainsErrorData("\"name\"");
    }

}
