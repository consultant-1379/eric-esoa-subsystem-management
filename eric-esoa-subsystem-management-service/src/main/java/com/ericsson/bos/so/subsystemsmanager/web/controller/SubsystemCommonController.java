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
package com.ericsson.bos.so.subsystemsmanager.web.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemsService;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemCannotBeDeletedException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemPartialDeleteException;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemCommonController.
 */
@Slf4j
public class SubsystemCommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemCommonController.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    @Qualifier("subsystemsService")
    private SubsystemsService subsystemsService;

    /**
     * Delete all subsystems.
     *
     * @param subsystemIds the subsystem ids
     * @return the response entity
     */
    public ResponseEntity<Void> deleteAllSubsystems(final List<String> subsystemIds) {
        LOGGER.info("Request received to delete the following subsystems {}", subsystemIds);
        final String getClientsStr =
            String.format("Request received to delete the following subsystems %s", subsystemIds);
        loggerHandler.logAudit(LOGGER, getClientsStr);
        final List<String> subsystemsNotFound = new ArrayList<>();
        final List<String> subsystemInUse = new ArrayList<>();
        final List<String> subsystemCannotDelete = new ArrayList<>();

        for (String subsystemId : subsystemIds) {
            try {
                LOGGER.info("Attempting to delete subsystem with id: {}", subsystemId);
                final String getClientsStr1 =
                    String.format("Attempting to delete subsystem with id: %s", subsystemId);
                loggerHandler.logAudit(LOGGER, getClientsStr1);
                if (!subsystemsService.deleteSubsystemById(subsystemId)) {
                    subsystemsNotFound.add(subsystemId);
                }
            } catch (SubsystemCannotBeDeletedException exception) {
                LOGGER.warn("Failed to delete subsystem with id: {}, {}, {}", subsystemId, exception.getMessage(), exception);
                loggerHandler.logAudit(LOGGER, String.format("Failed to delete subsystem with id: %s, %s, %s",
                    subsystemId, exception.getMessage(), exception));
                subsystemInUse.add(exception.getErrorData().get(0));
            } catch (SubsystemPartialDeleteException exception) {
                LOGGER.warn("Failed to delete subsystem with id: {}, {}, {}", subsystemId, exception.getMessage(), exception);
                loggerHandler.logAudit(LOGGER, String.format("Failed to delete subsystem with id: %s, %s, %s",
                    subsystemId, exception.getMessage(), exception));
                subsystemCannotDelete.add(exception.getErrorData().get(0));
            }
        }
        manageSubsystemConditions(subsystemsNotFound, subsystemInUse, subsystemCannotDelete);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Manage subsystem conditions.
     *
     * @param subsystemsNotFound the subsystems not found
     * @param subsystemInUse the subsystem in use
     * @param subsystemCannotDelete the subsystem cannot delete
     */
    private void manageSubsystemConditions(final List<String> subsystemsNotFound, final List<String> subsystemInUse,
                                           final List<String> subsystemCannotDelete) {
        if (!subsystemInUse.isEmpty()) {
            if (subsystemInUse.size() == 1) {
                throw new SubsystemCannotBeDeletedException(subsystemInUse.get(0));
            } else {
                throw new SubsystemCannotBeDeletedException(String.valueOf(subsystemInUse));
            }
        }
        if (!subsystemCannotDelete.isEmpty()) {
            if (subsystemCannotDelete.size() == 1) {
                throw new SubsystemPartialDeleteException(String.valueOf(subsystemCannotDelete.get(0)));
            } else {
                throw new SubsystemPartialDeleteException(String.valueOf(subsystemCannotDelete));
            }
        }
        if (!subsystemsNotFound.isEmpty()) {
            if (subsystemsNotFound.size() == 1) {
                throw new SubsystemDoesNotExistException(subsystemsNotFound.get(0));
            } else {
                throw new SubsystemDoesNotExistException(String.valueOf(subsystemsNotFound));
            }
        }
    }
}
