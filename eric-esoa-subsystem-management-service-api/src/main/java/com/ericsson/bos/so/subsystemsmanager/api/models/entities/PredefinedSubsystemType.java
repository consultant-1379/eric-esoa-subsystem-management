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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.Getter;

/**
 * The Enum PredefinedSubsystemType.
 */
public enum PredefinedSubsystemType {

    NFVO("NFVO"), DOMAIN_MANAGER("DomainManager"), PHYSICAL_DEVICE("PhysicalDevice"), CM_GATEWAY("CmGateway"), INVENTORY_SYSTEM(
            "InventorySystem"), DOMAIN_ORCHESTRATOR("DomainOrchestrator"), AUTHENTICATION_SYSTEMS("AuthenticationSystems");

    public static final List<String> CHECK_CONNECTIVITY_SUPPORTED_SUBSYSTEM_TYPES = Arrays.asList(NFVO.getType(), DOMAIN_MANAGER.getType());

    public static final List<String> SUBSYSTEM_TYPES_WITH_SINGLE_CONNECTION_PROPERTIES = Arrays.asList(DOMAIN_MANAGER.getType(),
            DOMAIN_ORCHESTRATOR.getType(), AUTHENTICATION_SYSTEMS.getType());

    private static final Set<String> PRIMARY_TYPES = ImmutableSet.of(NFVO.getType(), DOMAIN_MANAGER.getType(), DOMAIN_ORCHESTRATOR.getType(),
            AUTHENTICATION_SYSTEMS.getType());

    /**
     * Gets the type.
     *
     * @return the type
     */
    @Getter
    private final String type;

    /**
     * Instantiates a new predefined subsystem type.
     *
     * @param type the type
     */
    PredefinedSubsystemType(String type) {
        this.type = type;
    }

    /**
     * To set.
     *
     * @return the set of PredefinedSubsystemType
     */
    public static Set<String> toSet() {
        return Stream.of(values()).map(PredefinedSubsystemType::getType).collect(Collectors.toSet());
    }

    /**
     * Checks if it is predefined subtype.
     *
     * @param type the type
     * @return true, if predefined type
     */
    public static boolean isPredefinedType(String type) {
        return toSet().stream().anyMatch(subType -> subType.equalsIgnoreCase(type));
    }

    public static Set<String> getPrimaryTypes() {
        return PRIMARY_TYPES;
    }

    /**
     * Gets the non primary types.
     *
     * @return the non primary types
     */
    public static Set<String> getNonPrimaryTypes() {
        return Sets.difference(toSet(), getPrimaryTypes());
    }

    /**
     * Checks if the subsystemType supports check-connectivity..
     *
     * @param subsytemType the subsytem type
     * @return true, if is supported connectivity subsystem type
     */
    public static boolean isSupportedConnectivitySubsystemType(String subsytemType) {
        return CHECK_CONNECTIVITY_SUPPORTED_SUBSYSTEM_TYPES.contains(subsytemType);
    }

    /**
     * Checks if single connection property is supported.
     *
     * @param subsytemType the subsytem type
     * @return true, if is supported single connection property
     */
    public static boolean isSupportedSingleConnectionProperty(String subsytemType) {
        return SUBSYSTEM_TYPES_WITH_SINGLE_CONNECTION_PROPERTIES.contains(subsytemType);
    }

    /**
     * Method to resolve the Subsystem type category.
     *
     * @param subsystemType the subsystem type
     * @return the subsystem type category
     */
    public static SubsystemTypeCategory resolveCategory(final String subsystemType) {
        return PRIMARY_TYPES.contains(subsystemType) ? SubsystemTypeCategory.PRIMARY : SubsystemTypeCategory.CUSTOM;
    }

    @Override
    public String toString() {
        return type;
    }
}
