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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.steps;

import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class TestUtil.
 */
public class TestUtil {

    @Autowired
    private static SubsystemRepository subsystemRepository;

    /**
     * Builds and persist subsystem entity.
     *
     * @param subsystemName
     *            the subsystem name
     * @return the subsystem
     */
    public static Subsystem buildAndPersistSubsystemEntity(final String subsystemName) {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(userList);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);
        subsystemUser.setConnectionProperties(connectionProperties);
        userList.add(subsystemUser);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(subsystemName);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 1);
        return subsystemRepository.saveAndFlush(subsystemOnboardingRequest);
    }

}
