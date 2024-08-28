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
package com.ericsson.bos.so.subsystemsmanager.web.controller.v1;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.bos.so.subsystemsmanager.business.api.ConnectionPropsService;
import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemsServiceV1;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.connection_properties.SubsystemsApi;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ConnectionPropertiesController
 * Note: Controller implements ..api.connection_properties.SubsystemApi interface.
 */
@Slf4j
@RestController
@RequestMapping(SubsystemController.BASE_PATH)
public class ConnectionPropertiesController implements SubsystemsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPropertiesController.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private ConnectionPropsService connectionPropsService;

    @Autowired
    private SubsystemsServiceV1 subsystemsServiceV1;

    /**
     * Gets the connection properties by subsystem Id.
     *
     * @param subsystemId the subsystem id
     * @return the conn props by subsystem id
     */
    @Override
    public ResponseEntity<List<ConnectionProperties>> getConnPropsBySubsystemId(@PathVariable("subsystemId") final String subsystemId) {
        loggerHandler.logAudit(LOGGER, String.format("getConnPropsBySubsystemId() for subsystemId: %s", subsystemId));
        return new ResponseEntity<>(connectionPropsService.getConnPropsBySubsystemId(subsystemId), HttpStatus.OK);
    }

    /**
     * Post connection properties.
     *
     * @param subsystemId the subsystem id
     * @param connectionProperties the connection properties
     * @return the response entity
     */
    @Override
    public ResponseEntity<ConnectionProperties> postConnProps(@PathVariable("subsystemId") final String subsystemId,
                                                              @RequestBody(required = true) final ConnectionProperties connectionProperties) {
        loggerHandler.logAudit(LOGGER, String.format("postConnProps() for subsystemId : %s", subsystemId));
        final ConnectionProperties savedEntity = connectionPropsService.postConnProp(subsystemId, connectionProperties);
        clearGhostConnnectionProperties();
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
    }

    /**
     * Gets the connection properties by id.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertiesId the connection properties id
     * @return the conn props by id
     */
    @Override
    public ResponseEntity<ConnectionProperties> getConnPropsById(@PathVariable("subsystemId") final String subsystemId,
                                                                 @PathVariable("connectionPropertiesId") final String connectionPropertiesId) {
        loggerHandler.logAudit(LOGGER, String.format("getConnPropsById() for subsystemId : %s and connectionPropertiesId : %s"
                , subsystemId, connectionPropertiesId));
        return new ResponseEntity<>(connectionPropsService.getConnPropsById(subsystemId, connectionPropertiesId),
                HttpStatus.OK);
    }

    /**
     * Put connection properties by id.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertiesId the connection properties id
     * @param updatedConnProps the updated conn props
     * @return the response entity
     */
    @Override
    public ResponseEntity<ConnectionProperties> putConnPropsById(@PathVariable("subsystemId") final String subsystemId,
                                                                 @PathVariable("connectionPropertiesId") final String connectionPropertiesId,
                                                                 @RequestBody(required = true) final ConnectionProperties updatedConnProps) {
        loggerHandler.logAudit(LOGGER, String.format("putConnPropsById() for subsystemId : %s and connectionPropertiesId : %s"
                , subsystemId, connectionPropertiesId));
        final ConnectionProperties savedEntity = connectionPropsService.putConnProps(subsystemId, connectionPropertiesId, updatedConnProps);
        clearGhostConnnectionProperties();
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
    }

    /**
     * Patch connection properties by id.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertiesId the connection properties id
     * @param updatedConnProps the updated conn props
     * @return the response entity
     */
    @Override
    public ResponseEntity<ConnectionProperties> patchConnPropsById(@PathVariable("subsystemId") final String subsystemId,
                                                                   @PathVariable("connectionPropertiesId") final String connectionPropertiesId,
                                                                   @RequestBody(required = true) final Map<String, Object> updatedConnProps) {
        loggerHandler.logAudit(LOGGER, String.format("patchConnPropsById() for subsystemId : %s and connectionPropertiesId : %s"
                , subsystemId, connectionPropertiesId));
        final ConnectionProperties savedEntity = connectionPropsService.patchConnProps(subsystemId, connectionPropertiesId, updatedConnProps);
        clearGhostConnnectionProperties();
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
    }

    /**
     * Delete connection properties by id.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertiesId the connection properties id
     * @return the response entity
     */
    @Override
    public ResponseEntity<Void> deleteConnPropsById(@PathVariable("subsystemId") final String subsystemId,
            @PathVariable("connectionPropertiesId") final String connectionPropertiesId) {
        loggerHandler.logAudit(LOGGER, String.format("deleteConnPropsById() for subsystemId : %s and connectionPropertiesId : %s"
                , subsystemId, connectionPropertiesId));
        connectionPropsService.deleteConnProps(subsystemId, connectionPropertiesId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void clearGhostConnnectionProperties() {
        subsystemsServiceV1.clearGhostConnnectionProperties();
    }

}
