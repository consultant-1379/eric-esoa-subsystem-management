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
package com.ericsson.bos.so.subsystemsmanager.web.controller.v2;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_MANAGER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemUserService;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.v2.subsystem_user.SubsystemsApi;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemUserControllerV2.
 */
@Slf4j
@RestController
@RequestMapping(SubsystemUserControllerV2.BASE_PATH)
public class SubsystemUserControllerV2 implements SubsystemsApi {

    public static final String BASE_PATH = SUBSYSTEM_MANAGER + "/v2";

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemUserControllerV2.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private SubsystemUserService subsystemUserServiceV2;

    /**
     * Post user by connection properties id.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertiesId the connection properties id
     * @return the response entity
     */
    @Override
    public ResponseEntity<SubsystemUser> postUserByConnPropId(
            @PathVariable("subsystemId") String subsystemId,
            @PathVariable("connectionPropertiesId") String connectionPropertiesId) {
        loggerHandler.logAudit(LOGGER, String.format("postUserByConnPropId() received V2 service request for subsystem Id: %s", subsystemId));
        return new ResponseEntity<>(subsystemUserServiceV2.postUserByConnsPropId(subsystemId, connectionPropertiesId), HttpStatus.CREATED);
    }

    /**
     * Delete user by connection properties id.
     *
     * @param subsystemId the subsystem id
     * @param connectionPropertiesId the connection properties id
     * @param subsystemUserId the subsystem user id
     * @return the response entity
     */
    @Override
    public ResponseEntity<Void> deleteUserByConnPropId(
            @PathVariable("subsystemId") String subsystemId,
            @PathVariable("connectionPropertiesId") String connectionPropertiesId,
            @PathVariable("subsystemUserId") String subsystemUserId) {
        loggerHandler.logAudit(LOGGER, String.format("deleteUserByConnPropId() for V2 subsystemUserId: %s", subsystemUserId));
        subsystemUserServiceV2.deleteSubsystemUserById(subsystemId, connectionPropertiesId, subsystemUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}