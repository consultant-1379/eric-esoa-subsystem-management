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
 * The class GenericJSONException
 */
//@ApiModel
public class GenericJSONException extends SubsystemsManagerException {

    protected static final String INTERNAL_ERROR_CODE = "SSM-B-03";

    /**
     * Default constructor to set internal error code
     */
    public GenericJSONException() {
        super(INTERNAL_ERROR_CODE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
