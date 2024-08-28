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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ericsson.bos.so.subsystemsmanager.business.exception.DataIntegrityViolationException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.NameMustBeUniqueException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemPartialDeleteException;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.MockMvcRequests;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ResponseValidator;

/**
 * The Class SubsystemControllerTest.
 */
@WebMvcTest
public class SubsystemControllerTest extends MockMvcRequests {

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(daoControllerAdvice, subsystemController).build();
    }

    /**
     * Verify greeting request then greeting message return.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGreetingRequest_thenGreetingMessageReturn() throws Exception {
        ResponseValidator.givenGreetingRequest_AssertExpectedResponses(Constants.GREETING_MESSAGE,
                MockMvcRequests.actualGreetingMessage());
    }

    /**
     * Verify given get request on subsystems then expected JSON and status code is returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnSubsystems_thenExpectedJSONAndStatusCodeIsReturned() throws Exception {
        ResponseValidator.givenGetRequestOnSubsystems_AssertExpectedResponse(ExpectedResponse.getAllSubsystems(false),
                MockMvcRequests.actualGetRequestOnSubsystem());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).fetchSubsystemByQuery(any(), any());

    }

    /**
     * Verify given post request on subsystems then expected JSON and status code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnSubsystems_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenPostRequestOnSubsystems_AssertExpectedResponse(ExpectedResponse.getSubsystem(),
                MockMvcRequests.actualPostRequestOnSubsystem());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).postSubsystem(any());

    }

    /**
     * Verify given post request on subsystems then malformed content exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnSubsystems_thenMalformedContentExceptionIsThrown() throws Exception {
        ResponseValidator.givenPostRequestOnSubsystem_AssertMalformedExceptionResponse(
                new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA),
                MockMvcRequests.actualPostRequestOnSubsystemsThrowsMalformedContent());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).postSubsystem(any());

    }

    /**
     * Verify given delete request on subsystems then expected JSON and status code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnSubsystems_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenDeleteRequestOnSubsystemsById_AssertExpectedResponse(null,
                MockMvcRequests.actualDeleteRequestOnSubsystemsById());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).deleteSubsystemById(any());

    }

    /**
     * Verify given delete request on subsystem id then malformed content exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnSubsystemId_thenMalformedContentExceptionIsThrown() throws Exception {
        ResponseValidator.givenDeleteRequestOnSubsystemId_AssertMalformedExceptionResponse(
                new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnSubsystemByIdThrowsMalformedContent());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).deleteSubsystemById(any());

    }

    /**
     * Verify given delete request on subsystem id then subsystem does not exist exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnSubsystemId_thenSubsystemDoesNotExistExceptionIsThrown() throws Exception {
        ResponseValidator.givenDeleteRequestOnSubsystemId_AssertSubsystemDoesNotExistExceptionResponse(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnSubsystemByIdThrowsSubsystemDoesNotExist());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).deleteSubsystemById(any());

    }

    /**
     * Verify given delete request on multiple subsystems then expected JSON and status code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnMultipleSubsystems_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenDeleteRequestOnMultipleSubsystemsById_AssertExpectedResponse(null,
                MockMvcRequests.actualDeleteRequestOnOrigMultipleSubsystemsById());
        verify(MockMvcRequests.subsystemsService, times(3)).deleteSubsystemById(any());
    }

    /**
     * Verify given delete request on multiple subsystems then expected partial delete exception returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnMultipleSubsystems_thenExpectedPartialDeleteExceptionReturned() throws Exception {
        ResponseValidator.givenDeleteRequestOnMultiSubsystemsById_AssertPartialDeleteResponse(
                new SubsystemPartialDeleteException(Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnMultipleSubsystemsById());
        verify(MockMvcRequests.subsystemsService, times(3)).deleteSubsystemById(any());

    }

    /**
     * Verify given post request on subsystems and malformed exception with wrong error code then error message null returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnSubsystemsAndMalformedExceptionWithWrongErrorCode_thenErrorMessageNullReturned()
            throws Exception {
        ResponseValidator.givenPostRequestOnSubsystem_AssertErrorMessageNullResponse(
                ExpectedResponse.getNullErrorMessage(),
                MockMvcRequests.actualPostRequestOnSubsystemsThrowsMalformedContentWithWrongErrorCode());

        verify(MockMvcRequests.subsystemsServiceV1, times(1)).postSubsystem(any());

    }

    /**
     * Verify given put request on subsystems then expected JSON and status code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnSubsystems_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenPutRequestOnSubsystems_AssertExpectedResponse(ExpectedResponse.getUpdatedSubsystem(),
                MockMvcRequests.actualPutRequestOnSubsystemById());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).patchSubsystem(any(), any());

    }

    /**
     * Verify given put request on subsystem id then malformed content exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnSubsystemId_thenMalformedContentExceptionIsThrown() throws Exception {
        ResponseValidator.givenPutRequestOnSubsystemId_AssertMalformedExceptionResponse(
                new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA),
                MockMvcRequests.actualPutRequestOnSubsystemByIdThrowsMalformedContent());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).patchSubsystem(any(), any());

    }

    /**
     * Verify given put request on subsystem id then subsystem does not exist exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnSubsystemId_thenSubsystemDoesNotExistExceptionIsThrown() throws Exception {
        ResponseValidator.givenDeleteRequestOnSubsystemId_AssertSubsystemDoesNotExistExceptionResponse(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualPutRequestOnSubsystemByIdThrowsSubsystemDoesNotExist());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).patchSubsystem(any(), any());

    }

    /**
     * Verify given put request on subsystem id then subsystem name must be unique exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnSubsystemId_thenSubsystemNameMustBeUniqueExceptionIsThrown() throws Exception {
        ResponseValidator.givenPutRequestOnSubsystemId_AssertGenericDatabaseExceptionResponse(
                new NameMustBeUniqueException(Constants.ERROR_DATA),
                MockMvcRequests.actualPutRequestOnSubsystemByIdThrowsSubsystemNameMustBeUnique());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).patchSubsystem(any(), any());

    }

    /**
     * Verify given put request on subsystem id then data integrity violation exception is thrown.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnSubsystemId_thenDataIntegrityViolationExceptionIsThrown() throws Exception {
        ResponseValidator.givenPutRequestOnSubsystemId_AssertDataAccessExceptionResponse(new DataIntegrityViolationException(),
                MockMvcRequests.actualPutRequestOnSubsystemByIdThrowsDataAccessException());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).patchSubsystem(any(), any());

    }

    /**
     * Verify given get request on full paginated subsystems then expected json and status code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnFullPaginatedSubsystems_thenExpectedJsonAndStatusCodeReturned()
            throws Exception {
        ResponseValidator.givenGetRequestOnFullPaginatedSubsystems_AssertExpectedResponse(
                ExpectedResponse.getPaginatedSubsystems(false), MockMvcRequests.actualGetRequestOnFullPaginatedSubsystem());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).fetchSubsystemByQuery(any(), any());

    }

    /**
     * Verify given get request on get subsystems by two filtered fields then expected json and statuc code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnGetSubsystemsByTwoFilteredFields_thenExpectedJsonAndStatucCodeReturned()
            throws Exception {
        ResponseValidator.givenGetRequestOnSubsystemsByTwoFilteredFields(ExpectedResponse.getFilteredFields(),
                MockMvcRequests.actualGetRequestOnSubsystemByTwoFilteredFields());
        verify(MockMvcRequests.subsystemJsonFilterServiceV1, times(1)).filterResponseFields(any(), any());

    }

    /**
     * Verify given get request on get subsystems by one filtered fields then expected json and statuc code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnGetSubsystemsByOneFilteredFields_thenExpectedJsonAndStatucCodeReturned()
            throws Exception {
        ResponseValidator.givenGetRequestOnSubsystemsByOneFilteredFields(ExpectedResponse.getFilteredId(),
                MockMvcRequests.actualGetRequestOnSubsystemByOneFilteredFields());
        verify(MockMvcRequests.subsystemJsonFilterServiceV1, times(1)).filterResponseSingleField(any(), any());
    }

    /**
     * Verify given get request on get subsystems by filtered json with not exist field then expected json and statuc code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnGetSubsystemsByFilteredJsonWithNotExistField_thenExpectedJsonAndStatucCodeReturned()
            throws Exception {
        ResponseValidator.givenGetRequestOnSubsystemsByFilteredJsonWithNotExistField(
                ExpectedResponse.getFilteredResponseByNotExistField(),
                MockMvcRequests.actualGetRequestOnSubsystemByFilteredJsonWithNotExistField());
        verify(MockMvcRequests.subsystemJsonFilterServiceV1, times(1)).filterResponseSingleField(any(), any());

    }

    /**
     * Verify given patch request on subsystems then expected JSON and status code returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPatchRequestOnSubsystems_thenExpectedJSONAndStatusCodeReturned() throws Exception {
        ResponseValidator.givenPutRequestOnSubsystems_AssertExpectedResponse(ExpectedResponse.getUpdatedSubsystem(),
                MockMvcRequests.actualPutRequestOnSubsystemById());
        verify(MockMvcRequests.subsystemsServiceV1, times(1)).patchSubsystem(any(), any());

    }

}
