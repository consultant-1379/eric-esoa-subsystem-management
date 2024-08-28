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
package com.ericsson.bos.so.subsystemsmanager.business.subsystem;

/**
 * The Enum CrudType.
 */
public enum CrudType {
    CREATE("Create"),
    READ("Read"),
    UPDATE("Update"),
    DELETE("Delete");

    private final String type;

    /**
     * Instantiates a new crud type.
     *
     * @param type the type
     */
    CrudType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
