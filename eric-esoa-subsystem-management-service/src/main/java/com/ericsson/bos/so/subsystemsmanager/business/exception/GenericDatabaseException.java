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
 * The class GenericDatabaseException
 */
public class GenericDatabaseException extends SubsystemsManagerException {

    protected static final String INTERNAL_ERROR_CODE = "SSM-I-11";
    protected static final String INTERNAL_ERROR_CODE1 = "SSM-I-35";
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor to set internal error code
     */
    public GenericDatabaseException() {
        super(INTERNAL_ERROR_CODE);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param errorData String
     */
    public GenericDatabaseException(final String errorData) {
        super(INTERNAL_ERROR_CODE1);
        setErrorData(errorData);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

}
