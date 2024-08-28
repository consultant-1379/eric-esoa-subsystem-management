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
 * The class JSONIOException
 */
//@ApiModel
public class JSONIOException extends SubsystemsManagerException {

    protected static final String INTERNAL_ERROR_CODE = "SSM-C-04";

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor to set internal error code
     */
    public JSONIOException() {
        super(INTERNAL_ERROR_CODE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}