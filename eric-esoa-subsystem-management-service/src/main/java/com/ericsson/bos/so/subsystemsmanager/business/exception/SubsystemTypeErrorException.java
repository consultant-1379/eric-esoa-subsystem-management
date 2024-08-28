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
 * The class SubsystemTypeErrorException
 */
public class SubsystemTypeErrorException extends SubsystemsManagerException {

    /**
     * parameterized constructor setting internal error code and error data
     * @param internalErrorCode String
     * @param errorData String
     */
    public SubsystemTypeErrorException(String internalErrorCode, String errorData) {
        super(internalErrorCode);
        setErrorData(errorData);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
