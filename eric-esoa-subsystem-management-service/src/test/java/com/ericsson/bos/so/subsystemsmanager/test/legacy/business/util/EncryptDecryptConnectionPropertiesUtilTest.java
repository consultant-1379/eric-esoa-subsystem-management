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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.util;

import com.ericsson.bos.so.subsystemsmanager.business.kms.api.KmsService;
import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The Class EncryptDecryptConnectionPropertiesUtilTest.
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class EncryptDecryptConnectionPropertiesUtilTest {

    public static final String ENCRYPTED_PASSWORD = "xoSFawVcGSaacsunmAmojytJw";

    public static final String ENCRYPTED_USERNAME = "ENCRYPTED_USERNAME";

    public static final String ENCRYPTED_NAME = "ENCRYPTED_NAME";

    @Mock
    private KmsService kmsService;

    @InjectMocks
    private EncryptDecryptConnectionPropertiesUtil encryptDecryptConnectionPropertiesUtil;

    private ConnectionProperties decryptedConnectionProperties;

    private ConnectionProperties decryptedConnectionPropertiesWithAsterisk;

    private ConnectionProperties encryptedConnectionProperties;

    private ConnectionProperties encryptedConnectionPropertiesWithAsterisk;

    private final List<ConnectionProperties> connectionPropertiesEncryptedList = new ArrayList<>();

    private final List<ConnectionProperties> connectionPropertiesEncryptedListWithAsterisk = new ArrayList<>();

    private final List<ConnectionProperties> connectionPropertiesDecryptedList = new ArrayList<>();

    private final List<ConnectionProperties> connectionPropertiesDecryptedListWithAsterisk = new ArrayList<>();

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        setUpDecryptedConnectionProperties();
        setUpDecryptedConnectionPropertiesWithAsterisk();
    }

    /**
     * Iterate connection properties and encrypt selected property test.
     */
    @Test
    public void iterateConnectionPropertiesAndEncryptSelectedPropertyTest() {
        setupMocksForEncryption();
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(decryptedConnectionProperties);
        final List<ConnectionProperties> connectionProperties = encryptDecryptConnectionPropertiesUtil
                .encryptOrDecryptProperties(connectionPropertiesList, EncryptDecryptConnectionPropertiesUtil.ENCRYPT);

        assertEquals(ENCRYPTED_PASSWORD, connectionProperties.get(0).getProperties().get(0).getValue());
        assertEquals(ENCRYPTED_USERNAME, connectionProperties.get(0).getProperties().get(1).getValue());
    }

    /**
     * Iterate connection properties and encrypt all properties when asterisk appears test.
     */
    @Test
    public void iterateConnectionPropertiesAndEncryptAllPropertiesWhenAsteriskAppearsTest() {
        setupMocksForEncryption();
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(decryptedConnectionPropertiesWithAsterisk);
        final List<ConnectionProperties> connectionProperties = encryptDecryptConnectionPropertiesUtil
                .encryptOrDecryptProperties(connectionPropertiesList, EncryptDecryptConnectionPropertiesUtil.ENCRYPT);

        assertEquals(ENCRYPTED_PASSWORD, connectionProperties.get(0).getProperties().get(0).getValue());
        assertEquals(ENCRYPTED_USERNAME, connectionProperties.get(0).getProperties().get(1).getValue());
        assertEquals(ENCRYPTED_NAME, connectionProperties.get(0).getProperties().get(2).getValue());
    }

    /**
     * Encrypt selected property after an encrypt all request will reset is encrypt all to false test.
     */
    @Test
    public void encryptSelectedPropertyAfterAnEncryptAllRequestWillResetIsEncryptAllToFalseTest() {
        iterateConnectionPropertiesAndEncryptAllPropertiesWhenAsteriskAppearsTest();

        setupMocksForEncryption();
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(decryptedConnectionProperties);
        final List<ConnectionProperties> connectionProperties = encryptDecryptConnectionPropertiesUtil
                .encryptOrDecryptProperties(connectionPropertiesList, EncryptDecryptConnectionPropertiesUtil.ENCRYPT);

        assertEquals(ENCRYPTED_PASSWORD, connectionProperties.get(0).getProperties().get(0).getValue());
        assertEquals(ENCRYPTED_USERNAME, connectionProperties.get(0).getProperties().get(1).getValue());
        assertEquals(Constants.$_CONNECTIONS, connectionProperties.get(0).getProperties().get(2).getValue());
    }

    /**
     * Iterate connection properties decrypt selected property test.
     */
    @Test
    public void iterateConnectionPropertiesDecryptSelectedPropertyTest() {
        setupMocksForDecryption();

        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(encryptedConnectionProperties);
        final List<ConnectionProperties> connectionProperties = encryptDecryptConnectionPropertiesUtil
                .encryptOrDecryptProperties(connectionPropertiesList, EncryptDecryptConnectionPropertiesUtil.DECRYPT);

        assertEquals(Constants.$_PASSWORD, connectionProperties.get(0).getProperties().get(0).getValue());
        assertEquals(Constants.$_USERNAME, connectionProperties.get(0).getProperties().get(1).getValue());
    }

    /**
     * Setup mocks for encryption.
     */
    public void setupMocksForEncryption() {
        Mockito.when(kmsService.encryptProperty(decryptedConnectionProperties.getProperties().get(0).getValue()))
                .thenReturn(encryptedConnectionProperties.getProperties().get(0).getValue());
        Mockito.when(kmsService.encryptProperty(decryptedConnectionProperties.getProperties().get(1).getValue()))
                .thenReturn(encryptedConnectionProperties.getProperties().get(1).getValue());

        Mockito.when(kmsService.encryptProperty(decryptedConnectionPropertiesWithAsterisk.getProperties().get(0).getValue()))
                .thenReturn(encryptedConnectionPropertiesWithAsterisk.getProperties().get(0).getValue());
        Mockito.when(kmsService.encryptProperty(decryptedConnectionPropertiesWithAsterisk.getProperties().get(1).getValue()))
                .thenReturn(encryptedConnectionPropertiesWithAsterisk.getProperties().get(1).getValue());
        Mockito.when(kmsService.encryptProperty(decryptedConnectionPropertiesWithAsterisk.getProperties().get(2).getValue()))
                .thenReturn(encryptedConnectionPropertiesWithAsterisk.getProperties().get(2).getValue());
    }

    /**
     * Setup mocks for decryption.
     */
    public void setupMocksForDecryption() {
        Mockito.when(kmsService.decryptProperty(encryptedConnectionProperties.getProperties().get(0).getValue()))
                .thenReturn(decryptedConnectionProperties.getProperties().get(0).getValue());
        Mockito.when(kmsService.decryptProperty(encryptedConnectionProperties.getProperties().get(1).getValue()))
                .thenReturn(decryptedConnectionProperties.getProperties().get(1).getValue());
    }

    /**
     * Sets up the decrypted connection properties.
     */
    public void setUpDecryptedConnectionProperties() {
        setUpEncryptedConnectionProperties();
        decryptedConnectionProperties = new ConnectionProperties();
        decryptedConnectionProperties.setId(1L);
        decryptedConnectionProperties.setProperties(OldPropertyFactory.createCustomTestProperties(
                Constants.$_USERNAME, Constants.$_PASSWORD, Constants.$_CONNECTIONS, Constants.$_TENANT));
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add(Constants.$_PASSWORD);
        encryptedKeys.add(Constants.$_USERNAME);
        decryptedConnectionProperties.setEncryptedKeys(encryptedKeys);
        connectionPropertiesDecryptedList.add(decryptedConnectionProperties);
    }

    /**
     * Sets up the decrypted connection properties with asterisk.
     */
    public void setUpDecryptedConnectionPropertiesWithAsterisk() {
        setUpEncryptedConnectionPropertiesWithAsterisk();
        decryptedConnectionPropertiesWithAsterisk = new ConnectionProperties();
        decryptedConnectionPropertiesWithAsterisk.setId(2L);
        decryptedConnectionPropertiesWithAsterisk.setProperties(OldPropertyFactory.createCustomTestProperties(
                Constants.$_USERNAME, Constants.$_PASSWORD, Constants.$_CONNECTIONS, Constants.$_TENANT));
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add("*");
        decryptedConnectionPropertiesWithAsterisk.setEncryptedKeys(encryptedKeys);
        connectionPropertiesDecryptedListWithAsterisk.add(decryptedConnectionPropertiesWithAsterisk);
    }

    /**
     * Sets up the encrypted connection properties.
     */
    public void setUpEncryptedConnectionProperties() {
        encryptedConnectionProperties = new ConnectionProperties();
        encryptedConnectionProperties.setId(1L);
        encryptedConnectionProperties.setProperties(OldPropertyFactory.createCustomTestProperties(
                ENCRYPTED_USERNAME, ENCRYPTED_PASSWORD, Constants.$_CONNECTIONS, Constants.$_TENANT));
        encryptedConnectionProperties.getProperties().get(0).setEncrypted(true);
        encryptedConnectionProperties.getProperties().get(1).setEncrypted(true);
        connectionPropertiesEncryptedList.add(encryptedConnectionProperties);
    }

    /**
     * Sets up the encrypted connection properties with asterisk.
     */
    public void setUpEncryptedConnectionPropertiesWithAsterisk() {
        encryptedConnectionPropertiesWithAsterisk = new ConnectionProperties();
        encryptedConnectionPropertiesWithAsterisk.setId(1L);
        encryptedConnectionPropertiesWithAsterisk.setProperties(
                OldPropertyFactory.createCustomTestProperties(
                        ENCRYPTED_USERNAME, ENCRYPTED_PASSWORD, ENCRYPTED_NAME, Constants.$_TENANT));
        encryptedConnectionPropertiesWithAsterisk.getProperties().get(0).setEncrypted(true);
        encryptedConnectionPropertiesWithAsterisk.getProperties().get(1).setEncrypted(true);
        encryptedConnectionPropertiesWithAsterisk.getProperties().get(2).setEncrypted(true);
        connectionPropertiesEncryptedListWithAsterisk.add(encryptedConnectionPropertiesWithAsterisk);
    }

}
