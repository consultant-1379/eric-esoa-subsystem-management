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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.Getter;

/**
 * The Enum PredefinedSubsystemSubtype.
 */
public enum PredefinedSubsystemSubtype {

    OAUTH2_CLIENT_CREDENTIALS("Oauth2ClientCredentials");

    private static final Set<String> PREDEFINED_SUBTYPES = ImmutableSet.of(OAUTH2_CLIENT_CREDENTIALS.getName());

    @Getter
    private final String name;

    /**
     * Instantiates a new predefined subsystem subtype.
     *
     * @param name the name
     */
    PredefinedSubsystemSubtype(String name) {
        this.name = name;
    }

    /**
     * To set.
     *
     * @return the set of PredefinedSubsystemSubtype
     */
    public static Set<String> toSet() {
        return Stream.of(values()).map(PredefinedSubsystemSubtype::getName).collect(Collectors.toSet());
    }

    /**
     * Checks if it is predefined subsystem subtype.
     *
     * @param name the name
     * @return true, if predefined subsystem subtype
     */
    public static boolean isPredefinedSubtype(String name) {
        return toSet().stream().anyMatch(subTypeName -> subTypeName.equalsIgnoreCase(name));
    }

    public static Set<String> getPredefinedSubtypes() {
        return PREDEFINED_SUBTYPES;
    }

    public static Set<String> getNonPrimarySubtypes() {
        return Sets.difference(toSet(), getPredefinedSubtypes());
    }

    @Override
    public String toString() {
        return name;
    }
}
