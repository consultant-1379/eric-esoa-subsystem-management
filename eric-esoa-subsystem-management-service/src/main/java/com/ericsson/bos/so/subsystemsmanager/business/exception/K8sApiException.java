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
 * The class K8sApiException
 */
public class K8sApiException extends SubsystemsManagerException {

    protected static final String INTERNAL_ERROR_CODE = "SSM-F-05";
    protected static final String INTERNAL_ERROR_CODE1 = "SSM-F-39";

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor to set internal error code
     */
    public K8sApiException() {
        super(INTERNAL_ERROR_CODE);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param errorData String
     * @param errorData2 String
     */
    public K8sApiException(final String errorData, final String errorData2) {
        super(INTERNAL_ERROR_CODE1);
        setErrorData(errorData);
        setErrorData(errorData2);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
