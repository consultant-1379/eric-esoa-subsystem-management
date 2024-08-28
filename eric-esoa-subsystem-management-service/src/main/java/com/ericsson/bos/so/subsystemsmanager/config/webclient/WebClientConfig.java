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

package com.ericsson.bos.so.subsystemsmanager.config.webclient;

import com.ericsson.bos.so.security.mtls.client.webclient.TlsWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.TLS_WEBCLIENT;

/***
 * WebClient config class
 */
@Configuration
public class WebClientConfig {

    @Autowired
    private TlsWebClient tlsWebclient;

    /***
     * TLS Webclient config
     *
     * @return
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    @Bean
    @Qualifier(TLS_WEBCLIENT)
    public WebClient webClient() throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return tlsWebclient.getSslConfiguredWebClient()
                .build();
    }
}
