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
 * The class SubsystemTypeAlreadyExistsException
 */
public class SubsystemTypeAlreadyExistsException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-K-16";

    /**
     * parameterized constructor setting internal error code and error data
     * @param subsystemTypeName String
     */
    public SubsystemTypeAlreadyExistsException(final String subsystemTypeName) {
        super(INTERNAL_ERROR_CODE);
        setErrorData(subsystemTypeName);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
