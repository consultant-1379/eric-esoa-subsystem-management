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
package com.ericsson.bos.so.subsystemsmanager.test.integration;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.config.properties.PathProperties;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemTypeFactory;
import com.ericsson.bos.so.subsystemsmanager.web.SubsystemManagerApplication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import org.hamcrest.Matchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * The Class BaseIntegrationTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SubsystemManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("integration-test")
@Slf4j
@TestPropertySource(properties = { "spring.config.additional-location = src/test/resources/app/config/truststore.yaml" })
public abstract class BaseIntegrationTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse(
            "armdocker.rnd.ericsson.se/dockerhub-ericsson-remote/postgres:bullseye")
            .asCompatibleSubstituteFor("postgres"));


    static {
        postgreSQLContainer.start();
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    private static final String INTERNAL_ERROR_CODE_JSON_PROPERTY = "errorCode";
    private static final String DEVELOPER_MESSAGE_JSON_PROPERTY = "developerMessage";
    private static final String USER_MESSAGE_JSON_PROPERTY = "userMessage";
    private static final String ERROR_DATA_JSON_PROPERTY = "errorData";
    private static final String DOLLER_DOT="$.";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected PathProperties pathProperties;

    @Autowired
    protected SubsystemFactory subsystemFactory;
    @Autowired
    protected SubsystemTypeFactory subsystemTypeFactory;
    @Autowired
    protected ConnectionPropertiesFactory connectionPropertiesFactory;

    protected ResultActions response;

    /**
     * Check response body contains bodyElements.
     *
     * @param bodyElements the body elements
     * @throws Exception the exception
     */
    protected void checkResponseBodyContains(String... bodyElements) throws Exception {
        checkResponseBodyContains(Arrays.asList(bodyElements));
    }

    /**
     * Check response body contains bodyElements.
     *
     * @param bodyElements the body elements
     * @throws Exception the exception
     */
    protected void checkResponseBodyContains(List<String> bodyElements) throws Exception {
        for (String element : bodyElements) {
            response.andExpect(content().string(Matchers.containsStringIgnoringCase(element)));
        }
    }

    /**
     * Check response contains internal error code.
     *
     * @param internalErrorCode the internal error code
     * @throws Exception the exception
     */
    protected void checkResponseContainsInternalErrorCode(String internalErrorCode) throws Exception {
        response.andExpect(jsonPath(DOLLER_DOT + INTERNAL_ERROR_CODE_JSON_PROPERTY).value(internalErrorCode));
    }

    /**
     * Check response contains developer message.
     *
     * @param messages the messages
     * @throws Exception the exception
     */
    protected void checkResponseContainsDeveloperMessage(String... messages) throws Exception {
        for (String message : messages) {
            response.andExpect(jsonPath(DOLLER_DOT + DEVELOPER_MESSAGE_JSON_PROPERTY, Matchers.containsStringIgnoringCase(message)));
        }
    }

    /**
     * Check response contains user message.
     *
     * @param messages the messages
     * @throws Exception the exception
     */
    protected void checkResponseContainsUserMessage(String... messages) throws Exception {
        for (String message : messages) {
            response.andExpect(jsonPath(DOLLER_DOT + USER_MESSAGE_JSON_PROPERTY, Matchers.containsStringIgnoringCase(message)));
        }
    }

    /**
     * Check response contains error data.
     *
     * @param errorData the error data
     * @throws Exception the exception
     */
    protected void checkResponseContainsErrorData(String... errorData) throws Exception {
        for (int index = 0; index < errorData.length; index++) {
            response.andExpect(jsonPath(DOLLER_DOT + ERROR_DATA_JSON_PROPERTY + "[" + index + "]").value(errorData[index]));
        }
    }

    /**
     * Extract from response.
     *
     * @param <T> the generic type
     * @param response the response
     * @param typeReference the type reference
     * @return the t
     */
    protected <T> T extractFromResponse(final ResultActions response, final TypeReference<T> typeReference) {
        final MvcResult result = response.andReturn();
        T extractedObject = null;
        try {
            extractedObject = new ObjectMapper().readValue(result.getResponse().getContentAsString(), typeReference);
        } catch (Exception exception) {
            log.error("Encountered error parsing response: {}", exception.getMessage());
        }
        return extractedObject;
    }

    /**
     * Verify connection property contains expected value.
     *
     * @param subsystem the subsystem
     * @param encryptedPropertyIndex the encrypted property index
     * @param expectedPropertyValue the expected property value
     */
    protected void verifyConnectionPropertyContainsExpectedValue(final Subsystem subsystem, final int encryptedPropertyIndex,
            final String expectedPropertyValue) {
        Property property = new Property();
        try {
            // extract property at given index
            property = subsystem.getConnectionProperties().get(0).getProperties().get(encryptedPropertyIndex);
        } catch (Exception exception) {
            log.info("Encountered error attempting to extract property at index {}: {}", encryptedPropertyIndex, exception.getMessage());
        }
        // assert expected property value
        assertEquals(expectedPropertyValue, property.getValue());
    }

    /**
     * Creates the mock response.
     *
     * @param httpStatus the http status
     * @param body the body
     * @return the mock response
     */
    protected static MockResponse createMockResponse(final HttpStatus httpStatus, String body) {
        return new MockResponse()
                .setResponseCode(httpStatus.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
    }

}