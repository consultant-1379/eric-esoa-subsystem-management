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
 * The class MalformedContentException
 */
public class MalformedContentException extends SubsystemsManagerException {

    /**
     * parameterized constructor setting internal error code inheriting from parent exception
     * @param internalCode String
     */
    public MalformedContentException(final String internalCode) {
        super(internalCode);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param internalCode String
     * @param errorData String
     */
    public MalformedContentException(final String internalCode, final String errorData) {
        super(internalCode);
        setErrorData(errorData);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param internalCode String
     * @param errorData1 String
     * @param errorData2 String
     */
    public MalformedContentException(final String internalCode, final String errorData1, final String errorData2) {
        super(internalCode);
        setErrorData(errorData1);
        setErrorData(errorData2);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
