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
package com.ericsson.bos.so.subsystemsmanager.test.unit.connectivity;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.scheduler.ConnectivityCheck;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.ericsson.oss.orchestration.so.common.adapters.spi.model.v1.AdapterCheckConnectivityRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.EXECUTE_CONNECTIVITY_CHECK;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.AUTH_HEADERS_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.AUTH_TYPE_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.AUTH_URL_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.CLIENT_ID_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.CLIENT_SECRET_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.GRANT_TYPE_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.NAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.PW_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.TENANT_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.USERNAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_DO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.SUBSYSTEM_TYPE_NFVO;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_PROPERTY_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest.TEST_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.AUTH_HEADERS;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.AUTH_URL;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.CLIENT_ID;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.CLIENT_SECRET;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.GRANT_TYPE;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.OUTH_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/***
 * Test class for ConnectivityCheck to adapters
 */
@TestPropertySource(properties = { "ecmSol005Adapter.url:http://localhost:9000",
    "domainOrchAdapter.url:http://localhost:9000",
})
public class ConnectivityCheckTest extends BaseIntegrationTest {

    @Autowired
    private ConnectivityCheck connectivityCheckScheduler;

    @MockBean
    private KmsServiceImpl kmsService;

    private MockWebServer mockWebServer;

    /**
     * Test setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void testSetup() throws IOException {

        when(kmsService.decryptProperty(any())).thenAnswer(i -> i.getArguments()[0]);
        final Map<String, String> skippedConnectivityProperties = new HashMap<>();

        skippedConnectivityProperties.put(EXECUTE_CONNECTIVITY_CHECK, "false");
        skippedConnectivityProperties.put(PW_PROPERTY, TEST_USERNAME);
        skippedConnectivityProperties.put(USERNAME_PROPERTY, USERNAME_PROPERTY);
        skippedConnectivityProperties.put(NAME_PROPERTY, TEST_PROPERTY_NAME);
        skippedConnectivityProperties.put(TENANT_PROPERTY, TEST_TENANT_NAME);

        final List<ConnectionProperties> connectionProperties = Arrays.asList(
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(TEST_USERNAME, DEFAULT_PASSWORD, TEST_PROPERTY_NAME, TEST_TENANT_NAME)),
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(skippedConnectivityProperties))
                );

        final List<ConnectionProperties> connectionProperties2 = List.of(
                ConnectionPropertiesFactory.buildConnectionProperties(PropertyFactory.buildDefaultProperties()),
                ConnectionPropertiesFactory.buildConnectionProperties(
                        PropertyFactory.buildProperties(AUTH_URL, AUTH_HEADERS,
                                OUTH_2, CLIENT_ID, CLIENT_SECRET, GRANT_TYPE)));

        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, SUBSYSTEM_TYPE_NFVO, connectionProperties);
        subsystemFactory.persistSubsystem(SECONDARY_SUBSYSTEM, SUBSYSTEM_TYPE_DO, connectionProperties2);

        mockWebServer = new MockWebServer();
        mockWebServer.start(9000);

    }

    /**
     * Test tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void testTearDown() throws Exception {
        mockWebServer.close();
    }

    /**
     * Test for the request mapping to be sent to the adapters
     * for connectivity check
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void test_adapter_connectivity_request_mapping() throws Exception {
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrlV2() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        final List<Subsystem> subsystems = extractFromResponse(response, new TypeReference<>() {});

        final List<AdapterCheckConnectivityRequest> adapterCheckConnectivityRequests = connectivityCheckScheduler
                .constructConnectivityRequest(subsystems.get(0));

        assertThat(adapterCheckConnectivityRequests.size()).isEqualTo(1);
        assertThat(adapterCheckConnectivityRequests.get(0).getSubsystemName()).isEqualTo(PRIMARY_SUBSYSTEM);
        assertThat(adapterCheckConnectivityRequests.get(0).getConnectionProperties().get(USERNAME_PROPERTY)).isEqualTo(TEST_USERNAME);
        assertThat(adapterCheckConnectivityRequests.get(0).getConnectionProperties().get(PW_PROPERTY)).isEqualTo(DEFAULT_PASSWORD);
        assertThat(adapterCheckConnectivityRequests.get(0).getConnectionProperties().get(NAME_PROPERTY)).isEqualTo(TEST_PROPERTY_NAME);
        assertThat(adapterCheckConnectivityRequests.get(0).getConnectionProperties().get("url")).isEqualTo("/subsystem-1");

        final List<AdapterCheckConnectivityRequest> adapterCheckConnectivityRequests1 = connectivityCheckScheduler
                .constructConnectivityRequest(subsystems.get(1));

        assertThat(adapterCheckConnectivityRequests1.size()).isEqualTo(2);
        assertThat(adapterCheckConnectivityRequests1.get(0).getSubsystemName()).isEqualTo(SECONDARY_SUBSYSTEM);
        assertThat(adapterCheckConnectivityRequests1.get(0).getConnectionProperties().get(USERNAME_PROPERTY)).isEqualTo(DEFAULT_USERNAME);
        assertThat(adapterCheckConnectivityRequests1.get(0).getConnectionProperties().get(PW_PROPERTY)).isEqualTo(DEFAULT_PASSWORD);
        assertThat(adapterCheckConnectivityRequests1.get(0).getConnectionProperties().get(NAME_PROPERTY)).isEqualTo(DEFAULT_NAME);
        assertThat(adapterCheckConnectivityRequests1.get(0).getConnectionProperties().get("url")).isEqualTo("/subsystem-2");

        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get(AUTH_URL_PROPERTY)).isEqualTo(AUTH_URL);
        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get(AUTH_HEADERS_PROPERTY)).isEqualTo(AUTH_HEADERS);
        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get(AUTH_TYPE_PROPERTY)).isEqualTo(OUTH_2);
        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get(CLIENT_ID_PROPERTY)).isEqualTo(CLIENT_ID);
        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get(CLIENT_SECRET_PROPERTY)).isEqualTo(CLIENT_SECRET);
        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get(GRANT_TYPE_PROPERTY)).isEqualTo(GRANT_TYPE);
        assertThat(adapterCheckConnectivityRequests1.get(1).getConnectionProperties().get("url")).isEqualTo("/subsystem-2");

    }

    /***
     * Test for scheduler method to check for request sent to adapters
     *
     * @throws Exception the exception
     */
    @Test
    public void test_connectivity_scheduler() throws Exception {

        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));
        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));
        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));

        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        connectivityCheckScheduler.invokeConnectivityCheck();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/v1/check-connectivity");

        request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/v1/check-connectivity");

        request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/v1/check-connectivity");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);

    }

    /**
     * Test to ensure disabled connection property is not checked
     */
    @Test
    public void test_skipped_connection_property(){
        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));
        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));
        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));
        mockWebServer.enqueue(createMockResponse(HttpStatus.OK, ""));

        connectivityCheckScheduler.invokeConnectivityCheck();

        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

    /**
     * Test for retries when Adapter is unavailable
     */
    @Test
    public void test_connectivity_check_retry() {

        for(int i=0;i<9;i++) {
            mockWebServer.enqueue(createMockResponse(HttpStatus.INTERNAL_SERVER_ERROR, ""));
        }
        connectivityCheckScheduler.invokeConnectivityCheck();

        assertThat(mockWebServer.getRequestCount()).isEqualTo(9);
    }

}
