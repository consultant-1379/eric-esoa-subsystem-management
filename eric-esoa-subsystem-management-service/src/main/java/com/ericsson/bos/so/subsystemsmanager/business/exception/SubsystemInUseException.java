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
 * The class SubsystemInUseException
 */
public class SubsystemInUseException extends SubsystemsManagerException {

    /**
     * parameterized constructor setting error data and internal error code
     * @param internalErrorCode String
     * @param errorData String
     */
    public SubsystemInUseException(String internalErrorCode, String errorData) {
        super(internalErrorCode);
        setErrorData(errorData);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param internalErrorCode String
     * @param errorData String
     * @param errorData2 String
     */
    public SubsystemInUseException(String internalErrorCode, String errorData, String errorData2) {
        super(internalErrorCode);
        setErrorData(errorData);
        setErrorData(errorData2);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param internalErrorCode String
     * @param errorData String
     * @param errorData2 String
     * @param errorData3 String
     */
    public SubsystemInUseException(String internalErrorCode, String errorData, String errorData2, String errorData3) {
        super(internalErrorCode);
        setErrorData(errorData);
        setErrorData(errorData2);
        setErrorData(errorData3);
    }

    /**
     * returns status of conflict
     * @return status of conflict
     */
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
