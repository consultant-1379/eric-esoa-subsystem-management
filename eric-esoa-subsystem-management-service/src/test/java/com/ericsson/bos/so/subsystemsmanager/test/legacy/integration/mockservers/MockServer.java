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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import jakarta.annotation.PreDestroy;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Class MockServer.
 */
@Slf4j
public abstract class MockServer {

    protected String jsonResponsesDirectory;

    protected WireMockServer wireMockServer;

    private final int serverPort;

    //Default value is false
    private boolean jsonResponsesSetUp;

    private final TestRestTemplate testRestTemplate;

    /**
     * Instantiates a new mock server.
     *
     * @param testRestTemplate
     *            the test rest template
     * @param serverPort
     *            the server port
     */
    protected MockServer(TestRestTemplate testRestTemplate, int serverPort) {
        this.testRestTemplate = testRestTemplate;
        this.serverPort = serverPort;
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(serverPort));
    }

    /**
     * Stop server.
     */
    @PreDestroy
    public void stopServer() {
        if (wireMockServer.isRunning()) {
            log.info("--> Stopping {}:{}...", this.getClass().getSimpleName(), wireMockServer.port());
            wireMockServer.stop();
        }
    }

    /**
     * Checks if server is running.
     *
     * @return true, if is server running
     */
    public boolean isServerRunning() {
        return wireMockServer.isRunning();
    }

    /**
     * Start server.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    public void startServer() throws IOException, ParseException {
        if (!wireMockServer.isRunning()) {
            log.info("--> Starting {}:{}...", this.getClass().getSimpleName(), serverPort);
            wireMockServer.start();
            if (!jsonResponsesSetUp) {
                setUpJsonResponses();
            }
        }
    }

    /**
     * Setup json responses.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    public void setUpJsonResponses() throws IOException, ParseException {
        if (jsonResponsesSetUp) {
            log.info("--> WireMock JSON responses have been set up already. Not going to set them up again.");
            return;
        }

        log.info("--> Setting up WireMock JSON responses..");

        final JSONParser jsonParser = new JSONParser();
        for (File jsonFile : getFilesFromDirectory()) {
            final Object jsonMapping = jsonParser.parse(new FileReader(jsonFile));
            final ResponseEntity response = testRestTemplate.postForEntity(getAdminUrl(), jsonMapping, null, jsonMapping);
            if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
                log.warn("--> Failed to load JSON responses from file: {}. Reason: {}.", jsonFile.getName(), response.getStatusCode());
            }

            log.info("--> Finished setting up WireMock JSON responses: {}", testRestTemplate.getForObject(getAdminUrl(), String.class));
        }

        jsonResponsesSetUp = true;
    }

    /**
     * Gets the admin url.
     *
     * @return the admin url
     */
    private String getAdminUrl() {
        return "http://localhost:" + wireMockServer.port() + "/__admin/mappings";
    }

    /**
     * Gets the files from directory.
     *
     * @return the files from directory
     */
    private File[] getFilesFromDirectory() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final String directoryPath = loader.getResource(jsonResponsesDirectory).getPath();

        return new File(directoryPath).listFiles();
    }

}
