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
package com.ericsson.bos.so.subsystemsmanager.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ericsson.bos.so.subsystemsmanager.config.features.FeatureProperties;
import com.ericsson.bos.so.subsystemsmanager.config.properties.PathProperties;

/**
 * The Class SubsystemManagerApplication.
 */
@SpringBootApplication(scanBasePackages = { "com.ericsson.bos.so.security.mtls.*", "com.ericsson.bos.so.subsystemsmanager",
    "com.ericsson.bos.so.shared.spring.security"})
@EnableScheduling
@EnableRetry
@EnableConfigurationProperties({ PathProperties.class, FeatureProperties.class})
public class SubsystemManagerApplication {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(SubsystemManagerApplication.class, args);
    }
}
