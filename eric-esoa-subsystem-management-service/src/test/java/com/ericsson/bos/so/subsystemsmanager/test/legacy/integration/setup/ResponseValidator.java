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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.exception.*;
import com.ericsson.oss.orchestration.so.common.error.exception.ErrorMessageFactoryException;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.ericsson.oss.orchestration.so.common.error.message.*;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The Class ResponseValidator.
 */
public class ResponseValidator {

    /**
     * Given post request on connection properties assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnConnectionProperties_AssertExpectedResponse(
            ConnectionProperties expectedResponse, ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().is(Constants.STATUS_CODE_CREATED));
        doesActualResponseMapsToConnectionPropertiesFields(actualResponse);

        actualResponse.andExpect(jsonPath(Constants.$_NAME, is(expectedResponse.getProperties().get(2).getValue())))
                .andExpect(jsonPath(Constants.$_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath(Constants.$_PASSWORD, is(expectedResponse.getProperties().get(0).getValue())))
                .andExpect(jsonPath(Constants.$_USERNAME, is(expectedResponse.getProperties().get(1).getValue())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getSubsystem().getId().toString()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getSubsystemId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEMUSERS_ID)
                        .value(expectedResponse.getSubsystemUsers().get(0).getId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEMUSERS_CONNPROPS_ID)
                        .value(expectedResponse.getSubsystemUsers().get(0).getConnectionPropsId()));
    }

    /**
     * Given get request on connection properties assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnConnectionProperties_AssertExpectedResponse(
            List<ConnectionProperties> expectedResponse, ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().is(Constants.STATUS_CODE_OK)).andExpect(jsonPath(Constants.ZERO_ID).exists())
                .andExpect(jsonPath(Constants.ZERO_SUBSYSTEMID).exists())
                .andExpect(jsonPath(Constants.ZERO_NAME).exists()).andExpect(jsonPath(Constants.ZERO_USERNAME).exists())
                .andExpect(jsonPath(Constants.ZERO_PASSWORD).exists())
                .andExpect(jsonPath(Constants.ZERO_SUBSYSTEMUSERS).exists())
                .andExpect(jsonPath(Constants.ZERO_SUBSYSTEMUSERS_ANDID).exists())
                .andExpect(jsonPath(Constants.ZERO_SUBSYSTEMUSERS_AND_CONNPROPSID).exists());

        actualResponse.andExpect(jsonPath(Constants.ZERO_ID).value(expectedResponse.get(0).getId().toString()))
                .andExpect(
                        jsonPath(Constants.ZERO_SUBSYSTEMID).value(expectedResponse.get(0).getSubsystemId().toString()))
                .andExpect(jsonPath(Constants.ZERO_NAME).value(expectedResponse.get(0).getProperties().get(2).getValue()))
                .andExpect(jsonPath(Constants.ZERO_USERNAME).value(expectedResponse.get(0).getProperties().get(1).getValue()))
                .andExpect(jsonPath(Constants.ZERO_PASSWORD).value(expectedResponse.get(0).getProperties().get(0).getValue()))
                .andExpect(jsonPath(Constants.ZERO_SUBSYSTEMUSERS_ANDID)
                        .value(expectedResponse.get(0).getSubsystemUsers().get(0).getId().toString()))
                .andExpect(jsonPath(Constants.ZERO_SUBSYSTEMUSERS_AND_CONNPROPSID)
                        .value(expectedResponse.get(0).getSubsystemUsers().get(0).getConnectionPropsId().toString()));
    }

    /**
     * Given get request on connection properties with all ids assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnConnectionPropertiesWithAllIds_AssertExpectedResponse(
            ConnectionProperties expectedResponse, ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().is(Constants.STATUS_CODE_OK));
        doesActualResponseMapsToConnectionPropertiesFields(actualResponse);
        doesActualResponseMapsToConnectionPropertiesBasedOnFieldValues(expectedResponse, actualResponse);

    }

    /**
     * Given put request on connection properties using id and connection properties id assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPutRequestOnConnectionPropertiesUsingIdAndConnectionPropertiesId_AssertExpectedResponse(
            ConnectionProperties expectedResponse, ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().is(Constants.STATUS_CODE_CREATED));
        doesActualResponseMapsToConnectionPropertiesFields(actualResponse);
        doesActualResponseMapsToConnectionPropertiesBasedOnFieldValues(expectedResponse, actualResponse);
    }

    /**
     * Given delete request on connection properties using id assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnConnectionPropertiesUsingId_AssertExpectedResponse(Object expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(status().isNoContent());

    }

    /**
     * Given post request on connection properties assert connection properties does not exist exception.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnConnectionProperties_AssertConnectionPropertiesDoesNotExistException(
            ConnectionPropertiesDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {

        checkThatConnPropDataIsMatching(expectedResponse, actualResponse);
    }

    /**
     * Given post request on connection properties throw malformed exception.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnConnectionProperties_ThrowMalformedException(
            SubsystemsManagerException expectedResponse, ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().is(Constants.BAD_REQUEST))
                .andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_ERROR_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(jsonPath(Constants.$_ERROR_MESSAGE, is(expectedResponse.getErrorData())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));

    }

    /**
     * Given get request on connection properties throw subsystem does not exist exception.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnConnectionProperties_ThrowSubsystemDoesNotExistException(
            SubsystemsManagerException expectedResponse, ResultActions actualResponse) throws Exception {

        checkThatDataIsMatching(expectedResponse, actualResponse);

    }

    /**
     * Given get request on connection properties assert connection properties does not exist exception.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnConnectionProperties_AssertConnectionPropertiesDoesNotExistException(
            ConnectionPropertiesDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {

        checkThatDataIsMatching(expectedResponse, actualResponse);

    }

    /**
     * Given put request on connection properties assert exception is thrown.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPutRequestOnConnectionProperties_AssertExceptionIsThrown(
            SubsystemsManagerException expectedResponse, ResultActions actualResponse) throws Exception {

        checkThatDataIsMatching(expectedResponse, actualResponse);
    }

    /**
     * Given delete request on connection properties throw exception.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnConnectionProperties_ThrowException(
            SubsystemsManagerException expectedResponse, ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Check that connection properties data is matching.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    private static void checkThatConnPropDataIsMatching(ConnectionPropertiesDoesNotExistException expectedResponse, ResultActions actualResponse)
            throws Exception {
        actualResponse.andExpect(status().is(Constants.NOT_FOUND))
                .andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_ERROR_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(jsonPath(Constants.$_ERROR_MESSAGE, is(expectedResponse.getErrorData())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));

    }

    /**
     * Check that data is matching.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    private static void checkThatDataIsMatching(SubsystemsManagerException expectedResponse, ResultActions actualResponse)
            throws Exception {
        actualResponse.andExpect(status().is(Constants.NOT_FOUND))
                .andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_ERROR_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(jsonPath(Constants.$_ERROR_DATA, is(expectedResponse.getErrorData())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Does actual response maps to connection properties based on field values.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    private static void doesActualResponseMapsToConnectionPropertiesBasedOnFieldValues(
            ConnectionProperties expectedResponse, ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(jsonPath(Constants.$_NAME, is(expectedResponse.getProperties().get(2).getValue())))
                .andExpect(jsonPath(Constants.$_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath(Constants.$_PASSWORD, is(expectedResponse.getProperties().get(0).getValue())))
                .andExpect(jsonPath(Constants.$_USERNAME, is(expectedResponse.getProperties().get(1).getValue())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getSubsystemId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEMUSERS_ID)
                        .value(expectedResponse.getSubsystemUsers().get(0).getId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEMUSERS_CONNPROPS_ID)
                        .value(expectedResponse.getSubsystemUsers().get(0).getConnectionPropsId()));
    }

    /**
     * Does actual response maps to connection properties fields.
     *
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    private static void doesActualResponseMapsToConnectionPropertiesFields(ResultActions actualResponse)
            throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_ID).exists()).andExpect(jsonPath(Constants.$_NAME).exists())
                .andExpect(jsonPath(Constants.$_PASSWORD).exists()).andExpect(jsonPath(Constants.$_USERNAME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_USERS).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEMUSERS_ID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEMUSERS_CONNPROPS_ID).exists());
    }

    /**
     * Given greeting request assert expected responses.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGreetingRequest_AssertExpectedResponses(String expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(content().string(expectedResponse)).andExpect(status().isOk());
    }

    /**
     * Given get request on subsystems assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnSubsystems_AssertExpectedResponse(List<Subsystem> expectedResponse,
            ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().isOk()).andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_TYPEID).exists());

        actualResponse.andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME, is(expectedResponse.get(0).getName())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME,
                        is(expectedResponse.get(0).getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL, is(expectedResponse.get(0).getUrl())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE,
                        is(expectedResponse.get(0).getOperationalState().toString())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_TYPEID)
                        .value(expectedResponse.get(0).getSubsystemTypeId()));

    }

    /**
     * Given post request on subsystems assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnSubsystems_AssertExpectedResponse(Subsystem expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_URL).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_TYEPID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_STATE).exists());

        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME, is(expectedResponse.getName())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_HEALTH_CHECK_TIME, is(expectedResponse.getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_URL, is(expectedResponse.getUrl())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_TYEPID).value(expectedResponse.getSubsystemTypeId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_STATE, is(expectedResponse.getOperationalState().toString())))
                .andExpect(status().isCreated());
    }

    /**
     * Given get request on subsystem by id assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnSubsystemById_AssertExpectedResponse(Subsystem expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_URL).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_TYEPID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_STATE).exists());

        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME, is(expectedResponse.getName())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_HEALTH_CHECK_TIME, is(expectedResponse.getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_URL, is(expectedResponse.getUrl())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_TYEPID).value(expectedResponse.getSubsystemTypeId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_STATE, is(expectedResponse.getOperationalState().toString())))
                .andExpect(status().isOk());
    }

    /**
     * Given delete request on subsystems by id assert expected response.
     *
     * @param object
     *            the object
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnSubsystemsById_AssertExpectedResponse(Object object,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(status().isNoContent());
    }

    /**
     * Given put request on subsystems assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPutRequestOnSubsystems_AssertExpectedResponse(Subsystem expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_URL).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_TYEPID).exists())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_STATE).exists());

        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME, is(expectedResponse.getName())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_HEALTH_CHECK_TIME, is(expectedResponse.getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_URL, is(expectedResponse.getUrl())))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_TYEPID).value(expectedResponse.getSubsystemTypeId()))
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_STATE, is(expectedResponse.getOperationalState().toString())))
                .andExpect(status().isOk());

    }

    /**
     * Given post request on subsystem user assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnSubsystemUser_AssertExpectedResponse(SubsystemUser expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).exists())
                .andExpect(jsonPath(Constants.$_CONNPROP_ID).exists());

        actualResponse.andExpect(jsonPath(Constants.$_SUBSYSTEM_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath(Constants.$_CONNPROP_ID).value(expectedResponse.getConnectionPropsId()));

    }

    /**
     * Given delete request on subsystem user assert expected response.
     *
     * @param object
     *            the object
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnSubsystemUser_AssertExpectedResponse(Object object,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(status().isNoContent());
    }

    /**
     * Given post request on subsystem assert malformed exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnSubsystem_AssertMalformedExceptionResponse(
            MalformedContentException expectedResponse, ResultActions actualResponse) throws Exception {
        assertMalformedContentExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isBadRequest());
    }

    /**
     * Given post request on subsystem assert error message null response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPostRequestOnSubsystem_AssertErrorMessageNullResponse(
            ErrorMessage expectedResponse, ResultActions actualResponse) throws Exception {
        givenErrorMessageFactoryException_AssertErrorMessageEmptyResponse(actualResponse);
        actualResponse.andExpect(status().isBadRequest());
    }

    /**
     * Given delete request on subsystem id assert malformed exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnSubsystemId_AssertMalformedExceptionResponse(
            MalformedContentException expectedResponse, ResultActions actualResponse) throws Exception {
        assertMalformedContentExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isBadRequest());
    }

    /**
     * Given put request on subsystem id assert malformed exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPutRequestOnSubsystemId_AssertMalformedExceptionResponse(
            MalformedContentException expectedResponse, ResultActions actualResponse) throws Exception {
        assertMalformedContentExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isBadRequest());
    }

    /**
     * Assert generic database exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertGenericDatabaseExceptionResponse(GenericDatabaseException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert data integrity violation exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertDataIntegrityViolationExceptionResponse(DataIntegrityViolationException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert name must be unique exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertNameMustBeUniqueExceptionResponse(NameMustBeUniqueException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert subsystem does not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertSubsystemDoesNotExistExceptionResponse(SubsystemDoesNotExistException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert subsystem user does not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertSubsystemUserDoesNotExistExceptionResponse(SubsystemUserDoesNotExistException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert connection prop does not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertConnectionPropDoesNotExistExceptionResponse(ConnectionPropertiesDoesNotExistException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert subsystem partial delete exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void assertSubsystemPartialDeleteExceptionResponse(SubsystemPartialDeleteException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_ERROR_DATA).exists())
                .andExpect(jsonPath(Constants.$_ERROR_DATA, is(expectedResponse.getErrorData())))
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Assert base error message factory response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void AssertBaseErrorMessageFactoryResponse(ErrorMessageFactoryException expectedResponse,
            ResultActions actualResponse) throws Exception {
        givenErrorMessageFactoryException_AssertErrorMessageEmptyResponse(actualResponse);
    }

    /**
     * Given error message factory exception assert error message empty response.
     *
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenErrorMessageFactoryException_AssertErrorMessageEmptyResponse(ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).doesNotExist())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).doesNotExist());
        actualResponse.andReturn().getResponse().getContentAsString().isEmpty();
    }

    /**
     * Given put request on subsystem id assert generic database exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPutRequestOnSubsystemId_AssertGenericDatabaseExceptionResponse(
            NameMustBeUniqueException expectedResponse, ResultActions actualResponse) throws Exception {
        assertNameMustBeUniqueExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isConflict());
    }

    /**
     * Given delete request on subsystem id assert subsystem does not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnSubsystemId_AssertSubsystemDoesNotExistExceptionResponse(
            SubsystemDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {
        assertSubsystemDoesNotExistExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Given invalid subsystem id when post request on subsystem user assert malformed exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidSubsystemId_whenPostRequestOnSubsystemUser_AssertMalformedExceptionResponse(
            MalformedContentException expectedResponse, ResultActions actualResponse) throws Exception {
        assertMalformedContentExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isBadRequest());
    }

    /**
     * Given invalid subsystem id when post request on subsystem user assert subsystem does not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidSubsystemId_whenPostRequestOnSubsystemUser_AssertSubsystemDoesNotExistExceptionResponse(
            SubsystemDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {
        assertSubsystemDoesNotExistExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Given invalid connection properties id when post request on subsystem user assert connection properties does not exists response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidConnPropId_whenPostRequestOnSubsystemUser_AssertConnPropDoesNotExistsResponse(
            ConnectionPropertiesDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {
        assertConnectionPropDoesNotExistExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Given invalid id when deletet request on subsystem user assert malformed exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertMalformedExceptionResponse(
            MalformedContentException expectedResponse, ResultActions actualResponse) throws Exception {
        assertMalformedContentExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isBadRequest());

    }

    /**
     * Given invalid id when deletet request on subsystem user assert subsystem not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertSubsystemNotExistExceptionResponse(
            SubsystemDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {
        assertSubsystemDoesNotExistExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Given invalid id when deletet request on subsystem user assert subsystem user not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertSubsystemUserNotExistExceptionResponse(
            SubsystemUserDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {
        assertSubsystemUserDoesNotExistExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Given invalid id when deletet request on subsystem user assert connection properties does not exist exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenInvalidId_whenDeletetRequestOnSubsystemUser_AssertConnPropDoesNotExistExceptionResponse(
            ConnectionPropertiesDoesNotExistException expectedResponse, ResultActions actualResponse) throws Exception {
        assertConnectionPropDoesNotExistExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isNotFound());
    }

    /**
     * Assert malformed content exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    private static void assertMalformedContentExceptionResponse(MalformedContentException expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.$_USER_MESSAGE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE).exists())
                .andExpect(jsonPath(Constants.$_INTERNAL_ERROR_CODE, is(expectedResponse.getInternalErrorCode())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Given put request on subsystem id assert data access exception response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenPutRequestOnSubsystemId_AssertDataAccessExceptionResponse(
            DataIntegrityViolationException expectedResponse, ResultActions actualResponse) throws Exception {
        assertDataIntegrityViolationExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isConflict());
    }

    /**
     * Given get request on full paginated subsystems assert expected response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnFullPaginatedSubsystems_AssertExpectedResponse(SubsystemList expectedResponse,
            ResultActions actualResponse) throws Exception {
        System.out.println(actualResponse.andDo(MockMvcResultHandlers.print()).toString());

        actualResponse.andExpect(status().isOk()).andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_TYPEID).exists());

        actualResponse
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME, is(expectedResponse.getItems().get(0).getName())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).value(expectedResponse.getItems().get(0).getId()))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME,
                        is(expectedResponse.getItems().get(0).getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL, is(expectedResponse.getItems().get(0).getUrl())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_TYPEID)
                        .value(expectedResponse.getItems().get(0).getSubsystemTypeId()))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE,
                        is(expectedResponse.getItems().get(0).getOperationalState().toString())))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_NAME,
                        is(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getProperties().get(2).getValue())))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_ID)
                        .value(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getId()))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_SUBSYSTEM_ID)
                        .value(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getSubsystemId()))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_USERNAME,
                        is(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getProperties().get(1).getValue())))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_PASSWORD,
                        is(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getProperties().get(0).getValue())))
                .andExpect(status().isOk());

        actualResponse.andExpect(header().string(Constants.TOTAL, String.valueOf(expectedResponse.getTotal())));

    }

    /**
     * Given get request on subsystems by one filtered fields.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnSubsystemsByOneFilteredFields(List<Object> expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(content().string(containsString(String.valueOf(expectedResponse.get(0)))))
                .andExpect(content().string(containsString(String.valueOf(expectedResponse.get(1)))))
                .andExpect(content().contentType(Constants.APPLICATION_JSON)).andExpect(status().isOk());
    }

    /**
     * Given get request on subsystems by filtered json with not exist field.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnSubsystemsByFilteredJsonWithNotExistField(List<Object> expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(content().string(containsString(expectedResponse.toString())))
                .andExpect(content().contentType(Constants.APPLICATION_JSON)).andExpect(status().isOk());
    }

    /**
     * Given get request on subsystems by two filtered fields.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnSubsystemsByTwoFilteredFields(List<Subsystem> expectedResponse,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(jsonPath(Constants.ZERO_NAME).exists())
                .andExpect(jsonPath(Constants.ZERO_ID).exists());

        actualResponse.andExpect(jsonPath(Constants.ZERO_NAME, is(expectedResponse.get(0).getName())))
                .andExpect(jsonPath(Constants.ZERO_ID).value(expectedResponse.get(0).getId()))
                .andExpect(status().isOk());
    }

    /**
     * Given delete request on multiple subsystems by id assert expected response.
     *
     * @param object
     *            the object
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnMultipleSubsystemsById_AssertExpectedResponse(Object object,
            ResultActions actualResponse) throws Exception {
        actualResponse.andExpect(status().isNoContent());
    }

    /**
     * Given delete request on multi subsystems by id assert partial delete response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnMultiSubsystemsById_AssertPartialDeleteResponse(SubsystemPartialDeleteException expectedResponse,
            ResultActions actualResponse) throws Exception {
        assertSubsystemPartialDeleteExceptionResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isInternalServerError()); // INTERNAL_SERVER_ERROR
    }

    /**
     * Given delete request on multi subsystems by id assert base error message factory response.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenDeleteRequestOnMultiSubsystemsById_AssertBaseErrorMessageFactoryResponse(ErrorMessageFactoryException expectedResponse,
            ResultActions actualResponse) throws Exception {
        AssertBaseErrorMessageFactoryResponse(expectedResponse, actualResponse);
        actualResponse.andExpect(status().isInternalServerError());
    }

    /**
     * Given get request on subsystems assert expected response V2.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnSubsystems_AssertExpectedResponse_V2(List<Subsystem> expectedResponse,
            ResultActions actualResponse) throws Exception {

        actualResponse.andExpect(status().isOk()).andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_API_KEY).exists());

        actualResponse.andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME, is(expectedResponse.get(0).getName())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME,
                        is(expectedResponse.get(0).getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL, is(expectedResponse.get(0).getUrl())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE,
                        is(expectedResponse.get(0).getOperationalState().toString())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_API_KEY)
                        .value(expectedResponse.get(0).getApiKey().toString()));
    }

    /**
     * Given get request on full paginated subsystems assert expected response V2.
     *
     * @param expectedResponse
     *            the expected response
     * @param actualResponse
     *            the actual response
     * @throws Exception
     *             the exception
     */
    public static void givenGetRequestOnFullPaginatedSubsystems_AssertExpectedResponse_V2(
            SubsystemList expectedResponse, ResultActions actualResponse) throws Exception {
        System.out.println(actualResponse.andDo(MockMvcResultHandlers.print()).toString());

        actualResponse.andExpect(status().isOk()).andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE).exists())
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_API_KEY).exists());

        actualResponse
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_NAME, is(expectedResponse.getItems().get(0).getName())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_ID).value(expectedResponse.getItems().get(0).getId()))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_HEALTH_CHECK_TIME,
                        is(expectedResponse.getItems().get(0).getHealthCheckTime())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_URL, is(expectedResponse.getItems().get(0).getUrl())))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_API_KEY)
                        .value(expectedResponse.getItems().get(0).getApiKey().toString()))
                .andExpect(jsonPath(Constants.$0_SUBSYSTEM_ONE_STATE,
                        is(expectedResponse.getItems().get(0).getOperationalState().toString())))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_NAME,
                        is(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getProperties().get(2)
                                .getValue())))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_ID)
                        .value(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getId()))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_SUBSYSTEM_ID)
                        .value(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getSubsystemId()))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_USERNAME,
                        is(expectedResponse.getItems().get(0).getConnectionProperties().get(0).getProperties().get(1)
                                .getValue())))
                .andExpect(jsonPath(Constants.$0_CONN_PROP_PASSWORD, is(expectedResponse.getItems().get(0)
                        .getConnectionProperties().get(0).getProperties().get(0).getValue())))
                .andExpect(status().isOk());

        actualResponse.andExpect(header().string(Constants.TOTAL, String.valueOf(expectedResponse.getTotal())));

    }
}
