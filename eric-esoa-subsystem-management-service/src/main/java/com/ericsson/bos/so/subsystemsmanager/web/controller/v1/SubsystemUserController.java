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
import com.ericsson.bos.so.subsystemsmanager.api.subsystem_user.SubsystemsApi;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemUserController
 * Note: Controller implements ..api.subsystem_user.SubsystemApi interface.
 */
@Slf4j
@RestController
@RequestMapping(SubsystemController.BASE_PATH)
public class SubsystemUserController implements SubsystemsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemUserController.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private SubsystemUserService subsystemUserService;

    @Override
    public ResponseEntity<SubsystemUser> postUserByConnPropId(
            @PathVariable("subsystemId") String subsystemId,
            @PathVariable("connectionPropertiesId") String connectionPropertiesId) {
        loggerHandler.logAudit(LOGGER, String.format("postUserByConnPropId() received service request for subsystem Id: %s", subsystemId));
        return new ResponseEntity<>(subsystemUserService.postUserByConnsPropId(subsystemId, connectionPropertiesId), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUserByConnPropId(
            @PathVariable("subsystemId") String subsystemId,
            @PathVariable("connectionPropertiesId") String connectionPropertiesId,
            @PathVariable("subsystemUserId") String subsystemUserId) {
        loggerHandler.logAudit(LOGGER, String.format("deleteUserByConnPropId() for subsystemUserId: %s", subsystemUserId));
        subsystemUserService.deleteSubsystemUserById(subsystemId, connectionPropertiesId, subsystemUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}