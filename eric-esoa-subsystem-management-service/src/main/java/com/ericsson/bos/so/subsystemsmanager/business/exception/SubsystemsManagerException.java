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

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * The class SubsystemsManagerException
 */
@JsonIgnoreProperties({ "localizedMessage", "message", "suppressed", "cause", "stackTrace" })
@Getter
public abstract class SubsystemsManagerException extends RuntimeException {

    private final String internalErrorCode;
    private final List<String> errorData = new ArrayList<>();

    /**
     * parameterized constructor internal error code
     * @param internalErrorCode String
     */
    public SubsystemsManagerException(final String internalErrorCode) {
        super();
        this.internalErrorCode = internalErrorCode;
    }

    /**
     * sets error data
     * @param errorData String
     */
    public void setErrorData(String errorData) {
        if (errorData != null) {
            this.errorData.add(errorData);
        } else {
            this.errorData.add("null");
        }

    }

    /**
     * gets http status
     * @return http status
     */
    public abstract HttpStatus getHttpStatus();

}