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
 * The class SubsystemUserDoesNotExistException
 */
public class SubsystemUserDoesNotExistException extends SubsystemsManagerException {

    protected static final String INTERNAL_ERROR_CODE = "SSM-J-18";

    private static final long serialVersionUID = 1L;

    /**
     * parameterized constructor setting internal error code and error data
     * @param errorData String
     */
    public SubsystemUserDoesNotExistException(final String errorData) {
        super(INTERNAL_ERROR_CODE);
        setErrorData(errorData);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
