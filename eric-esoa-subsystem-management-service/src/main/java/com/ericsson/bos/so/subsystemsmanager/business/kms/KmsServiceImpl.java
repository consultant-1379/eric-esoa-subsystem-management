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
package com.ericsson.bos.so.subsystemsmanager.business.kms;

import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.api.KmsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.ReactiveVaultTemplate;
import org.springframework.vault.core.ReactiveVaultTransitOperations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class KmsServiceImpl.
 */
@Service
@Retryable(
        retryFor = { VaultException.class },
        maxAttemptsExpression = "${adp-kms-client.retry-policy.max-attempts}",
        backoff = @Backoff(delayExpression = "${adp-kms-client.retry-policy.backoff-delay}"))
public class KmsServiceImpl implements KmsService {

    @Value("${adp-kms-client.encryption-key.name}")
    private String encryptionKey;

    @Autowired
    private ReactiveVaultTemplate vaultTemplate;

    private ReactiveVaultTransitOperations transitOperations;

    /**
     * Sets up the vault client.
     */
    @PostConstruct
    public void setUpVaultClient() {
        transitOperations = vaultTemplate.opsForTransit();
    }

    @Override
    public String encryptProperty(final String property) {
        try {
            return transitOperations.encrypt(encryptionKey, property).block();
        } catch (final VaultException vaultException) {
            handleVaultException(vaultException);
        }
        return "";
    }

    @Override
    public String decryptProperty(final String property) {
        try {
            return transitOperations.decrypt(encryptionKey, property).block();
        } catch (final VaultException vaultException) {
            handleVaultException(vaultException);
        }
        return "";
    }

    private void handleVaultException(final VaultException vaultException) {

        final HttpStatusCode statusCode = extractStatusCodeValue(vaultException.getMessage());
        if (statusCode.is5xxServerError()) {
            throw vaultException;
        }
        throw new KmsServiceException(vaultException.getMessage());
    }

    private HttpStatusCode extractStatusCodeValue(String message) {
        String httpStatusCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
        final Matcher m = Pattern.compile("\\d{3}").matcher(message);
        if (m.find()) {
            httpStatusCode = m.group();
        }
        return HttpStatusCode.valueOf(Integer.parseInt(httpStatusCode));
    }

}
