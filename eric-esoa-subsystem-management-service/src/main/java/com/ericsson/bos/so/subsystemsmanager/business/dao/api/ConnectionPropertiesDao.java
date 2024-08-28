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
package com.ericsson.bos.so.subsystemsmanager.business.dao.api;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;

/**
 * The class ConnectionPropertiesDao
 */
@Component
public interface ConnectionPropertiesDao {

    /**
     * Saves connection properties to database
     * @param connectionProperties ConnectionProperties
     * @return response of the saved connection properties
     */
    ConnectionProperties saveConnectionProperties(ConnectionProperties connectionProperties);

    /**
     * Finds connection properties by Id
     * @param connPropsId String
     * @return response of connection properties fetched by Id
     */
    ConnectionProperties findConnPropsById(String connPropsId);

    /**
     * Finds connection properties by subsystem Id
     * @param subsystemId String
     * @return list of connection properties
     */
    List<ConnectionProperties> findConnPropsBySubsystemId(String subsystemId);

    /**
     * Deletes connection properties by Id
     * @param connectionPropertiesId String
     */
    void deleteConnPropsById(String connectionPropertiesId);

}
