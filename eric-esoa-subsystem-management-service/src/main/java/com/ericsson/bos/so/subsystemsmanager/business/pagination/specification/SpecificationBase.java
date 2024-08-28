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
package com.ericsson.bos.so.subsystemsmanager.business.pagination.specification;

import java.io.IOException;

import org.springframework.data.jpa.domain.Specification;

/**
 * The Class SpecificationBase.
 *
 * @param <T> the generic type
 * @param <U> the generic type
 */
public abstract class SpecificationBase<T, U> {

    private static final String WILDCARD = "%";

    /**
     * Gets the filter.
     *
     * @param filterRequest the filter request
     * @return the filter
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract Specification<T> getFilter(U filterRequest) throws IOException;

    /**
     * Contains lower case.
     *
     * @param searchField the search field
     * @return the string
     */
    protected String containsLowerCase(String searchField) {
        return WILDCARD + searchField.toLowerCase() + WILDCARD;
    }
}
