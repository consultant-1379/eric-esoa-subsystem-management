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

import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemUserDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.MockMvcRequests;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ResponseValidator;

/**
 * The Class SubsystemUserControllerV2Test.
 */
@WebMvcTest
public class SubsystemUserControllerV2Test extends MockMvcRequests {

    private final String subsystemUrl = ExpectedResponse.getSubsystemUrlV2();

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(daoControllerAdvice, subsystemUserControllerV2).build();
    }

    /**
     * Verify given post request on subsystem user then expected JSON and status code returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenPostRequestOnSubsystemUser_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenPostRequestOnSubsystemUser_AssertExpectedResponse(ExpectedResponse.getSubsystemUser(),
                MockMvcRequests.actualPostRequestOnSubsystemUser(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).postUserByConnsPropId(any(), any());

    }

    /**
     * Verify given invalid subsystem id when post request on subsystem user then malformed content exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenInvalidSubsystemId_whenPostRequestOnSubsystemUser_thenMalformedContentExceptionIsThrown() throws Exception {
        ResponseValidator.givenInvalidSubsystemId_whenPostRequestOnSubsystemUser_AssertMalformedExceptionResponse(
                new MalformedContentException("SSM-B-25", Constants.ERROR_DATA),
                MockMvcRequests.actualPostRequestOnSubsystemUserThrowsMalformedContent(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).postUserByConnsPropId(any(), any());

    }

    /**
     * Verify given invalid subsystem id when post request on subsystem user then subsystem does not exist exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenInvalidSubsystemId_whenPostRequestOnSubsystemUser_thenSubsystemDoesNotExistExceptionIsThrown() throws Exception {
        ResponseValidator.givenInvalidSubsystemId_whenPostRequestOnSubsystemUser_AssertSubsystemDoesNotExistExceptionResponse(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualPostRequestOnSubsystemUserThrowsSubsystemDoesNotExist(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).postUserByConnsPropId(any(), any());

    }

    /**
     * Verify given invalid  connection properties id
     * when post request on subsystem user then connection properties does not exist exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenInvalidConnectionPropertiesId_whenPostRequestOnSubsystemUser_thenConnPropDoesNotExistExceptionIsThrown() throws Exception {
        ResponseValidator.givenInvalidConnPropId_whenPostRequestOnSubsystemUser_AssertConnPropDoesNotExistsResponse(
                new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualPostRequestOnSubsystemUserThrowConnPropNotExist(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).postUserByConnsPropId(any(), any());

    }

    /**
     * Verify given delete request on subsystem user then expected JSON and status code returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void veriftGivenDeleteRequestOnSubsystemUser_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenDeleteRequestOnSubsystemUser_AssertExpectedResponse(null,
                MockMvcRequests.actualDeleteRequestOSubsystemUser(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).deleteSubsystemUserById(any(), any(),any());

    }

    /**
     * Verify given invalid subsystem id when delete request on subsystem user then malformed content exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenInvalidSubsystemId_whenDeleteRequestOnSubsystemUser_thenMalformedContentExceptionIsThrown() throws Exception {
        ResponseValidator.givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertMalformedExceptionResponse(
                new MalformedContentException("SSM-B-25", Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnSubsystemUserThrowsMalformedContent(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).deleteSubsystemUserById(any(), any(),any());

    }

    /**
     * Verify given not exist subsystem id when delete request on subsystem user then subsystem does not exist exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenNotExistSubsystemId_whenDeleteRequestOnSubsystemUser_thenSubsystemDoesNotExistExceptionIsThrown() throws Exception {
        ResponseValidator.givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertSubsystemNotExistExceptionResponse(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnSubsystemUserThrowsSubsystemDoesNotExist(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).deleteSubsystemUserById(any(), any(),any());

    }

    /**
     * Verify given not exist subsystem user id
     * when delete request on subsystem user then subsystem user does not exist exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenNotExistSubsystemUserId_whenDeleteRequestOnSubsystemUser_thenSubsystemUserDoesNotExistExceptionIsThrown()
            throws Exception {
        ResponseValidator.givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertSubsystemUserNotExistExceptionResponse(
                new SubsystemUserDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnSubsystemUserThrowsSubsystemUserDoesNotExist(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).deleteSubsystemUserById(any(), any(), any());

    }

    /**
     * Verify given not exist connection properties id
     * when delete request on subsystem user then connection properties does not exist exception is thrown.
     *
     * @throws Exception the exception
     */
    @Test
    public void verifyGivenNotExistConnPropId_whenDeleteRequestOnSubsystemUser_thenConnPropDoesNotExistExceptionIsThrown() throws Exception {
        ResponseValidator.givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertConnPropDoesNotExistExceptionResponse(
                new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnSubsystemUserThrowsConnPropDoesNotExist(subsystemUrl));
        verify(MockMvcRequests.subsystemUserService, times(1)).deleteSubsystemUserById(any(), any(),any());

    }

}

