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
package com.ericsson.bos.so.subsystemsmanager.business;

import static io.kubernetes.client.util.Config.ENV_SERVICE_PORT;
import static io.kubernetes.client.util.Config.SERVICEACCOUNT_CA_PATH;
import static io.kubernetes.client.util.Config.SERVICEACCOUNT_TOKEN_PATH;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.K8sApiException;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import lombok.extern.slf4j.Slf4j;

/**
 * The class AdapterLinksServiceImpl
 */
@Slf4j
@Service
public class AdapterLinksServiceImpl implements AdaptersLinksService {

    private static final String KUBERNETES_MASTER_HOST = "KUBERNETES_MASTER";

    @Value("${server.namespace}")
    private String namespace;

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    @Override
    public List<String> fetchAdapterLinksByType(final String subsystemType) {
        return fetchAdapterLinksByPodLabel(validateSubsystemType(subsystemType));
    }

    private String validateSubsystemType(final String subsystemType) {
        log.debug("validating subsystem type: '{}'...", subsystemType);
        return subsystemTypeDao.findByType(subsystemType).getType().toLowerCase();
    }

    /**
     * Get adapter links by K8's pod labels based on subsystem type.
     * @param subsystemType String
     * @return list of adapter links
     */
    private List<String> fetchAdapterLinksByPodLabel(final String subsystemType) {
        final List<String> adapterLinks = new ArrayList<>();
        try {
            final ApiClient client = getApiClient();
            Configuration.setDefaultApiClient(client);

            final CoreV1Api api = new CoreV1Api();
            final V1ServiceList list = api.listNamespacedService(
                    namespace,
                    null,
                    null,
                    null,
                    null,
                    subsystemType,
                    null,
                    null,
                    null,
                    null,
                    60,
                    false
            );
            for (V1Service item : list.getItems()) {
                adapterLinks.add(item.getMetadata().getName());
            }
        } catch (IOException e) {
            log.error("Error during attempt to connect K8s:", e);
            throw new K8sApiException();
        } catch (ApiException e) {
            log.error("Error during attempt to call K8s API:", e);
            throw new K8sApiException(subsystemType, namespace);
        }
        return adapterLinks;
    }

    /**
     * Allows to use correct kubernetes host to support ipv6 properly.
     * While we don't use any kube config for creation api client there is some problems with using
     * standard ways for creation api client.
     *
     * @return kubernetes api client
     *
     * @throws IOException on incorrect configuration
     */
    private ApiClient getApiClient() throws IOException {
        final ClientBuilder builder = new ClientBuilder();
        final String host = System.getenv(KUBERNETES_MASTER_HOST);
        final String port = System.getenv(ENV_SERVICE_PORT);
        builder.setBasePath("https://" + host + ":" + port);

        final String token =
                new String(
                        Files.readAllBytes(Paths.get(SERVICEACCOUNT_TOKEN_PATH)), Charset.defaultCharset());
        builder.setCertificateAuthority(Files.readAllBytes(Paths.get(SERVICEACCOUNT_CA_PATH)));
        builder.setAuthentication(new AccessTokenAuthentication(token));

        return builder.build();
    }

}
