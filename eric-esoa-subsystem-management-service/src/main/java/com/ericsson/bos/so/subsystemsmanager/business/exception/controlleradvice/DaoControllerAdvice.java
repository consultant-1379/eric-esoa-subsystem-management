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
package com.ericsson.bos.so.subsystemsmanager.business.exception.controlleradvice;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ericsson.bos.so.common.logging.security.SecurityLogger;
import com.ericsson.bos.so.subsystemsmanager.business.exception.*;
import com.ericsson.oss.orchestration.so.common.error.factory.ErrorMessageFactory;
import com.ericsson.oss.orchestration.so.common.error.message.ErrorMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * The class DaoControllerAdvice
 */
@ControllerAdvice
@Slf4j
public class DaoControllerAdvice {

    public static final String BAD_REQUEST_ERROR = "Bad Request Error: {}";
    private static final String SSM_B_21 = "SSM-B-21";
    private static final String SSM_B_29 = "SSM-B-29";
    private static final String SSM_B_38 = "SSM-B-38";
    private static final String SSM_B_40 = "SSM-B-40";
    private static final String SSM_B_55 = "SSM-B-55";

    /**
     * Handles RecordAlreadyExistsException exception
     * @param ex RecordAlreadyExistsException
     * @return response entity of built error message
     */
    @ExceptionHandler(RecordAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleRecordAlreadyExistsException(final RecordAlreadyExistsException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles RecordCannotBeDeletedException exception
     * @param ex RecordCannotBeDeletedException
     * @return response entity of built error message
     */
    @ExceptionHandler(RecordCannotBeDeletedException.class)
    @ResponseBody
    public ResponseEntity<Object> handleRecordCannotBeDeletedException(final RecordCannotBeDeletedException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles TenantDoesNotExistException exception
     * @param ex TenantDoesNotExistException
     * @return response entity of built error message
     */
    @ExceptionHandler(TenantDoesNotExistException.class)
    @ResponseBody
    public ResponseEntity<Object> handleTenantDoesNotExistException(final TenantDoesNotExistException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles ConnectionPropertiesDoesNotExistException exception
     * @param ex ConnectionPropertiesDoesNotExistException
     * @return response entity of built error message
     */
    @ExceptionHandler(ConnectionPropertiesDoesNotExistException.class)
    @ResponseBody
    public ResponseEntity<Object> handleInvalidConnectionPropertiesException(final ConnectionPropertiesDoesNotExistException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles K8sApiException exception
     * @param ex K8sApiException
     * @return response entity of built error message
     */
    @ExceptionHandler(K8sApiException.class)
    @ResponseBody
    public ResponseEntity<Object> handleK8sApiException(final K8sApiException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemUserDoesNotExistException exception
     * @param ex SubsystemUserDoesNotExistException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemUserDoesNotExistException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemUserDoesNotExistException(final SubsystemUserDoesNotExistException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemDoesNotExistException exception
     * @param ex SubsystemDoesNotExistException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemDoesNotExistException.class)
    @ResponseBody
    public ResponseEntity<Object> handleInvalidSubsystemException(final SubsystemDoesNotExistException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemTypeDoesNotExistException exception
     * @param ex SubsystemTypeDoesNotExistException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemTypeDoesNotExistException.class)
    @ResponseBody
    public ResponseEntity<Object> handleInvalidSubsystemTypeException(final SubsystemTypeDoesNotExistException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles SubsystemTypeAlreadyExistsException exception
     * @param exception SubsystemTypeAlreadyExistsException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemTypeAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemTypeAlreadyExistsException(final SubsystemTypeAlreadyExistsException exception) {
        return new ResponseEntity<>(createErrorMessage(exception), HttpStatus.CONFLICT);
    }

    /**
     * Handles SubsystemSubtypeAlreadyExistsException exception
     * @param exception SubsystemSubtypeAlreadyExistsException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemSubtypeAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemSubtypeAlreadyExistsException(final SubsystemSubtypeAlreadyExistsException exception) {
        return new ResponseEntity<>(createErrorMessage(exception), HttpStatus.CONFLICT);
    }

    /**
     * Handles SubsystemSubtypeDoesNotExistException exception
     * @param exception SubsystemSubtypeDoesNotExistException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemSubtypeDoesNotExistException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemSubtypeDoesNotExistException(final SubsystemSubtypeDoesNotExistException exception) {
        return new ResponseEntity<>(createErrorMessage(exception), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles NameMustBeUniqueException exception
     * @param exception NameMustBeUniqueException
     * @return response entity of built error message
     */
    @ExceptionHandler(NameMustBeUniqueException.class)
    @ResponseBody
    public ResponseEntity<Object> handleNameMustBeUniqueExceptionException(final NameMustBeUniqueException exception) {
        return new ResponseEntity<>(createErrorMessage(exception), exception.getHttpStatus());
    }

    /**
     * Handles DataAccessException exception
     * @param ex DataAccessException
     * @return response entity of built error message
     */
    @ExceptionHandler({DataAccessException.class})
    @ResponseBody
    public ResponseEntity<Object> handleDataAccessExceptions(final DataAccessException ex) {
        SecurityLogger.withFacility(() ->
            log.error("Unable to connect to Database: {}", ex.getMessage())
        );
        return new ResponseEntity<>(createErrorMessage(SSM_B_55), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles DataIntegrityViolationException exception
     * @param ex DataIntegrityViolationException
     * @return response entity of built error message
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleDataIntegrityViolationException(final DataIntegrityViolationException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles FailedJSONProcessingException exception
     * @param ex FailedJSONProcessingException
     * @return response entity of built error message
     */
    @ExceptionHandler(FailedJSONProcessingException.class)
    @ResponseBody
    public ResponseEntity<Object> handleFailedJSONProcessingException(final FailedJSONProcessingException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles FailedJSONParameterException exception
     * @param ex FailedJSONParameterException
     * @return response entity of built error message
     */
    @ExceptionHandler(FailedJSONParameterException.class)
    @ResponseBody
    public ResponseEntity<Object> handleFFailedJSONParameterException(final FailedJSONParameterException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles JSONIOException exception
     * @param ex JSONIOException
     * @return response entity of built error message
     */
    @ExceptionHandler(JSONIOException.class)
    @ResponseBody
    public ResponseEntity<Object> handleJSONIOException(final JSONIOException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles HttpMediaTypeException exception
     * @param ex HttpMediaTypeException
     * @return response entity of built error message
     */
    @ExceptionHandler({HttpMediaTypeException.class, HttpMessageConversionException.class})
    @ResponseBody
    public ResponseEntity<Object> handleBadRequestException(final GenericJSONException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles ServiceUnavailableException exception
     * @param ex ServiceUnavailableException
     * @return response entity of built error message
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseBody
    public ResponseEntity<Object> handleServiceUnavailableException(final ServiceUnavailableException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemCannotBeDeletedException exception
     * @param ex SubsystemCannotBeDeletedException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemCannotBeDeletedException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemCannotBeDeletedException(final SubsystemCannotBeDeletedException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemPartialDeleteException exception
     * @param ex SubsystemPartialDeleteException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemPartialDeleteException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemSubsystemPartialDeleteException(final SubsystemPartialDeleteException ex) {
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles GenericDatabaseException exception
     * @param exception GenericDatabaseException
     * @return response entity of built error message
     */
    @ExceptionHandler(GenericDatabaseException.class)
    @ResponseBody
    public ResponseEntity<Object> handleGenericDatabaseException(final GenericDatabaseException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(createErrorMessage(exception), exception.getHttpStatus());
    }

    /**
     * Handles Exception exception
     * @param exception Exception
     * @return response entity of built error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleAllOtherExceptions(final Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(createErrorMessage(SSM_B_55), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles MalformedContentException exception
     * @param ex MalformedContentException
     * @return response entity of built error message
     */
    @ExceptionHandler(MalformedContentException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMalformedContentException(final MalformedContentException ex) {
        log.error("MalformedContentException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles AdapterErrorException exception
     * @param ex AdapterErrorException
     * @return response entity of built error message
     */
    @ExceptionHandler(AdapterErrorException.class)
    @ResponseBody
    public ResponseEntity<Object> handleAdapterErrorException(final AdapterErrorException ex) {
        log.error("AdapterErrorException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles IllegalArgumentException exception
     * @param ex IllegalArgumentException
     * @return response entity of built error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Object> handleIllegalArgumentException(final MalformedContentException ex) {
        log.error("IllegalArgumentException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), HttpStatus.CONFLICT);
    }

    /**
     * Jandles SubsystemInUseException exception
     * @param ex SubsystemInUseException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemInUseException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemInUseExceptions(final SubsystemInUseException ex) {
        log.error("Subsystem in use: {}", ex.getInternalErrorCode());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles ApiKeyErrorException exception
     * @param ex ApiKeyErrorException
     * @return response entity of built error message
     */
    @ExceptionHandler(ApiKeyErrorException.class)
    @ResponseBody
    public ResponseEntity<Object> handleApiKeyErrorException(final ApiKeyErrorException ex) {
        log.error("ApiKeyErrorException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemTypeErrorException exception
     * @param ex SubsystemTypeErrorException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemTypeErrorException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemTypeErrorException(final SubsystemTypeErrorException ex) {
        log.error("SubsystemTypeErrorException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handles SubsystemSubtypeDoesNotSupportException exception
     * @param ex SubsystemSubtypeDoesNotSupportException
     * @return response entity of built error message
     */
    @ExceptionHandler(SubsystemSubtypeDoesNotSupportException.class)
    @ResponseBody
    public ResponseEntity<Object> handleSubsystemSubtypeDoesNotSupportException(final SubsystemSubtypeDoesNotSupportException ex) {
        log.error("SubsystemSubtypeDoesNotSupportException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handle KMS service access exception.
     *
     * @param ex KmsServiceException
     * @return HttpErrorResponse
     */
    @ExceptionHandler(KmsServiceException.class)
    @ResponseBody
    public ResponseEntity<Object> handleKmsServiceException(final KmsServiceException ex) {
        log.error("KmsServiceException: {} - {} - {}", ex.getHttpStatus(), ex.getInternalErrorCode(), ex.getErrorData());
        return new ResponseEntity<>(createErrorMessage(ex), ex.getHttpStatus());
    }

    /**
     * Handle 404 http error.
     *
     * @param ex NoHandlerFoundException
     * @return ErrorMessage
     */
    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleNoHandlerFoundException(final NoHandlerFoundException ex) {
        log.warn(BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(createErrorMessage(SSM_B_21), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle not supported http request method error.
     *
     * @param ex HttpRequestMethodNotSupportedException
     * @return ErrorMessage
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleInvalidRequestError(final HttpRequestMethodNotSupportedException ex) {
        log.warn(BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(createErrorMessage(SSM_B_29), HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle exception when HttpMessageConverter.read method fails.
     *
     * @param ex HttpMessageNotReadableException
     * @return ErrorMessage
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        log.warn(BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(createErrorMessage(SSM_B_29), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException exception
     * @param ex MethodArgumentNotValidException
     * @return response entity of built error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final String errorData = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError ? (((FieldError) error).getField() + " " + error.getDefaultMessage())
                        : error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Bad Request Error: {}. {}", ex.getMessage(), errorData);
        final ErrorMessage errorMessage = ErrorMessageFactory.buildFrom(SSM_B_40, errorData);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ResourceAccessException exception
     * @param ex ResourceAccessException
     * @return response entity of built error message
     */
    @ExceptionHandler({ResourceAccessException.class})
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleResourceAccessException(final ResourceAccessException ex) {
        log.error("Service Unavailable Error", ex);
        return new ResponseEntity<>(createErrorMessage(SSM_B_38), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle External service access exception.
     *
     * @param ex ResourceAccessException
     * @return ErrorMessage
     */
    public ErrorMessage createErrorMessage(SubsystemsManagerException ex) {
        ErrorMessage errorMessage = null;
        try {
            errorMessage = ErrorMessageFactory.buildFrom(ex.getInternalErrorCode(),
                    ex.getErrorData());
        } catch (Exception e) {
            log.error("BaseErrorMessageFactoryException during the errorMessage builder: " + e.getMessage(), e);
        }
        return errorMessage;
    }

    private static ErrorMessage createErrorMessage(String errorCode) {
        return ErrorMessageFactory.buildFrom(errorCode);
    }
}
