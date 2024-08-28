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
package com.ericsson.bos.so.subsystemsmanager.test.unit.kms;

import com.ericsson.bos.so.subsystemsmanager.business.exception.KmsServiceException;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.KmsIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.GetSubsystemsIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Class KmsServiceTest.
 */
@TestPropertySource(properties = { "spring.cloud.vault.enabled=true"})
public class KmsServiceTest extends KmsIntegrationTest {

    @Autowired
    private KmsServiceImpl kmsService;

    /**
     * Setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void testSetup() throws IOException {
        setupKmsMockWebServer();
    }

    /**
     * Teardown.
     *
     * @throws Exception the exception
     */
    @After
    public void testTearDown() throws Exception {
        closeKmsMockWebServer();
    }

    /**
     * GIVEN value for encryption WHEN kms service is called for encryption THEN expected encrypted value is returned.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_valueForEncryption_WHEN_kmsServiceIsCalledForEncryption_THEN_expectedEncryptedValueIsReturned() {
        // given
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.OK, vaultEncryptResponse));

        // when
        final String actualEncryptedPropertyValue = kmsService.encryptProperty(GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE);

        // then
        assertEquals(GetSubsystemsIntegrationTest.ENCRYPTED_PASSWORD_VALUE_ONE, actualEncryptedPropertyValue);
    }

    /**
     * GIVEN value for decryption WHEN kms service is called for decryption THEN expected decrypted value is returned.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_valueForDecryption_WHEN_kmsServiceIsCalledForDecryption_THEN_expectedDecryptedValueIsReturned() {
        // given
        kmsMockWebserver.enqueue(createMockResponse(HttpStatus.OK, vaultDecryptResponse));

        // when
        final String actualDecryptedPropertyValue = kmsService.decryptProperty(GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE);

        // then
        assertEquals(GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE, actualDecryptedPropertyValue);
    }

    /**
     * GIVEN kms returns bad request WHEN kms service is called for encryption THEN kms service exception.
     */
    @Test(expected = KmsServiceException.class)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsReturnsBadRequest_WHEN_kmsServiceIsCalledForEncryption_THEN_kmsServiceException() {
        // given
        mockKmsBadRequestResponse();

        // when
        kmsService.encryptProperty(GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE);
    }

    /**
     * GIVEN kms returns error WHEN kms service is called for decryption THEN kms service exception.
     */
    @Test(expected = KmsServiceException.class)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void GIVEN_kmsReturnsError_WHEN_kmsServiceIsCalledForDecryption_THEN_kmsServiceException() {
        // given
        mockKmsBadRequestResponse();

        // when
        kmsService.decryptProperty(GetSubsystemsIntegrationTest.DECRYPTED_PASSWORD_VALUE_ONE);
    }

}
