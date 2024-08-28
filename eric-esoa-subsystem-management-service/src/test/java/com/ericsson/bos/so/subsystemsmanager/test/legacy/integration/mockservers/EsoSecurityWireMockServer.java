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

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;


/**
 * The Class EsoSecurityWireMockServer.
 */
@Component
public class EsoSecurityWireMockServer extends MockServer {

    public static final int PORT = 9092;

    /**
     * Instantiates a new eso security wire mock server.
     *
     * @param testRestTemplate the test rest template
     */
    public EsoSecurityWireMockServer(TestRestTemplate testRestTemplate) {
        super(testRestTemplate, PORT);
        this.jsonResponsesDirectory = "wire-mock-json-responses/eso-security";
    }

}