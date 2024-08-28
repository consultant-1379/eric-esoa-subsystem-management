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
package com.ericsson.bos.so.subsystemsmanager.business.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.ConnectionPropertiesDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;

/**
 * The class ConnectionPropertiesDaoImpl
 */
@Component
public class ConnectionPropertiesDaoImpl implements ConnectionPropertiesDao {

    @Autowired
    private SubsystemRequestValidator subsystemRequestValidator;
    @Autowired
    private ConnectionPropertiesValidator connectionPropertiesValidator;

    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    @Override
    public ConnectionProperties saveConnectionProperties(final ConnectionProperties connectionProperties) {
        return connectionPropertiesRepository.save(connectionProperties);
    }

    @Override
    public ConnectionProperties findConnPropsById(final String connPropsId) {
        connectionPropertiesValidator.isConnPropsIdValid(connPropsId);
        final ConnectionProperties connectionProperties;
        final Optional<ConnectionProperties> foundConnPropsOpt = connectionPropertiesRepository.findById(Long.valueOf(connPropsId));
        if (foundConnPropsOpt.isPresent()) {
            connectionProperties = foundConnPropsOpt.get();
        } else {
            throw new ConnectionPropertiesDoesNotExistException(connPropsId);
        }
        return connectionProperties;
    }

    @Override
    public List<ConnectionProperties> findConnPropsBySubsystemId(final String subsystemId) {
        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setSubsystemId(Long.valueOf(subsystemId));
        final Example<ConnectionProperties> connPropExample = Example.of(connectionProperties);
        return connectionPropertiesRepository.findAll(connPropExample);
    }

    @Override
    public void deleteConnPropsById(final String connectionPropertiesId) {
        connectionPropertiesValidator.isValidConnProps(connectionPropertiesId);
        connectionPropertiesRepository.deleteById(Long.valueOf(connectionPropertiesId));
    }

}
