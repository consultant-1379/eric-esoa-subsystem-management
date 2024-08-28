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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemTypeCategory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ExpectedResponses.
 */
public class ExpectedResponses {

    private static List<Subsystem> subsystemList;

    /**
     * Gets all subsystems.
     *
     * @return all subsystems
     */
    public static List<Subsystem> getAllSubsystems() {
        subsystemList = new ArrayList<>();

        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(userList);
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(Constants.SUBSYSTEM_NAME_ECM);
        subsystemOnboardingRequest.setConnectionProperties(connectionPropertiesList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 1);

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(1L);
        subsystemType.setType(PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        subsystemType.setCategory(SubsystemTypeCategory.PRIMARY);
        subsystemOnboardingRequest.setSubsystemType(subsystemType);

        subsystemList.add(subsystemOnboardingRequest);
        return subsystemList;
    }

    /**
     * Gets all connection properties.
     *
     * @return all connection properties
     */
    public static List<ConnectionProperties> getAllConnProps() {
        subsystemList = getAllSubsystems();
        return subsystemList.get(0).getConnectionProperties();
    }

    /**
     * Gets the connection properties.
     *
     * @return the connection properties
     */
    public static ConnectionProperties getConnProp() {
        subsystemList = getAllSubsystems();
        return subsystemList.get(0).getConnectionProperties().get(0);
    }

    /**
     * Gets filtered id's.
     *
     * @param id
     *            the id
     * @return the filtered ids
     */
    public static List<Long> getFilteredIds(Long id) {
        final List<Long> list = new ArrayList<>();
        list.add(id);
        return list;
    }

    /**
     * Gets the filtered id and name.
     *
     * @param id
     *            the id
     * @return the filtered id and name
     */
    public static List<Subsystem> getFilteredIdAndName(Long id) {
        final List<Subsystem> subsystemList = new ArrayList<>();
        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setId(id);
        subsystemOne.setName(Constants.SUBSYSTEM_NAME_ECM);
        subsystemList.add(subsystemOne);
        return subsystemList;
    }

}
