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
 * The Class KmsServiceException.
 */
public class KmsServiceException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-I-09";

    /**
     * Instantiates a new kms service exception.
     *
     * @param errorData the error data
     */
    public KmsServiceException(final String errorData) {
        super(INTERNAL_ERROR_CODE);
        setErrorData(errorData);
    }

    /**
     * Gets the http status.
     *
     * @return the http status
     */
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
