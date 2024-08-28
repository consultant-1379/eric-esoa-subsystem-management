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
 * The class SubsystemSubtypeDoesNotExistException
 */
public class SubsystemSubtypeDoesNotExistException extends SubsystemsManagerException {

    public static final String INTERNAL_ERROR_CODE = "SSM-J-47";
    private static final long serialVersionUID = 1L;

    /**
     * parameterized constructor setting error data and internal error code
     * @param subsystemSubtypeId long
     * @param errorCode String
     */
    public SubsystemSubtypeDoesNotExistException(final long subsystemSubtypeId, final String errorCode) {
        super(errorCode);
        setErrorData("" + subsystemSubtypeId);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param subtypeName String
     * @param errorCode String
     */
    public SubsystemSubtypeDoesNotExistException(final String subtypeName, final String errorCode) {
        super(errorCode);
        setErrorData(subtypeName);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param subsystemTypeId long
     * @param subsystemSubtypeId long
     */
    public SubsystemSubtypeDoesNotExistException(final long subsystemTypeId, final long subsystemSubtypeId) {
        super(INTERNAL_ERROR_CODE);
        setErrorData("" + subsystemSubtypeId);
        setErrorData("" + subsystemTypeId);
    }

    /**
     * parameterized constructor setting error data and internal error code
     * @param subsystemType Object
     * @param subsystemSubtype Object
     */
    public SubsystemSubtypeDoesNotExistException(final Object subsystemType, final Object subsystemSubtype) {
        super(INTERNAL_ERROR_CODE);
        setErrorData("" + subsystemSubtype);
        setErrorData("" + subsystemType);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
