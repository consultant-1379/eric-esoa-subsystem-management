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
 * The class SubsystemDoesNotExistException
 */
public class SubsystemDoesNotExistException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-J-13";

    private static final long serialVersionUID = 1L;

    /**
     * parameterized constructor setting error data and internal error code
     * @param subsystemIdentifier String
     */
    public SubsystemDoesNotExistException(final String subsystemIdentifier) {
        super(INTERNAL_ERROR_CODE);
        setErrorData(subsystemIdentifier);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
