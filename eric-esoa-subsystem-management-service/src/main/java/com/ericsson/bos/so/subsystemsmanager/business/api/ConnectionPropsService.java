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
package com.ericsson.bos.so.subsystemsmanager.business.api;

import java.util.List;
import java.util.Map;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;

/**
 * The interface ConnectionPropsService
 */
public interface ConnectionPropsService {

    /**
     * Gets connection properties by id
     * @param subsystemId String
     * @param connectionPropertiesId String
     * @return list of connection properties
     */
    ConnectionProperties getConnPropsById(String subsystemId, String connectionPropertiesId);

    /**
     * Creates connection properties
     * @param subsystemId String
     * @param connPropsId ConnectionProperties
     * @return saved connection properties
     */
    ConnectionProperties postConnProp(String subsystemId, ConnectionProperties connPropsId);

    /**
     * Applies patch on existing connection properties of a subsystem
     * @param subsystemId String
     * @param connPropsId String
     * @param patchRequestFields Map
     * @return updated connection properties
     */
    ConnectionProperties patchConnProps(String subsystemId, String connPropsId, Map<String, Object> patchRequestFields);

    /**
     * Updates connection properties of an existing subsystem
     * @param subsystemId String
     * @param connPropsId String
     * @param connectionProperties ConnectionProperties
     * @return updated connection properties
     */
    ConnectionProperties putConnProps(String subsystemId, String connPropsId, ConnectionProperties connectionProperties);

    /**
     * Deletes connection properties
     * @param subsystemId String
     * @param connectionPropertiesId String
     */
    void deleteConnProps(String subsystemId, String connectionPropertiesId);

    /**
     * Gets connection properties of a subsystem by it's id
     * @param subsystemId String
     * @return list of connection properties
     */
    List<ConnectionProperties> getConnPropsBySubsystemId(String subsystemId);
}