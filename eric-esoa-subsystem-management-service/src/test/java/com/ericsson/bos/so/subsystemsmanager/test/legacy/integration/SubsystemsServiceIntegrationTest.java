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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.DOMAIN_MANAGER;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.NFVO;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_TYPE_FILTER_NAME;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.TENANT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.TENANT_QUERY_PARAM;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.VENDOR;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.Constants.ADAPTER_LINK_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.Constants.BAD_SUBSYSTEM_TYPE;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.Constants.URL;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.Constants.VENDOR_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemsServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import org.springframework.boot.test.mock.mockito.MockBean;


/**
 * Legacy test; to be removed when pagination using specification is implemented.
 */
@Deprecated
public class SubsystemsServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    SubsystemsServiceV1 subsystemsService;

    @Autowired
    SubsystemRepository subsystemRepository;

    @Autowired
    SubsystemTypeRepository subsystemTypeRepository;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * Inits the database.
     */
    @Before
    public void initDb() {
        createAndPersistUniqueSubsystems();
    }

    /**
     * Teardown.
     */
    @After
    public void teardown() {
        subsystemRepository.deleteAll();
    }

    /**
     * When fetch subsystem by subsystem type and vendor then correct fields are returned.
     */
    @Test
    public void when_fetchSubsystemBySubsystemTypeAndVendor_thenCorrectFieldsAreReturned() {
        final Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(SUBSYSTEM_TYPE_FILTER_NAME, NFVO);
        filterMap.put(VENDOR, VENDOR_NAME);
        filterMap.put(TENANT_QUERY_PARAM, TENANT_NAME);
        final List<Subsystem> filteredSubsystems = subsystemsService.fetchSubsystemByQuery(TENANT_NAME, filterMap);
        assertNotNull(filteredSubsystems);
        assertTrue(filteredSubsystems.stream().allMatch(subsystem -> NFVO.equalsIgnoreCase(subsystem.getSubsystemType().getType())));
        assertTrue(filteredSubsystems.stream().allMatch(subsystem -> VENDOR_NAME.equalsIgnoreCase(subsystem.getVendor())));
        assertTrue(filteredSubsystems.stream().allMatch(subsystem -> ADAPTER_LINK_NAME.equalsIgnoreCase(subsystem.getAdapterLink())));
    }

    /**
     * When fetch subsystem by subsystem non existing type then no subsystems are returned.
     */
    @Test
    public void when_fetchSubsystemBySubsystemNonExistingType_thenNoSubsystemsAreReturned() {
        final Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(SUBSYSTEM_TYPE_FILTER_NAME, BAD_SUBSYSTEM_TYPE);
        filterMap.put(VENDOR, VENDOR_NAME);
        filterMap.put(TENANT_QUERY_PARAM, TENANT_NAME);
        final List<Subsystem> filteredSubsystems = subsystemsService.fetchSubsystemByQuery(TENANT_NAME, filterMap);
        assertNotNull(filteredSubsystems);
        assertTrue(filteredSubsystems.isEmpty());
    }

    /**
     * Creates and persist unique subsystems.
     */
    private void createAndPersistUniqueSubsystems() {
        final List<Subsystem> subsystems = new ArrayList<>();
        subsystems.add(createSubsystem(PRIMARY_SUBSYSTEM, NFVO, VENDOR_NAME, ADAPTER_LINK_NAME));
        subsystems.add(createSubsystem(SECONDARY_SUBSYSTEM, DOMAIN_MANAGER, VENDOR_NAME, ""));
        subsystemRepository.saveAll(subsystems);
        subsystemRepository.flush();
    }

    /**
     * Creates the subsystem.
     *
     * @param subsystemName the subsystem name
     * @param subsystemType the subsystem type
     * @param vendorName the vendor name
     * @param adapterLink the adapter link
     * @return the subsystem
     */
    private Subsystem createSubsystem(String subsystemName, String subsystemType, String vendorName, String adapterLink) {
        final Optional<SubsystemType> existingSubsystemType = subsystemTypeRepository.findByType(subsystemType);
        final Subsystem subsystem = new Subsystem();
        subsystem.setName(subsystemName.isEmpty() ? SECONDARY_SUBSYSTEM : subsystemName);
        subsystem.setSubsystemType(existingSubsystemType.orElseGet(() -> createNewUniqueSubsystemType(subsystemType)));
        subsystem.setUrl(URL);
        subsystem.setConnectionProperties(createGenericConnectionProperties());
        subsystem.setOperationalState(OperationalState.REACHABLE);
        subsystem.setVendor(vendorName);
        subsystem.setAdapterLink(adapterLink);
        return subsystem;
    }

    /**
     * Creates the generic connection properties.
     *
     * @return the list
     */
    private List<ConnectionProperties> createGenericConnectionProperties() {
        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        connectionProperties.setSubsystemUsers(createSubsystemUserList());
        return Arrays.asList(connectionProperties);
    }

    /**
     * Creates the subsystem user list.
     *
     * @return the list
     */
    private List<SubsystemUser> createSubsystemUserList() {
        return Arrays.asList(new SubsystemUser());
    }

    /**
     * Creates new unique subsystem type.
     *
     * @param subsystemType the subsystem type
     * @return the subsystem type
     */
    private SubsystemType createNewUniqueSubsystemType(String subsystemType) {
        final SubsystemType newSubsystemType = new SubsystemType();
        newSubsystemType.setType(subsystemType);
        return newSubsystemType;
    }

}
