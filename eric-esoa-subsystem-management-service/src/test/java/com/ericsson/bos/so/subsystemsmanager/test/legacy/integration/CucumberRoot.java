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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.EsoSecurityWireMockServer;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.MockServer;
import com.ericsson.bos.so.subsystemsmanager.web.SubsystemManagerApplication;


/**
 * The Class CucumberRoot.
 */
@SpringBootTest(classes = SubsystemManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("integration-test")
public abstract class CucumberRoot {

    @Autowired
    protected EsoSecurityWireMockServer esoSecurityWireMockServer;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    /**
     * Gets the template.
     *
     * @return the template
     */
    public TestRestTemplate getTemplate() {
        return template;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Gets the eso security server.
     *
     * @return the eso security server
     */
    public MockServer getEsoSecurityServer() {
        return this.esoSecurityWireMockServer;
    }

}
