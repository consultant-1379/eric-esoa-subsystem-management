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
package com.ericsson.bos.so.subsystemsmanager.business.validation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class DeleteRequestValidator.
 */
@Slf4j
@Component
public class DeleteRequestValidator {

    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    /**
     * Verify whether subsystem in use.
     *
     * @param subsystemId the subsystemId
     * @return boolean
     */
    public Boolean isSubsystemInUse(String subsystemId) {
        log.info("isSubsystemInUse() called");
        final Optional<List> connectionPropertiesOpt = connectionPropertiesRepository.findBySubsystemId(Long.valueOf(subsystemId));
        if (connectionPropertiesOpt.isPresent()) {
            final List<ConnectionProperties> connectionProperties = connectionPropertiesOpt.get();
            return verifyIfConnectionPropertyHasSubsystemUserRefernce(connectionProperties);
        }
        return false;
    }

    /**
     * Verify whether connection property has subsystem user reference.
     *
     * @param connectionProperties the connectionProperties
     * @return boolean
     */
    private Boolean verifyIfConnectionPropertyHasSubsystemUserRefernce(List<ConnectionProperties> connectionProperties) {
        log.info("fetching connection props");
        for (ConnectionProperties connectionProperty : connectionProperties) {
            final List<SubsystemUser> subsystemUsers = connectionProperty.getSubsystemUsers();
            if (!subsystemUsers.isEmpty()) {
                log.info("Connection Property is IN USE");
                return true;
            }
        }
        log.info("Connection Property is NOT IN USE");
        return false;
    }

}