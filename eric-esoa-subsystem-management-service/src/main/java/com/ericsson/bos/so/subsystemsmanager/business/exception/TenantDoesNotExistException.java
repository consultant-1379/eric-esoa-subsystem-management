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
 * The class TenantDoesNotExistException
 */
public class TenantDoesNotExistException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-J-19";

    /**
     * parameterized constructor setting internal error code and error data
     * @param tenantName String
     */
    public TenantDoesNotExistException(String tenantName) {
        super(INTERNAL_ERROR_CODE);
        setErrorData(tenantName);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
