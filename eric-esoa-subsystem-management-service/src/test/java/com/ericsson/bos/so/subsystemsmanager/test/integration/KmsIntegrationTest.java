/*******************************************************************************
 * COPYRIGHT Ericsson 2024
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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * The Class KmsIntegrationTest.
 */
public abstract class KmsIntegrationTest extends BaseIntegrationTest {

    protected static String vaultLoginResponse;
    protected static String vaultErrorResponse;
    protected static String vaultEncryptResponse;
    protected static String vaultPatchRequestEncryptResponse;
    protected static String vaultDecryptResponse;
    protected static String serviceUnavailable;
    protected static String badGateway;
    protected static String internalServerError;
    protected static boolean isLoggedIn;

    @Value("${spring.cloud.vault.port}")
    protected int kmsPort;

    protected MockWebServer kmsMockWebserver;

    /**
     * Setup kms mock responses.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void setupKmsMockResponses() throws IOException {
        vaultLoginResponse = Resources.toString(Resources.getResource("kms/success/ValidKmsLoginResponse.json"), Charsets.UTF_8);
        vaultErrorResponse = Resources.toString(Resources.getResource("kms/error/VaultErrorResponse.json"), Charsets.UTF_8);
        vaultEncryptResponse = Resources.toString(Resources.getResource("kms/success/ValidEncryptResponse.json"), Charsets.UTF_8);
        vaultDecryptResponse = Resources.toString(Resources.getResource("kms/success/ValidDecryptResponse.json"), Charsets.UTF_8);
        vaultPatchRequestEncryptResponse =  Resources.toString(Resources
                .getResource("kms/success/ValidPatchRequestEncryptResponse.json"), Charsets.UTF_8);
        serviceUnavailable =
                Resources.toString(
                        Resources.getResource("kms/error/ServiceUnavailableErrorResponse.json"), Charsets.UTF_8);
        badGateway =
                Resources.toString(
                        Resources.getResource("kms/error/BadGatewayErrorResponse.json"), Charsets.UTF_8);
        internalServerError =
                Resources.toString(
                        Resources.getResource("kms/error/InternalServerErrorResponse.json"), Charsets.UTF_8);
    }

    /**
     * Setup kms mock web server.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setupKmsMockWebServer() throws IOException {
        // setup KMS mock server
        kmsMockWebserver = new MockWebServer();
        kmsMockWebserver.start(kmsPort);
        if (!isLoggedIn) {
            kmsMockWebserver.enqueue(createMockResponse(HttpStatus.OK, vaultLoginResponse));
        }
    }

    /**
     * Close kms mock web server.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void closeKmsMockWebServer() throws IOException {
        // close KMS mock server
        kmsMockWebserver.close();
    }

    /**
     * Mock kms bad request response.
     */
    protected void mockKmsBadRequestResponse() {
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.BAD_REQUEST, vaultErrorResponse));
    }

}