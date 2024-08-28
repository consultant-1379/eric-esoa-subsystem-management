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
package com.ericsson.bos.so.subsystemsmanager.business.exception;

import org.springframework.http.HttpStatus;

/**
 * The class ServiceUnavailableException
 */
public class ServiceUnavailableException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-I-11";
    public static final String INTERNAL_ERROR_CODE1 = "SSM-C-08";

    /**
     * Default constructor to set internal error code
     */
    public ServiceUnavailableException() {
        super(INTERNAL_ERROR_CODE);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param errorData String
     */
    public ServiceUnavailableException(final String errorData) {
        super(INTERNAL_ERROR_CODE1);
        setErrorData(errorData);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

}
