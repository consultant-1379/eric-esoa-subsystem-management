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
package com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.transaction.TestTransaction;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class EntityUtil.
 */
@Component
@Slf4j
public class EntityUtil {

    @Autowired
    private SubsystemRepository subsystemRepository;
    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;
    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    /**
     * Purge all repos.
     */
    public void purgeAllRepos() {
        log.trace("--> Purging all repos...");
        if (!TestTransaction.isActive()) {
            TestTransaction.start();
        }

        // Delete all but the default Subsystem Types
        final List<SubsystemType> subsystemTypesToDelete = subsystemTypeRepository.findByTypeNotIn(PredefinedSubsystemType.toSet());
        subsystemTypeRepository.deleteAll(subsystemTypesToDelete);

        // Completely purge all other relevant repos (note: order matters!)
        connectionPropertiesRepository.deleteAll();
        subsystemRepository.deleteAll();

        TestTransaction.flagForCommit();
        TestTransaction.end();
        log.trace("--> Purged all repos.");
    }

}
