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
 * The class ConnectionPropertiesDoesNotExistException
 */
public class ConnectionPropertiesDoesNotExistException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-J-00";

    /**
     * parameterized constructor setting error data and internal error data
     * @param errorData String
     */
    public ConnectionPropertiesDoesNotExistException(final String errorData) {
        super(INTERNAL_ERROR_CODE);
        setErrorData(errorData);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}