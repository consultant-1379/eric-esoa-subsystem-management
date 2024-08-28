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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ericsson.bos.so.subsystemsmanager.business.dao.SubsystemDaoImpl;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * The Class SubsystemDaoImplTest.
 */
public class SubsystemDaoImplTest {

    public static final Long _1 = Long.valueOf(1);
    public static final String TEST_NAME = "test-name";
    public static final String TEST_URL = "test-url";
    public static final String _15_00_00 = "15:00:00";
    static Subsystem subsystem;
    static List<Subsystem> listOfSubsystems;
    private static final Integer OFFSET = null;
    private static final Integer LIMIT = null;
    private static final String SORT_ATTR = null;
    private static final String SORT_DIR = null;

    @Mock
    private SubsystemRepository subsystemRepository;

    @InjectMocks
    private SubsystemDaoImpl subsystemDaoImpl;

    /**
     * Initialize.
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(_1);
        subsystemType.setType(PredefinedSubsystemType.NFVO.getType());

        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(_1);
        subsystemUser.setConnectionPropsId(_1);
        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setId(_1);
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystemUsers(usersSet);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        subsystem = new Subsystem();
        subsystem.setId(1L);
        subsystem.setConnectionProperties(connPropsList);
        subsystem.setHealthCheckTime(_15_00_00);
        subsystem.setName(TEST_NAME);
        subsystem.setOperationalState(OperationalState.REACHABLE);
        subsystem.setUrl(TEST_URL);
        subsystem.setSubsystemTypeId(subsystemType.getId());
        subsystem.setSubsystemType(subsystemType);

        listOfSubsystems = new ArrayList<>();
        listOfSubsystems.add(subsystem);
    }


    /**
     * Get all the subsystems test.
     */
    @Test
    public void getAllSubsystemsTest() {
        subsystemRepository.findAll();
        verify(subsystemRepository, times(1)).findAll();
    }

    /**
     * Save subsystem test.
     */
    @Test
    public void saveSubsystemTest() {
        subsystemRepository.save(subsystem);
        verify(subsystemRepository, times(1)).save(subsystem);
    }

    /**
     * Find subsystem by id test.
     */
    @Test
    public void findSubsystemByIdTest() {
        when(subsystemRepository.findById(subsystem.getId())).thenReturn(Optional.of(subsystem));
        final Optional<Subsystem> foundSubsystemOpt = subsystemRepository.findById(subsystem.getId());
        assertEquals(foundSubsystemOpt, Optional.of(subsystem));
    }

    /**
     * Delete subsystem by id test.
     */
    @Test
    public void deleteSubsystemByIdTest() {
        subsystemRepository.deleteById(subsystem.getId());
        verify(subsystemRepository, times(1)).deleteById(subsystem.getId());
    }

    /**
     * Given set up when filter available then filter based on id.
     */
    @Test
    public void givenSetUp_whenFilterAvailable_thenFilterBasedOnId() {
        when(subsystemDaoImpl.getAllSubsystems()).thenReturn(listOfSubsystems);
        final String json = "{\"id\":\"1\"}";
        final SubsystemList result = subsystemDaoImpl.getFullPaginatedSubsystems(OFFSET, LIMIT, SORT_ATTR, SORT_DIR, json);
        assertEquals(1, result.getTotal());
    }

    /**
     * Given set up when filter available then filter based on name.
     */
    @Test
    public void givenSetUp_whenFilterAvailable_thenFilterBasedOnName() {
        when(subsystemDaoImpl.getAllSubsystems()).thenReturn(listOfSubsystems);
        final String json = "{\"name\":\"test-name\"}";
        final SubsystemList result = subsystemDaoImpl.getFullPaginatedSubsystems(OFFSET, LIMIT, SORT_ATTR, SORT_DIR, json);
        assertEquals(1, result.getTotal());
    }

    /**
     * Given set up when filter available then filter based on health check time.
     */
    @Test
    public void givenSetUp_whenFilterAvailable_thenFilterBasedOnHealthCheckTime() {
        when(subsystemRepository.findAll()).thenReturn(listOfSubsystems);
        final String json = "{\"healthCheckTime\":\"15:00:00\"}";
        final SubsystemList result = subsystemDaoImpl.getFullPaginatedSubsystems(OFFSET, LIMIT, SORT_ATTR, SORT_DIR, json);
        assertEquals(1, result.getTotal());
    }

    /**
     * Given set up when filter available then filter based on url.
     */
    @Test
    public void givenSetUp_whenFilterAvailable_thenFilterBasedOnUrl() {
        when(subsystemDaoImpl.getAllSubsystems()).thenReturn(listOfSubsystems);
        final String json = "{\"url\":\"test-url\"}";
        final SubsystemList result = subsystemDaoImpl.getFullPaginatedSubsystems(OFFSET, LIMIT, SORT_ATTR, SORT_DIR, json);
        assertEquals(1, result.getTotal());
    }

    /**
     * Given set up when filter available then filter based on operational state.
     */
    @Test
    public void givenSetUp_whenFilterAvailable_thenFilterBasedOnOperationalState() {
        when(subsystemDaoImpl.getAllSubsystems()).thenReturn(listOfSubsystems);
        final String json = "{\"operationalState\":\"REACHABLE\"}";
        final SubsystemList result = subsystemDaoImpl.getFullPaginatedSubsystems(OFFSET, LIMIT, SORT_ATTR, SORT_DIR, json);
        assertEquals(1, result.getTotal());
    }

    /**
     * Given set up when filter available then filter based on id and name.
     */
    @Test
    public void givenSetUp_whenFilterAvailable_thenFilterBasedOnIdAndName() {
        when(subsystemDaoImpl.getAllSubsystems()).thenReturn(listOfSubsystems);
        final String json = "{\"id\":\"1\",\"name\":\"test-name\"}";
        final SubsystemList result = subsystemDaoImpl.getFullPaginatedSubsystems(OFFSET, LIMIT, SORT_ATTR, SORT_DIR, json);
        assertEquals(1, result.getTotal());
    }

}
