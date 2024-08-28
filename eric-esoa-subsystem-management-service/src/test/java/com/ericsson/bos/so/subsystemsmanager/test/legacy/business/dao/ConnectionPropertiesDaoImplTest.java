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

import com.ericsson.bos.so.subsystemsmanager.business.dao.ConnectionPropertiesDaoImpl;
import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemsManagerException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class ConnectionPropertiesDaoImplTest.
 */
public class ConnectionPropertiesDaoImplTest {

    static ConnectionProperties connectionProperties;
    static List<ConnectionProperties> listOfConnectionProperties = new ArrayList<>();
    @InjectMocks
    ConnectionPropertiesDaoImpl connectionPropertiesDaoImpl;

    @Mock
    private ConnectionPropertiesRepository connectionPropertiesRepository;
    @Mock
    private ConnectionPropertiesValidator connectionPropertiesValidator;
    @Mock
    private SubsystemRequestValidator subsystemRequestValidator;



    /**
     * Initialize.
     */
    @Before
    public void initialize() {

        MockitoAnnotations.openMocks(this);

        final SubsystemUser subsystemUser = new SubsystemUser();

        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);

        connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystemUsers(usersSet);
        connectionProperties.setId(21111L);
        connectionProperties.setSubsystemId(11L);

        listOfConnectionProperties.add(connectionProperties);

    }

    /**
     * Test to save connection properties.
     */
    @Test
    public void testSaveConnectionProperties() {
        connectionPropertiesDaoImpl.saveConnectionProperties(connectionProperties);
        verify(connectionPropertiesRepository, times(1)).save(connectionProperties);

    }

    /**
     * Test to find connection properties by id.
     */
    @Test
    public void testFindConnPropsById() {
        try {
            when(connectionPropertiesValidator.isConnPropsIdValid("21111L")).thenReturn(connectionProperties);

            when(connectionPropertiesRepository.findById(Long.valueOf(connectionProperties.getId()))).thenReturn(Optional.of(connectionProperties));

            assertEquals(connectionProperties, connectionPropertiesDaoImpl.findConnPropsById(String.valueOf(connectionProperties.getId())));
        } catch (SubsystemsManagerException ex) {
            throw ex;
        }
    }

    /**
     * Test to find connection properties by id exception.
     */
    @Test(expected = ConnectionPropertiesDoesNotExistException.class)
    public void testFindConnPropsByIdException() {
        when(connectionPropertiesValidator.isConnPropsIdValid("21111L")).thenReturn(connectionProperties);

        when(connectionPropertiesRepository.findById(Long.valueOf(connectionProperties.getId()))).thenReturn(Optional.of(connectionProperties));
        connectionPropertiesDaoImpl.findConnPropsById("10");
    }

    /**
     * Test to find connection properties by subsystem id.
     */
    @Test
    public void testFindConnPropsBySubsystemId() {
        try {
            final ConnectionProperties connectionPropertiesTest = new ConnectionProperties();
            connectionPropertiesTest.setSubsystemId(Long.valueOf(connectionProperties.getSubsystemId()));
            final Example<ConnectionProperties> connPropExample = Example.of(connectionPropertiesTest);
            when(connectionPropertiesRepository.findAll(connPropExample)).thenReturn(listOfConnectionProperties);
            connectionPropertiesDaoImpl.findConnPropsBySubsystemId(connectionPropertiesTest.getSubsystemId().toString());
            assertEquals(listOfConnectionProperties, connectionPropertiesRepository.findAll(connPropExample));
        } catch (SubsystemsManagerException ex) {
            throw ex;
        }
    }

    /**
     * Test to delete connection properties by id when valid id is given.
     */
    @Test
    public void testDeleteConnPropsById_whenValidIdIsGiven(){
        try {
            final String connectionPropertiesId = "21111";
            connectionPropertiesDaoImpl.deleteConnPropsById(connectionPropertiesId);
            verify(connectionPropertiesRepository, times(1)).deleteById(Long.valueOf(connectionPropertiesId));
        } catch (SubsystemsManagerException ex) {
            throw ex;
        }
    }
}
