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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.controller.v2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.MockMvcRequests;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ResponseValidator;

/**
 * The Class SubsystemControllerV2Test.
 */
@WebMvcTest
public class SubsystemControllerV2Test extends MockMvcRequests {

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(daoControllerAdvice, subsystemControllerV2).build();
    }

    /**
     * Verify given get request on subsystems then expected JSON and status code is returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenGetRequestOnSubsystems_thenExpectedJSONAndStatusCodeIsReturned() throws Exception {
        ResponseValidator.givenGetRequestOnSubsystems_AssertExpectedResponse_V2(ExpectedResponse.getAllSubsystems(true),
                MockMvcRequests.actualGetRequestOnSubsystemV2());
        verify(MockMvcRequests.subsystemsServiceV2, times(1)).fetchSubsystemByQuery(any(), any());

    }

    /**
     * Verify given get request on full paginated subsystems then expected json and status code returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenGetRequestOnFullPaginatedSubsystems_thenExpectedJsonAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenGetRequestOnFullPaginatedSubsystems_AssertExpectedResponse_V2(ExpectedResponse.getPaginatedSubsystems(true),
                MockMvcRequests.actualGetRequestOnFullPaginatedSubsystemV2());
        verify(MockMvcRequests.subsystemsServiceV2, times(1)).fetchSubsystemByQuery(any(), any());

    }

    /**
     * Verify given get request on get subsystems by two filtered fields then expected json and status code returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenGetRequestOnGetSubsystemsByTwoFilteredFields_thenExpectedJsonAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenGetRequestOnSubsystemsByTwoFilteredFields(ExpectedResponse.getFilteredFields(),
                MockMvcRequests.actualGetRequestOnSubsystemByTwoFilteredFieldsV2());
        verify(MockMvcRequests.subsystemJsonFilterServiceV2, times(1)).filterResponseFields(any(), any());

    }

    /**
     * Verify given get request on get subsystems by one filtered fields then expected json and status code returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenGetRequestOnGetSubsystemsByOneFilteredFields_thenExpectedJsonAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenGetRequestOnSubsystemsByOneFilteredFields(ExpectedResponse.getFilteredId(),
                MockMvcRequests.actualGetRequestOnSubsystemByOneFilteredFieldsV2());
        verify(MockMvcRequests.subsystemJsonFilterServiceV2, times(1)).filterResponseSingleField(any(), any());
    }

    /**
     * Verify given get request on get subsystems by filtered json with not exist field then expected json and status code returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenGetRequestOnGetSubsystemsByFilteredJsonWithNotExistField_thenExpectedJsonAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenGetRequestOnSubsystemsByFilteredJsonWithNotExistField(ExpectedResponse.getFilteredResponseByNotExistField(),
                MockMvcRequests.actualGetRequestOnSubsystemByFilteredJsonWithNotExistFieldV2());
        verify(MockMvcRequests.subsystemJsonFilterServiceV2, times(1)).filterResponseSingleField(any(), any());

    }

}
