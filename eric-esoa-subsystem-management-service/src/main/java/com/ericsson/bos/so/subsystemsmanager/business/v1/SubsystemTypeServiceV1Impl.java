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
package com.ericsson.bos.so.subsystemsmanager.business.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ericsson.bos.so.subsystemsmanager.business.SubsystemTypeServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemTypeServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeAlreadyExistsException;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemTypeCategory;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.SubsystemTypeRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemTypeServiceV1Impl.
 */
@Service
@Slf4j
public class SubsystemTypeServiceV1Impl extends SubsystemTypeServiceImpl implements SubsystemTypeServiceV1 {

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    /**
     * Post subsystem type.
     *
     * @param request the SubsystemTypeRequest
     * @return the subsystem type
     */
    @Override
    public SubsystemType postSubsystemType(SubsystemTypeRequest request) {
        log.info("Creating Subsystem Type: {}...", request);
        validatePostRequest(request);

        final SubsystemType subsystemType = SubsystemType.builder().type(request.getType()).build();
        final SubsystemType persistedSubsystemType = subsystemTypeDao.saveSubsystemType(subsystemType);

        persistedSubsystemType.setAlias(null);
        persistedSubsystemType.setSubtypes(null);

        log.info("'{}' created.", persistedSubsystemType);
        return persistedSubsystemType;
    }

    /**
     * Gets the subsystem types.
     *
     * @return the subsystem types
     */
    @Override
    public List<SubsystemType> getSubsystemTypes() {
        log.info("Reading all subsystem types...");
        return buildSubsystemTypes(subsystemTypeDao.findAll());
    }

    /**
     * Gets the subsystem types.
     *
     * @param subsystemTypeCategory the subsystem type category
     * @return the subsystem types
     */
    @Override
    public List<SubsystemType> getSubsystemTypes(SubsystemTypeCategory subsystemTypeCategory) {
        log.info("Reading subsystem types of [{}] category...", subsystemTypeCategory);
        final List<SubsystemType> subsystemTypes = subsystemTypeDao.findAll();

        return buildSubsystemTypes(subsystemTypes.stream().filter(subsystemType -> subsystemType.getCategory().equals(subsystemTypeCategory))
                .collect(Collectors.toList()));
    }

    /**
     * Validate post request.
     *
     * @param request the request
     */
    private void validatePostRequest(SubsystemTypeRequest request) {
        log.info("Validating '{}'...", request);
        if (!StringUtils.hasText(request.getType())) {
            throw new MalformedContentException("SSM-B-25", request.getType());
        }

        if (subsystemTypeDao.subsystemTypeExists(request.getType())) {
            throw new SubsystemTypeAlreadyExistsException(request.getType());
        }
    }

    /**
     * Builds the subsystem types.
     *
     * @param subsystemTypes the subsystem types
     * @return the list
     */
    private List<SubsystemType> buildSubsystemTypes(List<SubsystemType> subsystemTypes) {
        subsystemTypes.forEach(subsystemType -> {
            subsystemType.setAlias(null);
            subsystemType.setSubtypes(null);
        });
        return subsystemTypes;
    }

}