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

import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.GenericDatabaseException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.MockMvcRequests;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ResponseValidator;

/**
 * The Class ConnectionPropertiesControllerTest.
 */
@WebMvcTest
public class ConnectionPropertiesControllerTest extends MockMvcRequests {

    /**
     * Setup.
     *
     * @throws Exception
     *             the exception
     */
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(daoControllerAdvice, connectionPropertiesController).build();
    }

    /**
     * Verify given post request on connection properties then expected JSON and status code is returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnConnectionProperties_ThenExpectedJSONAndStatusCodeIsReturned()
            throws Exception {
        ResponseValidator.givenPostRequestOnConnectionProperties_AssertExpectedResponse(
                ExpectedResponse.getConnectionPropertiesDetail(),
                MockMvcRequests.actualPostRequestOnConnectionProperties());
        verify(MockMvcRequests.connectionPropsService, times(1)).postConnProp((any()), (any()));
    }

    /**
     * Verify given get request on connection properties using subsystem id then expected JSON and status code is returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnConnectionPropertiesUsingSubsystemId_ThenExpectedJSONAndStatusCodeIsReturned()
            throws Exception {
        ResponseValidator.givenGetRequestOnConnectionProperties_AssertExpectedResponse(
                ExpectedResponse.getServiceResponseDataForGet(),
                MockMvcRequests.actualGetRequestOnConnectionProperties());
        verify(MockMvcRequests.connectionPropsService, times(1)).getConnPropsBySubsystemId(any());

    }

    /**
     * Verify given get request on connection properties using subsystem id and connection properties id then expected JSON and status code is
     * returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnConnectionPropertiesUsingSubsystemIdAndConnectionPropertiesId_ThenExpectedJSONAndStatusCodeIsReturned()
            throws Exception {
        ResponseValidator.givenGetRequestOnConnectionPropertiesWithAllIds_AssertExpectedResponse(
                ExpectedResponse.getServiceResponseDataForGetWithAllIds(),
                MockMvcRequests.actualGetRequestOnConnectionPropertiesWithAllIds());
        verify(MockMvcRequests.connectionPropsService, times(1)).getConnPropsById(any(), any());
    }

    /**
     * Verify given put request on connection properties using subsystem id and connection properties id then expected JSON and status code is
     * returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnConnectionPropertiesUsingSubsystemIdAndConnectionPropertiesId_ThenExpectedJSONAndStatusCodeIsReturned()
            throws Exception {
        ResponseValidator.givenPutRequestOnConnectionPropertiesUsingIdAndConnectionPropertiesId_AssertExpectedResponse(
                ExpectedResponse.getServiceResponseDataForPutWithAllIdsAndMap(),
                MockMvcRequests.actualPutRequestOnConnectionPropertiesWithAllIdsAndMap());
        verify(MockMvcRequests.connectionPropsService, times(1)).putConnProps(any(), any(), any());
    }

    /**
     * Verify given post request on connection properties using subsystem id and connection properties id then expected JSON and status code is
     * returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnConnectionPropertiesUsingSubsystemIdAndConnectionPropertiesId_ThenExpectedJSONAndStatusCodeIsReturned()
            throws Exception {
        ResponseValidator.givenPutRequestOnConnectionPropertiesUsingIdAndConnectionPropertiesId_AssertExpectedResponse(
                ExpectedResponse.getServiceResponseDataForPutWithAllIdsAndMap(),
                MockMvcRequests.actualPatchRequestOnConnectionPropertiesWithAllIdsAndMap());
        verify(MockMvcRequests.connectionPropsService, times(1)).patchConnProps(any(), any(), any());
    }

    /**
     * Verify given delete request on connection properties using id then expected JSON and status code is returned.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnConnectionPropertiesUsingId_ThenExpectedJSONAndStatusCodeIsReturned()
            throws Exception {
        ResponseValidator.givenDeleteRequestOnConnectionPropertiesUsingId_AssertExpectedResponse(null,
                MockMvcRequests.actualDeleteRequestOnConnectionPropertiesUsingId());
        verify(MockMvcRequests.connectionPropsService, times(1)).deleteConnProps(any(), any());
    }

    /**
     * Verify given post request on connection properties then throw connection properties does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnConnectionProperties_ThenThrowConnectionPropertiesDoesNotExistException()
            throws Exception {
        ResponseValidator.givenPostRequestOnConnectionProperties_AssertConnectionPropertiesDoesNotExistException(
                new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests
                        .actualPostRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).postConnProp(any(), any());
    }

    /**
     * Verify given post request on connection properties then throw malformed exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPostRequestOnConnectionProperties_ThenThrowMalformedException() throws Exception {
        ResponseValidator.givenPostRequestOnConnectionProperties_ThrowMalformedException(
                new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA),
                MockMvcRequests.actualPostRequestOnConnectionPropertiesThrowsMalformedException());
        verify(MockMvcRequests.connectionPropsService, times(1)).postConnProp(any(), any());
    }

    /**
     * Verify given get request on connection properties then throw subsystem does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnConnectionProperties_ThenThrowSubsystemDoesNotExistException() throws Exception {
        ResponseValidator.givenGetRequestOnConnectionProperties_ThrowSubsystemDoesNotExistException(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualGetRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).getConnPropsBySubsystemId(any());
    }

    /**
     * Verify given get request on connection properties then throw connection properties does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenGetRequestOnConnectionProperties_ThenThrowConnectionPropertiesDoesNotExistException()
            throws Exception {
        ResponseValidator.givenGetRequestOnConnectionProperties_AssertConnectionPropertiesDoesNotExistException(
                new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests
                        .actualGetRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).getConnPropsById(any(), any());
    }

    /**
     * Verify given put request on connection properties then throw subsystem does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnConnectionProperties_ThenThrowSubsystemDoesNotExistException() throws Exception {
        ResponseValidator.givenPutRequestOnConnectionProperties_AssertExceptionIsThrown(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests
                        .actualPutRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).putConnProps(any(), any(), any());
    }

    /**
     * Verify given put request on connection properties then throw connection properties does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPutRequestOnConnectionProperties_ThenThrowConnectionPropertiesDoesNotExistException()
            throws Exception {
        ResponseValidator.givenPutRequestOnConnectionProperties_AssertExceptionIsThrown(
                new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests
                        .actualPutRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).putConnProps(any(), any(), any());
    }

    /**
     * Verify given patch request on connection properties then throw subsystem does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPatchRequestOnConnectionProperties_ThenThrowSubsystemDoesNotExistException()
            throws Exception {
        ResponseValidator.givenPutRequestOnConnectionProperties_AssertExceptionIsThrown(
                new SubsystemDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests.actualPatchRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).patchConnProps(any(), any(), any());
    }

    /**
     * Verify given patch request on connection properties then throw connection properties does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenPatchRequestOnConnectionProperties_ThenThrowConnectionPropertiesDoesNotExistException()
            throws Exception {
        ResponseValidator.givenPutRequestOnConnectionProperties_AssertExceptionIsThrown(
                new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA),
                MockMvcRequests
                        .actualPatchRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).patchConnProps(any(), any(), any());
    }

    /**
     * Verify given delete request on connection properties then throw subsystem does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnConnectionProperties_ThenThrowSubsystemDoesNotExistException()
            throws Exception {
        ResponseValidator.givenDeleteRequestOnConnectionProperties_ThrowException(
                new GenericDatabaseException(Constants.ERROR_DATA),
                MockMvcRequests.actualDeleteRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).deleteConnProps(any(), any());
    }

    /**
     * Verify given delete request on connection properties then throw connection properties does not exist exception.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void verifyGivenDeleteRequestOnConnectionProperties_ThenThrowConnectionPropertiesDoesNotExistException()
            throws Exception {
        ResponseValidator.givenDeleteRequestOnConnectionProperties_ThrowException(
                new GenericDatabaseException(Constants.ERROR_DATA),
                MockMvcRequests
                        .actualDeleteRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException());
        verify(MockMvcRequests.connectionPropsService, times(1)).deleteConnProps(any(), any());
    }
}
