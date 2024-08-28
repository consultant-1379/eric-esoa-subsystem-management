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
package com.ericsson.bos.so.subsystemsmanager.business;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.bos.so.subsystemsmanager.business.api.ConnectionPropsService;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.ConnectionPropertiesDao;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.business.util.PatchUtil;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The class ConnectionPropsServiceImpl
 */
@Service
public class ConnectionPropsServiceImpl implements ConnectionPropsService {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPropsServiceImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SubsystemRequestValidator subsystemRequestValidator;

    @Autowired
    private SubsystemValidator subsystemValidator;

    @Autowired
    private ConnectionPropertiesValidator connectionPropertiesValidator;

    @Autowired
    private ConnectionPropertiesDao connectionPropertiesDao;

    @Autowired
    private SubsystemRepository subsystemRepository;

    @Autowired
    private PatchUtil patchUtil;

    @Autowired
    private EncryptDecryptConnectionPropertiesUtil encryptDecryptConnectionPropertiesUtil;

    @Override
    public ConnectionProperties postConnProp(final String subsystemId, final ConnectionProperties connectionProperties) {
        subsystemRequestValidator.isValidConnPropsPostRequest(connectionProperties, subsystemId);
        LOG.info("postConnProp() request received for connection properties with id : {}", connectionProperties.getSubsystemId());
        encryptDecryptConnectionPropertiesUtil.encryptProperties(connectionProperties);
        return connectionPropertiesDao.saveConnectionProperties(connectionProperties);
    }

    @Override
    public ConnectionProperties patchConnProps(final String subsystemId, final String connectionPropertiesId,
                                               final Map<String, Object> patchRequestFields) {
        LOG.info("patchConnProps() request received for connection properties with id : {}", connectionPropertiesId);
        final ConnectionProperties currentConnProps = connectionPropertiesValidator.isValidConnProps(connectionPropertiesId, subsystemId,
                patchRequestFields);
        if (patchRequestFields.containsKey("subsystemId")) {
            final Object subsystemField = patchRequestFields.get("subsystemId");
            subsystemValidator.isSubsystemIdValid(subsystemField.toString());
        }
        final ConnectionProperties patchedConnProps = applyPatchConnProp(subsystemId, connectionPropertiesId, patchRequestFields, currentConnProps);
        encryptDecryptConnectionPropertiesUtil.encryptProperties(patchedConnProps);
        return connectionPropertiesDao.saveConnectionProperties(patchedConnProps);
    }

    @Override
    public ConnectionProperties putConnProps(final String subsystemId, final String connPropsId, final ConnectionProperties connectionProperties) {
        LOG.info("putConnProps() request received for connection properties with id : {}", connectionProperties.getSubsystemId());
        connectionPropertiesValidator.validateConnectionProperty(connectionProperties, subsystemId);
        final ConnectionProperties connectionPropertyToChange = connectionPropertiesValidator.isValidConnProps(connPropsId, subsystemId);
        connectionPropertyToChange.setProperties(connectionProperties.getProperties());
        connectionPropertyToChange.setEncryptedKeys(connectionProperties.getEncryptedKeys());
        encryptDecryptConnectionPropertiesUtil.encryptProperties(connectionPropertyToChange);
        return connectionPropertiesDao.saveConnectionProperties(connectionPropertyToChange);
    }

    @Override
    public List<ConnectionProperties> getConnPropsBySubsystemId(final String subsystemId) {
        subsystemValidator.isSubsystemIdValid(subsystemId);
        return encryptDecryptConnectionPropertiesUtil.encryptOrDecryptProperties(connectionPropertiesDao.findConnPropsBySubsystemId(subsystemId),
                EncryptDecryptConnectionPropertiesUtil.DECRYPT);
    }

    @Override
    public ConnectionProperties getConnPropsById(final String subsystemId, final String connectionPropertiesId) {
        encryptDecryptConnectionPropertiesUtil.decryptProperties(connectionPropertiesValidator.isValidConnProps(connectionPropertiesId, subsystemId));
        return connectionPropertiesValidator.isValidConnProps(connectionPropertiesId, subsystemId);
    }

    @Override
    public void deleteConnProps(final String subsystemId, final String connectionPropertiesId) {
        connectionPropertiesValidator.isValidConnProps(connectionPropertiesId, subsystemId);
        connectionPropertiesDao.deleteConnPropsById(connectionPropertiesId);
    }

    private ConnectionProperties applyPatchConnProp(final String subsystemId, final String connectionPropertiesId,
                                                    final Map<String, Object> patchRequestFields, final ConnectionProperties foundConnProps) {
        Map<String, Object> connPropsMap = objectMapper.convertValue(foundConnProps, new TypeReference<Map<String, Object>>() {
        });
        connPropsMap = patchUtil.applyPatchForConn(connPropsMap, patchRequestFields);
        final ConnectionProperties connProps = objectMapper.convertValue(connPropsMap, ConnectionProperties.class);
        connProps.setSubsystemId(Long.valueOf(subsystemId));
        connProps.setId(Long.valueOf(connectionPropertiesId));
        return connProps;
    }

}
