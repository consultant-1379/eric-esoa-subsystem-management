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

import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_CONN_PROPS_JSON;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_CONN_PROPS_LIST_JSON;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_FILTERED_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_SUBSYSTEM_JSON;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_SUBSYSTEM_LIST_JSON;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_SUBSYSTEM_LIST_JSON_V2;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.SERIALIZED_UPDATED_SUBSYSTEM_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.HashMap;
import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ericsson.bos.so.subsystemsmanager.business.api.ConnectionPropsService;
import com.ericsson.bos.so.subsystemsmanager.business.api.GenericSerializer;
import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemUserService;
import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemsService;
import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemsServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemsServiceV2;
import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.DataIntegrityViolationException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.NameMustBeUniqueException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemPartialDeleteException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemUserDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.controlleradvice.DaoControllerAdvice;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterService;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v1.SubsystemFilterServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v2.SubsystemFilterServiceV2;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.ConnectionPropertiesController;
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.SubsystemController;
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.SubsystemUserController;
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.SubsystemControllerV2;
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.SubsystemUserControllerV2;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * The Class MockMvcRequests.
 */
public class MockMvcRequests {

    @InjectMocks
    public static ConnectionPropertiesController connectionPropertiesController;

    @Autowired
    public static MockMvc mockMvc;

    @InjectMocks
    public static SubsystemController subsystemController;

    @InjectMocks
    public static SubsystemControllerV2 subsystemControllerV2;

    @InjectMocks
    public static SubsystemUserController subsystemUserController;

    @InjectMocks
    public static SubsystemUserControllerV2 subsystemUserControllerV2;

    @Mock
    protected static ConnectionPropsService connectionPropsService;

    @Mock
    protected static SubsystemsServiceV1 subsystemsServiceV1;

    @Mock
    protected static SubsystemFilterService subsystemJsonFilterService;

    @Mock
    protected static GenericSerializer genericSerializer;

    @InjectMocks
    protected static DaoControllerAdvice daoControllerAdvice;

    @Mock
    protected static SubsystemUserService subsystemUserService;

    @Mock
    protected static SubsystemsServiceV2 subsystemsServiceV2;

    @Mock
    protected static SubsystemFilterServiceV1 subsystemJsonFilterServiceV1;

    @Mock
    protected static SubsystemFilterServiceV2 subsystemJsonFilterServiceV2;

    @Mock
    protected static SubsystemsService subsystemsService;

