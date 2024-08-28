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
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.bos.so.subsystemsmanager.business.dbconfig.SubsystemJpaConfig;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;

/**
 * The Class DefaultSubsystemTypesVerifactionTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SubsystemJpaConfig.class })
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("integration-test")
public class DefaultSubsystemTypesVerifactionTest {

    public static final int NUMBER_OF_EXPECTED_SUBSYSTEM_TYPES = 9;

    @Autowired
    SubsystemTypeRepository subsystemTypeRepository;

    /**
     * Setup.
     */
    @Before
    public void setUp() {
    }

    /**
     * Teardown.
     */
    @After
    public void teardown() {
    }

    /**
     * When the database is initialized then verify that default subsystem types are created.
     */
    @Test
    public void whenTheDbIsInitialised_ThenVerifyThatDefaultSubsystemTypesAreCreated() {
        final List<SubsystemType> foundTypes = subsystemTypeRepository.findAll();
        assertEquals(NUMBER_OF_EXPECTED_SUBSYSTEM_TYPES, foundTypes.size());
    }

    /**
     * When the database is initialized then verify that NFVO subsystem type is created.
     */
    @Test
    public void whenTheDbIsInitialised_ThenVerifyThatNFVOSubsystemTypeIsCreated() {
        final Optional<SubsystemType> foundTypeOpt = subsystemTypeRepository.findByType("NFVO");
        SubsystemType subsystemType = new SubsystemType();
        if(foundTypeOpt.isPresent()) {
            subsystemType = foundTypeOpt.get();
        }
        assertEquals("NFVO", subsystemType.getType());
    }

    /**
     * When the database is initialized then verify that domain manager subsystem type is created.
     */
    @Test
    public void whenTheDbIsInitialised_ThenVerifyThatDomainManagerSubsystemTypeIsCreated() {
        final Optional<SubsystemType> foundTypeOpt = subsystemTypeRepository.findByType("DomainManager");
        SubsystemType subsystemType = new SubsystemType();
        if(foundTypeOpt.isPresent()) {
            subsystemType = foundTypeOpt.get();
        }
        assertEquals("DomainManager", subsystemType.getType());
    }

    /**
     * When the database initialized then verify that domain orchestrator subsystem type is created.
     */
    @Test
    public void whenTheDbIsInitialised_ThenVerifyThatDomainOrchestratorSubsystemTypeIsCreated() {
        final Optional<SubsystemType> foundTypeOpt = subsystemTypeRepository.findByType("DomainOrchestrator");
        SubsystemType subsystemType = new SubsystemType();
        if(foundTypeOpt.isPresent()) {
            subsystemType = foundTypeOpt.get();
        }
        assertEquals("DomainOrchestrator", subsystemType.getType());
    }
}