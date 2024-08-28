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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.nbi.entity.relationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.bos.so.subsystemsmanager.business.dbconfig.SubsystemJpaConfig;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * The Class EntityRelationshipVerificationTests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SubsystemJpaConfig.class})
@DataJpaTest
public class EntityRelationshipVerificationTests {

    @Autowired
    private SubsystemRepository subsystemRepository;

    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    private Subsystem savedSubsystem;

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        buildAndPersistSubsystemEntity();
    }

    /**
     * Teardown.
     */
    @After
    public void teardown() {
        subsystemRepository.deleteAll();
    }

    /**
     * When A subsystem is deleted then verify that its associated subsystem type reference was not removed.
     */
    @Test
    public void whenASubsystemIsDeleted_thenVerifyThatItsAssociatedSubsystemTypeReferenceWasNotRemoved() {
        final long expectedCount = subsystemTypeRepository.count();

        subsystemRepository.deleteById(savedSubsystem.getId());
        assertEquals(subsystemTypeRepository.count(), expectedCount);
    }

    /**
     * When A subsystem type is deleted that is referenced by A subsystem the expected exception is thrown.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void whenASubsystemTypeIsDeletedThatIsReferencedByASubsystem_TheExpectedExceptionIsThrown() {
        subsystemTypeRepository.deleteById(1L);
        subsystemTypeRepository.flush();
        assertTrue(subsystemTypeRepository.existsById(1L));
    }

    /**
     * When A subsystem type is deleted that is not referenced by A subsystem then the expected subsystem type is deleted.
     */
    @Test
    public void whenASubsystemTypeIsDeletedThatIsNotReferencedByASubsystem_ThenTheExpectedSubsystemTypeIsDeleted() {
        subsystemTypeRepository.deleteById(2L);
        subsystemTypeRepository.flush();
        assertFalse(subsystemTypeRepository.existsById(2L));
    }

    /**
     * Builds and persist subsystem entity.
     */
    private void buildAndPersistSubsystemEntity() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> userList = new ArrayList<>();
        userList.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystemUsers(userList);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOne = new Subsystem();
        subsystemOne.setConnectionProperties(connPropsList);
        subsystemOne.setHealthCheckTime("test");
        subsystemOne.setName("autobots");
        subsystemOne.setOperationalState(OperationalState.REACHABLE);
        subsystemOne.setUrl("test");
        subsystemOne.setSubsystemTypeId((long) 1);
        savedSubsystem = subsystemRepository.save(subsystemOne);
    }

}