    @Mock
    protected static LoggerHandler loggerHandler;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Actual post request on connection properties.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnConnectionProperties() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_CONN_PROPS_JSON);
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getConnectionPropertiesDetail());
        when(connectionPropsService.postConnProp(any(), any()))
                .thenReturn(ExpectedResponse.getConnectionPropertiesDetail());

        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual get request on connection properties.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnConnectionProperties() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_CONN_PROPS_LIST_JSON);
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getServiceResponseDataForGet());

        when(connectionPropsService.getConnPropsBySubsystemId(any(String.class)))
                .thenReturn(ExpectedResponse.getServiceResponseDataForGet());

        return mockMvc.perform(get(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual get request on connection properties with all ids.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnConnectionPropertiesWithAllIds() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_CONN_PROPS_JSON);
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getServiceResponseDataForGetWithAllIds());

        when(connectionPropsService.getConnPropsById(any(String.class), any(String.class)))
                .thenReturn(ExpectedResponse.getServiceResponseDataForGetWithAllIds());

        return mockMvc.perform(get(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson));
    }

    /**
     * Actual put request on connection properties with all ids and map.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnConnectionPropertiesWithAllIdsAndMap() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_CONN_PROPS_JSON);
        updateRequest.put(Constants.$_NAME, Constants.$_NAME);
        final ObjectMapper mapper = new ObjectMapper();

        final String updateJson = mapper.writeValueAsString(updateRequest);

        when(connectionPropsService.putConnProps(any(String.class), any(String.class), any(ConnectionProperties.class)))
                .thenReturn(ExpectedResponse.getServiceResponseDataForPutWithAllIdsAndMap());

        return mockMvc.perform(put(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual patch request on connection properties with all ids and map.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPatchRequestOnConnectionPropertiesWithAllIdsAndMap() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_CONN_PROPS_JSON);
        updateRequest.put(Constants.$_NAME, Constants.$_NAME);
        final ObjectMapper mapper = new ObjectMapper();

        final String updateJson = mapper.writeValueAsString(updateRequest);

        when(connectionPropsService.patchConnProps(any(String.class), any(String.class),
                (Map<String, Object>) any(Map.class)))
                        .thenReturn(ExpectedResponse.getServiceResponseDataForPutWithAllIdsAndMap());

        return mockMvc.perform(patch(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual delete request on connection properties using id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnConnectionPropertiesUsingId() throws Exception {
        return mockMvc.perform(delete(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON));

    }

    /**
     * Actual post request on connection properties throws connection properties does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException()
            throws Exception {

        when(connectionPropsService.postConnProp(any(), any()))
                .thenThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA));
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getConnectionPropertiesDetail());
        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual post request on connection properties throws malformed exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnConnectionPropertiesThrowsMalformedException() throws Exception {
        when(connectionPropsService.postConnProp(any(), any()))
                .thenThrow(new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA));
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getConnectionPropertiesDetail());
        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual get request on connection properties throws subsystem does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException()
            throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getServiceResponseDataForGet());
        when(connectionPropsService.getConnPropsBySubsystemId(any(String.class)))
                .thenThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA));

        return mockMvc.perform(get(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual get request on connection properties throws connection properties does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException()
            throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getServiceResponseDataForGet());

        when(connectionPropsService.getConnPropsById(any(), any()))
                .thenThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA));

        return mockMvc.perform(get(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual put request on connection properties throws subsystem does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();

        updateRequest.put(Constants.$_NAME, Constants.$_NAME);
        final ObjectMapper mapper = new ObjectMapper();
        final String updateJson = mapper.writeValueAsString(updateRequest);

        when(connectionPropsService.putConnProps(any(String.class), any(String.class), any(ConnectionProperties.class)))
                .thenThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA));

        return mockMvc.perform(put(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual put request on connection properties throws connection properties does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();

        updateRequest.put(Constants.$_NAME, Constants.$_NAME);
        final ObjectMapper mapper = new ObjectMapper();
        final String updateJson = mapper.writeValueAsString(updateRequest);

        when(connectionPropsService.putConnProps(any(String.class), any(String.class),
                any(ConnectionProperties.class))).thenThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA));

        return mockMvc.perform(put(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual patch request on connection properties throws subsystem does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPatchRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();

        updateRequest.put(Constants.$_NAME, Constants.$_NAME);
        final ObjectMapper mapper = new ObjectMapper();
        final String updateJson = mapper.writeValueAsString(updateRequest);

        when(connectionPropsService.patchConnProps(any(String.class), any(String.class),
                (Map<String, Object>) any(Map.class)))
                        .thenThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA));
        return mockMvc.perform(patch(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual patch request on connection properties throws connection properties does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPatchRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();

        updateRequest.put(Constants.$_NAME, Constants.$_NAME);
        final ObjectMapper mapper = new ObjectMapper();
        final String updateJson = mapper.writeValueAsString(updateRequest);

        when(connectionPropsService.patchConnProps(any(String.class), any(String.class),
                (Map<String, Object>) any(Map.class)))
                        .thenThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA));
        return mockMvc.perform(patch(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual delete request on connection properties throws subsystem does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnConnectionPropertiesThrowsSubsystemDoesNotExistException() throws Exception {

        doThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA)).when(connectionPropsService).deleteConnProps((any()), (any()));
        return mockMvc.perform(delete(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON));

    }

    /**
     * Actual delete request on connection properties throws connection properties does not exist exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnConnectionPropertiesThrowsConnectionPropertiesDoesNotExistException() throws Exception {
        doThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_MESSAGE)).when(connectionPropsService).deleteConnProps((any()),
                (any()));
        return mockMvc.perform(delete(ExpectedResponse.getServiceUrl() + Constants.$_SLASH + Constants.SUBSYSTEMS + Constants.$_SLASH
                + Constants.$_SUBSYSTEM_ID + Constants.$_SLASH + Constants.CONNECTION_PROPERTIES + Constants.$_SLASH
                + Constants.CONNECTION_PROPERTIES_ID).contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Actual greeting message.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGreetingMessage() throws Exception {
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrl()));
    }

    /**
     * Actual get request on subsystem.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystem() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_LIST_JSON);
        when(subsystemsServiceV1.fetchSubsystemByQuery(any(), any())).thenReturn(ExpectedResponse.getAllSubsystems(false));
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL).contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Actual post request on subsystem.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystem() throws Exception {
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getSubsystem());
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_JSON);
        when(subsystemsServiceV1.postSubsystem(any())).thenReturn(ExpectedResponse.getSubsystem());
        return mockMvc.perform(post(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual get request on subsystem by id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemById() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_JSON);
        when(subsystemsServiceV1.getSubsystemById(Constants.SUBSYSTEM_ID, Constants.TENANT_NAME)).thenReturn(ExpectedResponse.getSubsystem());
        return mockMvc.perform(
                get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID));
    }

    /**
     * Actual delete request on subsystems by id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemsById() throws Exception {
        when(subsystemsServiceV1.deleteSubsystemById(any())).thenReturn(true);
        return mockMvc.perform(
                delete(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID));
    }

    /**
     * Actual put request on subsystem by id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnSubsystemById() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_UPDATED_SUBSYSTEM_JSON);
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        updateRequest.put(Constants.$_NAME, Constants.SUBSYSTEM_UPDATE_NAME);
        final String updateJson = mapper.writeValueAsString(updateRequest);
        when(subsystemsServiceV1.patchSubsystem(Constants.SUBSYSTEM_ID, updateRequest))
                .thenReturn(ExpectedResponse.getUpdatedSubsystem());
        return mockMvc.perform(
                put(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID)
                        .header("content-type", "text/html; charset=utf-8")
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual post request on subsystems throws malformed content.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystemsThrowsMalformedContent() throws Exception {
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getSubsystem());
        when(subsystemsServiceV1.postSubsystem(any()))
                .thenThrow(new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA));
        return mockMvc.perform(post(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual post request on subsystems throws malformed content with wrong error code.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystemsThrowsMalformedContentWithWrongErrorCode() throws Exception {
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getSubsystem());
        when(subsystemsServiceV1.postSubsystem(any()))
                .thenThrow(new MalformedContentException(Constants.WRONG_ERROR_CODE_GENERIC, Constants.ERROR_DATA));
        return mockMvc.perform(post(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual post request on subsystem user.
     *
     * @param subsytemUrl
     *            the subsytem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystemUser(String subsytemUrl) throws Exception {
        final String request = mapper.writeValueAsString(ExpectedResponse.getSubsystemUser());
        when(subsystemUserService.postUserByConnsPropId(any(), any()))
                .thenReturn(ExpectedResponse.getSubsystemUser());
        return mockMvc.perform(post(subsytemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL).contentType(MediaType.APPLICATION_JSON).content(request));
    }

    /**
     * Actual delete request O subsystem user.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOSubsystemUser(String subsystemUrl) throws Exception {
        return mockMvc.perform(delete(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID).contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Actual get request on subsystem by id throws malformed content.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByIdThrowsMalformedContent() throws Exception {
        when(subsystemsServiceV1.getSubsystemById(Constants.SUBSYSTEM_ID, Constants.TENANT_NAME))
                .thenThrow(new MalformedContentException(Constants.MALFORMED_CONTENT_DEVELOPER_MSG, Constants.ERROR_MESSAGE));
        return mockMvc.perform(
                get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID));
    }

    /**
     * Actual delete request on subsystem by id throws malformed content.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemByIdThrowsMalformedContent() throws Exception {
        doThrow(new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA)).when(subsystemsServiceV1)
                .deleteSubsystemById(Constants.SUBSYSTEM_ID);
        return mockMvc.perform(
                delete(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID));
    }

    /**
     * Actual put request on subsystem by id throws malformed content.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnSubsystemByIdThrowsMalformedContent() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        updateRequest.put(Constants.$_NAME, Constants.SUBSYSTEM_UPDATE_NAME);
        final String updateJson = mapper.writeValueAsString(updateRequest);
        when(subsystemsServiceV1.patchSubsystem(Constants.SUBSYSTEM_ID, updateRequest))
                .thenThrow(new MalformedContentException(Constants.MALFORMED_CONTENT_ERROR_CODE, Constants.ERROR_DATA));
        return mockMvc.perform(
                put(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual get request on subsystem by id throws subsystem does not exist.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByIdThrowsSubsystemDoesNotExist() throws Exception {
        when(subsystemsServiceV1.getSubsystemById(Constants.SUBSYSTEM_ID, Constants.TENANT_NAME))
                .thenThrow(new SubsystemDoesNotExistException(Constants.SUBSYSTEM_NOT_EXIST_DEVELOPER_MSG));
        return mockMvc.perform(
                get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID));
    }

    /**
     * Actual put request on subsystem by id throws subsystem does not exist.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnSubsystemByIdThrowsSubsystemDoesNotExist() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        updateRequest.put(Constants.$_NAME, Constants.SUBSYSTEM_UPDATE_NAME);
        final String updateJson = mapper.writeValueAsString(updateRequest);
        when(subsystemsServiceV1.patchSubsystem(Constants.SUBSYSTEM_ID, updateRequest))
                .thenThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA));
        return mockMvc.perform(
                put(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual delete request on subsystem by id throws subsystem does not exist.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemByIdThrowsSubsystemDoesNotExist() throws Exception {
        doThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA)).when(subsystemsServiceV1).deleteSubsystemById(Constants.SUBSYSTEM_ID);
        return mockMvc.perform(
                delete(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID));
    }

    /**
     * Actual post request on subsystem user throws malformed content.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystemUserThrowsMalformedContent(String subsystemUrl) throws Exception {
        final String request = mapper.writeValueAsString(ExpectedResponse.getSubsystemUser());
        when(subsystemUserService.postUserByConnsPropId(any(), any()))
                .thenThrow(new MalformedContentException("SSM-B-25", Constants.ERROR_DATA));
        return mockMvc.perform(post(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL).contentType(MediaType.APPLICATION_JSON).content(request));
    }

    /**
     * Actual post request on subsystem user throws subsystem does not exist.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystemUserThrowsSubsystemDoesNotExist(String subsystemUrl) throws Exception {
        final String request = mapper.writeValueAsString(ExpectedResponse.getSubsystemUser());
        when(subsystemUserService.postUserByConnsPropId(any(), any()))
                .thenThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA));
        return mockMvc.perform(post(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL).contentType(MediaType.APPLICATION_JSON).content(request));
    }

    /**
     * Actual post request on subsystem user throw conn prop not exist.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPostRequestOnSubsystemUserThrowConnPropNotExist(String subsystemUrl) throws Exception {
        final String request = mapper.writeValueAsString(ExpectedResponse.getSubsystemUser());
        when(subsystemUserService.postUserByConnsPropId(any(), any()))
                .thenThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA));
        return mockMvc.perform(post(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL).contentType(MediaType.APPLICATION_JSON).content(request));
    }

    /**
     * Actual delete request on subsystem user throws malformed content.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemUserThrowsMalformedContent(String subsystemUrl) throws Exception {
        doThrow(new MalformedContentException("SSM-B-25", Constants.ERROR_DATA))
                .when(subsystemUserService).deleteSubsystemUserById(any(), any(), any());
        return mockMvc.perform(delete(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID).contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Actual delete request on subsystem user throws subsystem does not exist.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemUserThrowsSubsystemDoesNotExist(String subsystemUrl) throws Exception {
        doThrow(new SubsystemDoesNotExistException(Constants.ERROR_DATA))
                .when(subsystemUserService).deleteSubsystemUserById(any(), any(), any());
        return mockMvc.perform(delete(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID).contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Actual delete request on subsystem user throws subsystem user does not exist.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemUserThrowsSubsystemUserDoesNotExist(String subsystemUrl) throws Exception {
        doThrow(new SubsystemUserDoesNotExistException(Constants.ERROR_DATA))
                .when(subsystemUserService).deleteSubsystemUserById(any(), any(), any());
        return mockMvc.perform(delete(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID).contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Actual delete request on subsystem user throws conn prop does not exist.
     *
     * @param subsystemUrl
     *            the subsystem url
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnSubsystemUserThrowsConnPropDoesNotExist(String subsystemUrl) throws Exception {
        doThrow(new ConnectionPropertiesDoesNotExistException(Constants.ERROR_DATA))
                .when(subsystemUserService).deleteSubsystemUserById(any(), any(), any());
        return mockMvc.perform(delete(subsystemUrl + Constants.SUBSYSTEMS_URL + Constants.$_SLASH
                + Constants.SUBSYSTEM_ID + Constants.CONN_PROP_URL + Constants.$_SLASH + Constants.SUBSYSTEM_USER_CONNPROP_ID
                + Constants.SUBSYSTEM_USER_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID).contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Actual put request on subsystem by id throws subsystem name must be unique.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnSubsystemByIdThrowsSubsystemNameMustBeUnique() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        updateRequest.put(Constants.$_NAME, Constants.SUBSYSTEM_DUPLICATE_NAME);
        final String updateJson = mapper.writeValueAsString(updateRequest);
        when(subsystemsServiceV1.patchSubsystem(Constants.SUBSYSTEM_ID, updateRequest))
                .thenThrow(new NameMustBeUniqueException(Constants.ERROR_DATA));
        return mockMvc.perform(
                put(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual put request on subsystem by id throws data access exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPutRequestOnSubsystemByIdThrowsDataAccessException() throws Exception {
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        updateRequest.put(Constants.$_NAME, Constants.SUBSYSTEM_DUPLICATE_NAME);
        final String updateJson = mapper.writeValueAsString(updateRequest);
        final DataIntegrityViolationException ex = new DataIntegrityViolationException();
        when(subsystemsServiceV1.patchSubsystem(Constants.SUBSYSTEM_ID, updateRequest))
                .thenThrow(ex);
        return mockMvc.perform(
                put(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual get request on full paginated subsystem.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnFullPaginatedSubsystem() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_LIST_JSON);
        when(subsystemsServiceV1.fetchSubsystemByQuery(any(), any())).thenReturn(ExpectedResponse.getPaginatedSubsystemsList(false));
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEM_PAGINATION).header(
                Constants.TOTAL, String.valueOf(ExpectedResponse.getPaginatedSubsystemsList(false).size())));
    }

    /**
     * Actual get request on subsystem by filtered json.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByFilteredJson() throws Exception {
        when(subsystemJsonFilterServiceV1.filterResponseFields(Constants.FILTERED_ID, Constants.TENANT_NAME))
                .thenReturn(ExpectedResponse.getFilteredId());
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.SELECT_URL + Constants.FILTERED_ID));
    }

    /**
     * Actual get request on subsystem by two filtered fields.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByTwoFilteredFields() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_FILTERED_SUBSYSTEM);
        when(subsystemJsonFilterServiceV1.filterResponseFields
                (Constants.FILTERED_ID +Constants.$_COMMA + Constants.FILTERED_NAME, Constants.TENANT_NAME))
                .thenReturn(
                        ExpectedResponse.getFilteredFields());
        return mockMvc.perform(get(
                ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.SELECT_URL + Constants.FILTERED_ID + ","
                        + Constants.FILTERED_NAME).contentType(
                                Constants.APPLICATION_JSON));
    }

    /**
     * Actual get request on subsystem by filtered json with not exist field.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByFilteredJsonWithNotExistField() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn("[]");
        when(subsystemJsonFilterServiceV1.filterResponseFields(Constants.FILTERED_NOT_EXIST, Constants.TENANT_NAME))
                .thenReturn(ExpectedResponse.getFilteredResponseByNotExistField());
        return mockMvc.perform(
                get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.SELECT_URL + Constants.FILTERED_NOT_EXIST).contentType(
                        Constants.APPLICATION_JSON));
    }

    /**
     * Actual get request on subsystem by one filtered fields.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByOneFilteredFields() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn("[1,2]");
        when(subsystemJsonFilterServiceV1.filterResponseSingleField(Constants.FILTERED_ID, Constants.TENANT_NAME))
                .thenReturn(ExpectedResponse.getFilteredId());
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.SELECT_URL + Constants.FILTERED_ID));
    }

    /**
     * Actual delete request on multiple subsystems by id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnMultipleSubsystemsById() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getIds());
        when(subsystemsService.deleteSubsystemById(Constants.SUBSYSTEM_ID))
                .thenThrow(new SubsystemPartialDeleteException(Constants.ERROR_DATA));
        return mockMvc.perform(delete(ExpectedResponse.getSubsystemUrl()
                + Constants.SUBSYSTEMS_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Base error message factory exception.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions baseErrorMessageFactoryException() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getIds());
        when(subsystemsServiceV1.deleteSubsystemById(Constants.SUBSYSTEM_ID))
                .thenThrow(new SubsystemPartialDeleteException(Constants.ERROR_DATA));
        return mockMvc.perform(delete(ExpectedResponse.getSubsystemUrl()
                + Constants.SUBSYSTEMS_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual delete request on orig multiple subsystems by id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualDeleteRequestOnOrigMultipleSubsystemsById() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getIds());
        when(subsystemsService.deleteSubsystemById(any())).thenReturn(true);
        return mockMvc.perform(
                delete(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson));
    }

    /**
     * Actual get request on full paginated subsystem with conn prop filter.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnFullPaginatedSubsystemWithConnPropFilter() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_LIST_JSON);
        when(subsystemsServiceV1.getAllSubsystemsPagination(Constants.PAGE_OFFSET, Constants.PAGE_LIMIT, Constants.SORT_ATTR, Constants.SORT_DIR,
                Constants.FILTER_CONNPROP, Constants.TENANT_NAME)).thenReturn(
                        ExpectedResponse.getPaginatedSubsystems(false));
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEM_PAGINATION + Constants.PAGINATION_DETAILS_URL).header(
                Constants.TOTAL, String.valueOf(ExpectedResponse.getPaginatedSubsystems(false).getTotal())));
    }

    /**
     * Actual get request on subsystem by id and filter field.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByIdAndFilterField() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getFields());
        when(subsystemJsonFilterServiceV1.filterResponseSingleFieldFromKnownSubsystem(any(), any(), any())).thenReturn(ExpectedResponse.getFields());
        return mockMvc.perform(
                get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID + Constants.SELECT_URL
                        + Constants.SUBSYSTEM_NAME).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson));
    }

    /**
     * Actual get request on subsystem by id and filter fields.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByIdAndFilterFields() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String requestJson = mapper.writeValueAsString(ExpectedResponse.getMultipleFields());
        when(subsystemJsonFilterServiceV1.filterResponseFields(any(), any(), any())).thenReturn(ExpectedResponse.getMultipleFields());
        return mockMvc.perform(
                get(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID + Constants.SELECT_URL
                        + Constants.FIELDS).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson));
    }

    /**
     * Actual patch request on subsystem by id.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualPatchRequestOnSubsystemById() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_UPDATED_SUBSYSTEM_JSON);
        final Map<String, Object> updateRequest = new HashMap<String, Object>();
        updateRequest.put(Constants.$_NAME, Constants.SUBSYSTEM_UPDATE_NAME);
        final String updateJson = mapper.writeValueAsString(updateRequest);
        when(subsystemsServiceV1.patchSubsystem(Constants.SUBSYSTEM_ID, updateRequest))
                .thenReturn(ExpectedResponse.getUpdatedSubsystem());
        return mockMvc.perform(
                patch(ExpectedResponse.getSubsystemUrl() + Constants.SUBSYSTEMS_URL + Constants.$_SLASH + Constants.SUBSYSTEM_ID)
                        .header("content-type", "text/html; charset=utf-8")
                        .contentType(MediaType.APPLICATION_JSON).content(updateJson));
    }

    /**
     * Actual get request on subsystem V2.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemV2() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_LIST_JSON_V2);
        when(subsystemsServiceV2.fetchSubsystemByQuery(any(), any()))
                .thenReturn(ExpectedResponse.getAllSubsystems(true));
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrlV2() + Constants.SUBSYSTEMS_URL)
                .contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Actual get request on full paginated subsystem V2.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnFullPaginatedSubsystemV2() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_SUBSYSTEM_LIST_JSON_V2);
        when(subsystemsServiceV2.fetchSubsystemByQuery(any(), any()))
                .thenReturn(ExpectedResponse.getPaginatedSubsystemsList(true));
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrlV2() + Constants.SUBSYSTEM_PAGINATION)
                .header(Constants.TOTAL, String.valueOf(ExpectedResponse.getPaginatedSubsystemsList(true).size())));
    }

    /**
     * Actual get request on subsystem by two filtered fields V2.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByTwoFilteredFieldsV2() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn(SERIALIZED_FILTERED_SUBSYSTEM);
        when(subsystemJsonFilterServiceV2.filterResponseFields(Constants.FILTERED_ID + "," + Constants.FILTERED_NAME,
                Constants.TENANT_NAME)).thenReturn(ExpectedResponse.getFilteredFields());
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrlV2() + Constants.SUBSYSTEMS_URL
                + Constants.SELECT_URL + Constants.FILTERED_ID + "," + Constants.FILTERED_NAME)
                        .contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Actual get request on subsystem by filtered json with not exist field V2.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByFilteredJsonWithNotExistFieldV2() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn("[]");
        when(subsystemJsonFilterServiceV2.filterResponseFields(Constants.FILTERED_NOT_EXIST, Constants.TENANT_NAME))
                .thenReturn(ExpectedResponse.getFilteredResponseByNotExistField());
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrlV2() + Constants.SUBSYSTEMS_URL
                + Constants.SELECT_URL + Constants.FILTERED_NOT_EXIST).contentType(Constants.APPLICATION_JSON));
    }

    /**
     * Actual get request on subsystem by one filtered fields V2.
     *
     * @return the result actions
     * @throws Exception
     *             the exception
     */
    public static ResultActions actualGetRequestOnSubsystemByOneFilteredFieldsV2() throws Exception {
        when(genericSerializer.serializeObjectToExpectedJson(any())).thenReturn("[1,2]");
        when(subsystemJsonFilterServiceV2.filterResponseSingleField(Constants.FILTERED_ID, Constants.TENANT_NAME))
                .thenReturn(ExpectedResponse.getFilteredId());
        return mockMvc.perform(get(ExpectedResponse.getSubsystemUrlV2() + Constants.SUBSYSTEMS_URL
                + Constants.SELECT_URL + Constants.FILTERED_ID));
    }
}
