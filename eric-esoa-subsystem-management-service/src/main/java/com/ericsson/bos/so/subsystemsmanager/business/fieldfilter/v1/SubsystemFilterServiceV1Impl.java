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
package com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v1;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemsServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterEntity;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

/**
 * The class SubsystemFilterServiceV1Impl
 */
@Service
public class SubsystemFilterServiceV1Impl extends SubsystemFilterServiceImpl implements SubsystemFilterServiceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(SubsystemFilterServiceV1Impl.class);

    @Autowired
    private SubsystemsServiceV1 subsystemsServiceV1;

    @Override
    public List<Subsystem> filterResponseFields(final String fields, final String tenantName) {
        LOG.info("filterResponseFields() received for tenantName: {}", tenantName);
        final List<Subsystem> subsystems = subsystemsServiceV1.getAllSubsystems(tenantName);

        final List<SubsystemFilterEntity> subsystemJsonFilterList = super.getAllAsFilterEntities(subsystems, false);
        return subsystemExtractFieldsUtil.extractFieldsFromSubsystemList(fields, subsystemJsonFilterList);
    }

    @Override
    public List<Object> filterResponseSingleField(final String fields, final String tenantName) {
        LOG.info("filterResponseSingleField() received request for tenantName: {}", tenantName);
        final List<Subsystem> subsystemList = subsystemsServiceV1.getAllSubsystems(tenantName);

        if ("connectionProperties".equals(fields)) {
            final List<SubsystemFilterEntity> subsystemJsonFilterList = super.getAllAsFilterEntities(subsystemList, false);
            return subsystemExtractFieldsUtil.extractFieldsFromConnectionProperties(fields, subsystemJsonFilterList);
        } else {
            return subsystemExtractFieldsUtil.extractSingleFieldFromSubsystemList(fields, subsystemList);
        }
    }

    @Override
    public SubsystemFilterEntity getByIdAsFilterEntity(final String subsystemId, final String tenantName) {
        LOG.info("getByIdAsFilterEntity() received request to getIdAsFilterEntity, subsystemId: {}, tenantName: {}", subsystemId, tenantName);
        final Subsystem subsystem = subsystemsServiceV1.getSubsystemById(subsystemId, tenantName);
        return super.mapSubsystemToFilterEntity(subsystem);
    }

    @Override
    public Subsystem filterResponseFields(final String fields, final String subsystemId, final String tenantName) {
        LOG.info("filterResponseFields() received for subsystemId: {}, tenantName: {}", subsystemId, tenantName);
        final SubsystemFilterEntity subsystemJsonFilter = getByIdAsFilterEntity(subsystemId, tenantName);
        return subsystemExtractFieldsUtil.extractFieldsFromSubsystem(fields, subsystemJsonFilter);
    }

    @Override
    public List<Object> filterResponseSingleFieldFromKnownSubsystem(final String field, final String subsystemId, final String tenantName) {
        LOG.info("filterResponseSingleFieldFromKnownSubsystem() received request to filter single field in subsystem with id:{}",
                subsystemId);
        final Subsystem subsystem = subsystemsServiceV1.getSubsystemById(subsystemId, tenantName);
        final List<Subsystem> subsystemList = new LinkedList<>();
        subsystemList.add(subsystem);
        return subsystemExtractFieldsUtil.extractSingleFieldFromSubsystemList(field, subsystemList);
    }
}
