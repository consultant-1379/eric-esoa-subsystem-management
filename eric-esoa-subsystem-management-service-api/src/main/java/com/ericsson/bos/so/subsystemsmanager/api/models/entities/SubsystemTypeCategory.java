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
package com.ericsson.bos.so.subsystemsmanager.api.models.entities;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * The Enum SubsystemTypeCategory.
 */
public enum SubsystemTypeCategory {

    PRIMARY("Primary"), CUSTOM("Custom");

    @Getter
    @JsonValue
    private final String category;

    /**
     * Instantiates a new subsystem type category.
     *
     * @param category the category
     */
    SubsystemTypeCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return category;
    }

}