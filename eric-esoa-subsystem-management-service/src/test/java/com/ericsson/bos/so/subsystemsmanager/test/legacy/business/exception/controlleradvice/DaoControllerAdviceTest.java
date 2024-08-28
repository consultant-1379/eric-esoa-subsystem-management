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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.exception.controlleradvice;

import static org.junit.Assert.assertEquals;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.DataIntegrityViolationException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.FailedJSONProcessingException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.GenericDatabaseException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.JSONIOException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemUserDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.controlleradvice.DaoControllerAdvice;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;

/**
 * The Class DaoControllerAdviceTest.
 */
public class DaoControllerAdviceTest extends BaseIntegrationTest {

    protected static final String ID = "1";

    @InjectMocks
    private DaoControllerAdvice daoControllerAdvice;

    @MockBean
    private KmsServiceImpl kmsService;


    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test handle invalid subsystem exception.
     */
    @Test
    public void testHandleInvalidSubsystemException() {
        final SubsystemDoesNotExistException subsystemDoesNotExistException = new SubsystemDoesNotExistException(ID);
        final ResponseEntity<Object> response = daoControllerAdvice.handleInvalidSubsystemException(subsystemDoesNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test handle invalid subsystem type exception.
     */
    @Test
    public void testHandleInvalidSubsystemTypeException() {
        final SubsystemTypeDoesNotExistException subsystemTypeDoesNotExistException = new SubsystemTypeDoesNotExistException("matrix-subsystem-type");
        final ResponseEntity<Object> response = daoControllerAdvice.handleInvalidSubsystemTypeException(subsystemTypeDoesNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test handle invalid connection properties exception.
     */
    @Test
    public void testHandleInvalidConnectionPropertiesException() {
        final ConnectionPropertiesDoesNotExistException connectionPropertiesDoesNotExistException = new ConnectionPropertiesDoesNotExistException(ID);
        final ResponseEntity<Object> response = daoControllerAdvice
                .handleInvalidConnectionPropertiesException(connectionPropertiesDoesNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test handle subsystem user does not exist exception.
     */
    @Test
    public void testHandleSubsystemUserDoesNotExistException() {
        // SubsystemUserDoesNotExistException subsystemUserDoesNotExistException = new SubsystemUserDoesNotExistException(EXCEPTION_MESSAGE,
        // ERROR_MESSAGE);
        final SubsystemUserDoesNotExistException subsystemUserDoesNotExistException = new SubsystemUserDoesNotExistException(ID);
        final ResponseEntity<Object> response = daoControllerAdvice.handleSubsystemUserDoesNotExistException(subsystemUserDoesNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test handle malformed content exception.
     */
    @Test
    public void testHandleMalformedContentException() {
        final MalformedContentException malformedContentException = new MalformedContentException("SSM-B-25");
        final ResponseEntity<Object> response = daoControllerAdvice.handleMalformedContentException(malformedContentException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test handle malformed content exception with one error data.
     */
    @Test
    public void testHandleMalformedContentExceptionWithOneErrorData() {
        final MalformedContentException malformedContentException = new MalformedContentException("SSM-B-34", ID);
        final ResponseEntity<Object> response = daoControllerAdvice.handleMalformedContentException(malformedContentException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test handle malformed content exception with two error data.
     */
    @Test
    public void testHandleMalformedContentExceptionWithTwoErrorData() {
        final MalformedContentException malformedContentException = new MalformedContentException("SSM-B-31", ID, ID);
        final ResponseEntity<Object> response = daoControllerAdvice.handleMalformedContentException(malformedContentException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test handle data integrity violation exception.
     */
    @Test
    public void testHandleDataIntegrityViolationException() {
        final DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException();
        final ResponseEntity<Object> response = daoControllerAdvice.handleDataIntegrityViolationException(dataIntegrityViolationException);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Test handle illegal argument exception.
     */
    @Test
    public void testHandleIllegalArgumentException() {
        final MalformedContentException illegalArgumentException = new MalformedContentException("SSM-B-31", ID, ID);
        final ResponseEntity<Object> response = daoControllerAdvice.handleIllegalArgumentException(illegalArgumentException);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Test handle data access exceptions.
     */
    @Test
    public void testHandleDataAccessExceptions() {
        final DataAccessResourceFailureException dataAccessException = new DataAccessResourceFailureException("Unable to acquire JDBC Connection");
        final ResponseEntity<Object> response = daoControllerAdvice.handleDataAccessExceptions(dataAccessException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Test handle all other exceptions.
     */
    @Test
    public void testHandleAllOtherExceptions() {
        final GenericDatabaseException ex = new GenericDatabaseException();
        final ResponseEntity<Object> response = daoControllerAdvice.handleGenericDatabaseException(ex);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    /**
     * Test handle failed JSON processing exception.
     */
    @Test
    public void testHandleFailedJSONProcessingException() {
        final FailedJSONProcessingException failedJSONProcessingException = new FailedJSONProcessingException(); // (EXCEPTION_MESSAGE, ERROR_MESSAGE)
        final ResponseEntity<Object> response = daoControllerAdvice.handleFailedJSONProcessingException(failedJSONProcessingException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test handle JSONIO exception.
     */
    @Test
    public void testHandleJSONIOException() {
        final JSONIOException exception = new JSONIOException();
        final ResponseEntity<Object> response = daoControllerAdvice.handleJSONIOException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

