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
package com.ericsson.bos.so.subsystemsmanager.business.scheduler;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemsServiceV2;
import com.ericsson.oss.orchestration.so.common.adapters.spi.model.v1.AdapterCheckConnectivityRequest;
import com.ericsson.oss.orchestration.so.common.adapters.spi.model.v1.AdapterCheckConnectivityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType.DOMAIN_ORCHESTRATOR;
import static com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType.NFVO;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.EXECUTE_CONNECTIVITY_CHECK;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.ID;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_ID;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.TLS_WEBCLIENT;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.URL;
import static java.util.stream.Collectors.toList;

/***
 * Scheduler class to invoke connectivity checks on adapters at regular intervals
 * with all connection properties
 *
 */
@Component
public class ConnectivityCheck implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectivityCheck.class);
    private static final String TENANT_NAME = "master";
    private static final String CONNECTIVITY_CHECK_URI = "/v1/check-connectivity";

    @Autowired
    @Qualifier(TLS_WEBCLIENT)
    private WebClient webClient;

    @Value("${ecmSol005Adapter.url}")
    private String sol005Url;

    @Value("${domainOrchAdapter.url}")
    private String domainOrchUrl;

    @Autowired
    private SubsystemsServiceV2 subsystemsServiceV2;

    @Autowired
    private RetryTemplate retryTemplate;

    /***
     * Scheduler to invoke connectivity checks on the adapters
     */
    public void invokeConnectivityCheck() {

        LOGGER.debug("Invoking connectivity check for NFVO and DO");
        final List<Subsystem> subsystems = subsystemsServiceV2.getAllSubsystems(TENANT_NAME);

        final URI sol005Uri = UriComponentsBuilder.fromHttpUrl(sol005Url)
                .path(CONNECTIVITY_CHECK_URI).build().toUri();

        final URI domainOrchUri = UriComponentsBuilder.fromHttpUrl(domainOrchUrl)
                .path(CONNECTIVITY_CHECK_URI).build().toUri();

        subsystems.stream()
                .filter(subsystem -> subsystem.getSubsystemType().getType().equals(NFVO.getType()))
                .flatMap(subsystem -> constructConnectivityRequest(subsystem).stream())
                .forEach(request -> invokeConnectivityCheck(sol005Uri, request));

        subsystems.stream()
                .filter(subsystem -> subsystem.getSubsystemType().getType().equals(DOMAIN_ORCHESTRATOR.getType()))
                .flatMap(subsystem -> constructConnectivityRequest(subsystem).stream())
                .forEach(request -> invokeConnectivityCheck(domainOrchUri, request));

    }

    /***
     * Construct adapter connectivity requests from subsystem
     *
     * @param subsystem
     * @return
     */
    public List<AdapterCheckConnectivityRequest> constructConnectivityRequest(final Subsystem subsystem) {

        return subsystem.getConnectionProperties()
                .stream()
                .filter(connection -> connectivityCheckEnabled(connection))
                .map(connection -> extractProperties(subsystem, connection))
                .collect(toList());

    }

    private boolean connectivityCheckEnabled(final ConnectionProperties connection) {
        return connection.getProperties().stream().allMatch(prop -> !prop.getKey().equals(EXECUTE_CONNECTIVITY_CHECK)) || connection.getProperties()
                .stream().anyMatch(property -> property.getKey().equals(EXECUTE_CONNECTIVITY_CHECK) && Boolean.parseBoolean(property.getValue()));
    }

    private AdapterCheckConnectivityRequest extractProperties(final Subsystem subsystem,
                                                              final ConnectionProperties connection) {
        final AdapterCheckConnectivityRequest adapterCheckConnectivityRequest = new AdapterCheckConnectivityRequest();
        final Map<String, String> connectionProperties = new HashMap<>();

        connectionProperties.put(URL, subsystem.getUrl());
        connectionProperties.put(SUBSYSTEM_ID, subsystem.getId().toString());
        connectionProperties.put(ID, connection.getId().toString());
        connection.getProperties().stream()
                .forEach(property -> connectionProperties.put(property.getKey(), property.getValue()));

        adapterCheckConnectivityRequest.setSubsystemName(subsystem.getName());
        adapterCheckConnectivityRequest.setConnectionProperties(connectionProperties);

        return adapterCheckConnectivityRequest;
    }

    private void invokeConnectivityCheck(final URI uri,
                                         final AdapterCheckConnectivityRequest adapterCheckConnectivityRequest) {
        try {
            retryTemplate.execute(retryContext -> sendRequest(uri, adapterCheckConnectivityRequest));
        } catch (Exception e) {
            LOGGER.error("Connectivity check failed for : {} with error : {}",
                    adapterCheckConnectivityRequest.getSubsystemName(), e.getMessage());
        }
    }

    private AdapterCheckConnectivityResponse sendRequest(final URI uri, final AdapterCheckConnectivityRequest adapterCheckConnectivityRequest) {
        return webClient.method(HttpMethod.POST)
                .uri(uri)
                .headers(header -> setHeaders(header))
                .bodyValue(adapterCheckConnectivityRequest)
                .retrieve()
                .bodyToMono(AdapterCheckConnectivityResponse.class)
                .block();
    }

    private static void setHeaders(HttpHeaders header) {
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Override
    public void run() {
        invokeConnectivityCheck();
    }

}
