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
package com.ericsson.bos.so.subsystemsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryTemplateConfig {

    /**
     * Create RetryTemplate config for connectivity check requests
     * @return
     */
    @Bean
    public RetryTemplate getRetryTemplate() {
        final RetryTemplate retryTemplate = new RetryTemplate();
        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000l);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
