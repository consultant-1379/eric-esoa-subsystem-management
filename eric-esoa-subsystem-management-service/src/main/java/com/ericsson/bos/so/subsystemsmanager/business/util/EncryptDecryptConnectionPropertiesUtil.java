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
package com.ericsson.bos.so.subsystemsmanager.business.util;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.vault.VaultException;

import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.api.KmsService;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;

/**
 * The Class EncryptDecryptConnectionPropertiesUtil.
 */
@Component

/** The Constant log. */
@Slf4j
public class EncryptDecryptConnectionPropertiesUtil {

    public static final boolean ENCRYPT = true;

    public static final boolean DECRYPT = false;

    public static final String ENCRYPT_ALL_INDICATOR = "*";

    @Autowired
    private KmsService kmsService;

    /**
     * Encrypt or decrypt connection properties.
     *
     * @param connectionPropertyList
     *            the connection property list
     * @param isEncrypt
     *            the is encrypt
     * @return the list
     */
    public List<ConnectionProperties> encryptOrDecryptProperties(final List<ConnectionProperties> connectionPropertyList, final boolean isEncrypt) {
        connectionPropertyList.forEach(connectionProperty -> {
            if (!isEncrypt) {
                setEncryptedKeyListForConnectionProperty(connectionProperty);
            }
            if (!SubsystemRequestValidator.isEncryptedKeysEmpty(connectionProperty.getEncryptedKeys())) {
                if (isEncrypt) {
                    encryptProperties(connectionProperty);
                } else {
                    decryptProperties(connectionProperty);
                }
            }
        });
        return connectionPropertyList;
    }

    /**
     * Encrypt connection properties.
     *
     * @param connectionProperty
     *            the connection property
     * @return the connection properties
     */
    public ConnectionProperties encryptProperties(final ConnectionProperties connectionProperty) {
        if (!SubsystemRequestValidator.isEncryptedKeysEmpty(connectionProperty.getEncryptedKeys())) {
            if (isEncryptAll(connectionProperty)) {
                connectionProperty.getProperties().forEach(this::encryptProperty);
            } else {
                connectionProperty.getEncryptedKeys().forEach(encryptedKey -> connectionProperty.getProperties().forEach(property -> {
                    if (property.getKey().equalsIgnoreCase(encryptedKey)) {
                        encryptProperty(property);
                    }
                }));
            }
        }
        return connectionProperty;
    }

    /**
     * Encrypt property.
     *
     * @param property
     *            the property
     */
    private void encryptProperty(final Property property) {
        final String encryptedValue;
        try {
            encryptedValue = kmsService.encryptProperty(property.getValue());
        } catch (final VaultException vaultException) {
            log.error("Encountered error attempting to encrypt connection property due to: {}", vaultException.getMessage());
            throw new KmsServiceException(vaultException.getMessage());
        }
        property.setEncrypted(true);
        property.setValue(encryptedValue);
    }

    /**
     * Decrypt connection properties.
     *
     * @param connectionProperty
     *            the connection property
     * @return the connection properties
     */
    public ConnectionProperties decryptProperties(final ConnectionProperties connectionProperty) {
        connectionProperty.getProperties().forEach(property -> {
            if (property.isEncrypted()) {
                final String decryptedValue;
                try {
                    decryptedValue = kmsService.decryptProperty(property.getValue());
                } catch (final VaultException vaultException) {
                    log.error("Encountered error attempting to decrypt connection property due to: {}", vaultException.getMessage());
                    throw new KmsServiceException(vaultException.getMessage());
                }
                property.setValue(decryptedValue);
            }
        });
        return connectionProperty;
    }

    /**
     * Sets the encrypted key list for connection property.
     *
     * @param connectionProperty
     *            the new encrypted key list for connection property
     */
    private void setEncryptedKeyListForConnectionProperty(final ConnectionProperties connectionProperty) {
        final List<String> encryptedKeys = new ArrayList<>();
        connectionProperty.getProperties().forEach(property -> {
            if (property.isEncrypted()) {
                encryptedKeys.add(property.getKey());
            }
        });
        connectionProperty.setEncryptedKeys(encryptedKeys);
    }

    /**
     * Checks if all connection properties encrypted.
     *
     * @param connectionProperty
     *            the connection property
     * @return true, if is encrypt all
     */
    public boolean isEncryptAll(final ConnectionProperties connectionProperty) {
        return connectionProperty.getEncryptedKeys().get(0).equals(ENCRYPT_ALL_INDICATOR);
    }

}
