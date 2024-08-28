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
package com.ericsson.bos.so.subsystemsmanager.business.fieldfilter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

/**
 * The class SubsystemFilterServiceImpl
 */
@Service
public class SubsystemFilterServiceImpl implements SubsystemFilterService {


    private static final Logger LOG = LoggerFactory.getLogger(SubsystemFilterServiceImpl.class);

    @Autowired
    protected SubsystemFilterUtil subsystemExtractFieldsUtil;

    @Autowired
    protected EncryptDecryptConnectionPropertiesUtil encryptDecryptConnectionPropertiesUtil;

    @Override
    public List<SubsystemFilterEntity> getAllAsFilterEntities(final List<Subsystem> subsystems, boolean populateSubsystemType) {
        LOG.info("getAllAsFilterEntities() received request to get all as filter entities");
        final List<SubsystemFilterEntity> subsystemFilterEntityList = new ArrayList<>();
        for (final Subsystem subsystem : subsystems) {
            subsystemFilterEntityList.add(mapSubsystemToFilterEntity(subsystem, populateSubsystemType));

            if (!subsystem.getConnectionProperties().isEmpty()) {
                for (final ConnectionProperties connectionProperty : subsystem.getConnectionProperties()) {
                    encryptDecryptConnectionPropertiesUtil.decryptProperties(connectionProperty);
                }
            }
        }
        return subsystemFilterEntityList;
    }

    @Override
    public SubsystemFilterEntity mapSubsystemToFilterEntity(final Subsystem subsystem) {
        return mapSubsystemToFilterEntity(subsystem, false);
    }

    @Override
    public SubsystemFilterEntity mapSubsystemToFilterEntity(final Subsystem subsystem, boolean populateSubsystemType) {
        LOG.info("mapSubsystemToFilterEntity() received request to map subsystem to subsystemFilterEntity");
        final SubsystemFilterEntity subsystemFilterEntity = new SubsystemFilterEntity();
        subsystemFilterEntity.setId(subsystem.getId());
        subsystemFilterEntity.setName(subsystem.getName());
        subsystemFilterEntity.setOperationalState(subsystem.getOperationalState());

        if (populateSubsystemType) {
            subsystemFilterEntity.setSubsystemType(subsystem.getSubsystemType());
            subsystemFilterEntity.setApiKey(subsystem.getApiKey());
        } else {
            subsystemFilterEntity.setSubsystemTypeId(subsystem.getSubsystemTypeId());
        }

        subsystemFilterEntity.setUrl(subsystem.getUrl());
        subsystemFilterEntity.setConnectionProperties(subsystem.getConnectionProperties());
        subsystemFilterEntity.setHealthCheckTime(subsystem.getHealthCheckTime());
        subsystemFilterEntity.setVendor(subsystem.getVendor());
        return subsystemFilterEntity;
    }
}
