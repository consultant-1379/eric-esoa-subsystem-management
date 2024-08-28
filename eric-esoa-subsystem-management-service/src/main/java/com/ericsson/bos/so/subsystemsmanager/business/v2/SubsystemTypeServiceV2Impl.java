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
package com.ericsson.bos.so.subsystemsmanager.business.v2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ericsson.bos.so.subsystemsmanager.business.SubsystemTypeServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemTypeServiceV2;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemSubtypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeAlreadyExistsException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeAlreadyExistsException;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeRequest;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemTypeServiceV2Impl.
 */
@Service
@Slf4j
public class SubsystemTypeServiceV2Impl extends SubsystemTypeServiceImpl implements SubsystemTypeServiceV2 {

    private static final String NAME = "name";

    private static final String TYPE = "type";

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    @Autowired
    private SubsystemSubtypeDao subsystemSubtypeDao;

    @Autowired
    private SubsystemValidator subsystemValidator;

    /**
     * Post subsystem type.
     *
     * @param request the SubsystemTypeRequest
     * @return the subsystem type response
     */
    @Override
    public SubsystemTypeResponse postSubsystemType(SubsystemTypeRequest request) {
        log.info("Creating Subsystem Type: {}...", request);

        validatePostSubsystemTypeRequest(request);

        final SubsystemType subsystemType = SubsystemType.builder().type(request.getType()).subtypes(request.getSubtypes()).alias(request.getAlias())
                .build();
        final SubsystemTypeResponse persistedSubsystemType = buildSubsystemType(subsystemTypeDao.saveSubsystemType(subsystemType));

        log.info("'{}' created.", persistedSubsystemType);
        return persistedSubsystemType;
    }

    /**
     * Post subsystem subtype.
     *
     * @param subsystemTypeId the subsystem type id
     * @param subtype the subtype
     * @return the list
     */
    @Override
    public List<Subtype> postSubsystemSubtype(Long subsystemTypeId, Subtype subtype) {
        log.info("Creating Subsystem Subtype: subtypes {} and subsystemTypeId {}...", subtype, subsystemTypeId);
        validatePostSubtypeRequest(subsystemTypeId, subtype);
        final List<Subtype> persistedSubsystemSubtype = subsystemTypeDao.saveSubsystemSubtype(subsystemTypeId, subtype);
        log.info("'{}' created.", persistedSubsystemSubtype);
        return persistedSubsystemSubtype;
    }

    /**
     * Gets the subsystem types.
     *
     * @return the subsystem types
     */
    @Override
    public List<SubsystemTypeResponse> getSubsystemTypes() {
        log.info("Reading all subsystem types...");
        return buildSubsystemTypes(subsystemTypeDao.findAll());
    }

    /**
     * Gets the subsystem types by type.
     *
     * @param subsystemType the subsystem type
     * @return the subsystem types by type
     */
    @Override
    public List<SubsystemTypeResponse> getSubsystemTypesByType(String subsystemType) {
        log.info("Reading subsystem types of [{}] subsystemType...", subsystemType);
        final Stream<SubsystemType> subsystemTypes = Stream.of(subsystemTypeDao.findByType(subsystemType));
        return buildSubsystemTypes(subsystemTypes.collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Delete subsystem subtype.
     *
     * @param subsystemTypeId the subsystem type id
     * @param subsystemSubtypeId the subsystem subtype id
     */
    @Override
    public void deleteSubsystemSubtype(Long subsystemTypeId, Long subsystemSubtypeId) {
        log.info("Deleting Subsystem Subtype with subsystemTypeId [{}] and subsystemSubtypeId [{}]...", subsystemTypeId, subsystemSubtypeId);
        subsystemTypeDao.deleteSubsystemSubtype(subsystemTypeId, subsystemSubtypeId);
        log.info("Deleted Subsystem Subtype with subsystemTypeId [{}] and subsystemSubtypeId [{}]...", subsystemTypeId, subsystemSubtypeId);
    }

    /**
     * Builds the subsystem types.
     *
     * @param subsystemTypes the subsystem types
     * @return the list
     */
    private List<SubsystemTypeResponse> buildSubsystemTypes(List<SubsystemType> subsystemTypes) {
        return subsystemTypes.stream().map(subsystemType -> buildSubsystemType(subsystemType)).collect(Collectors.toList());
    }

    /**
     * Builds the subsystem type.
     *
     * @param subsystemType the subsystem type
     * @return the subsystem type response
     */
    private SubsystemTypeResponse buildSubsystemType(SubsystemType subsystemType) {
        return SubsystemTypeResponse.builder().id(subsystemType.getId()).type(subsystemType.getType()).alias(subsystemType.getAlias())
                .subtypes(subsystemType.getSubtypes()).build();
    }

    /**
     * Validate post subsystem type request.
     *
     * @param request the request
     */
    private void validatePostSubsystemTypeRequest(SubsystemTypeRequest request) {
        log.info("Validating Post subsystem type request '{}'...", request);
        if (!StringUtils.hasText(request.getType())) {
            throw new MalformedContentException("SSM-B-25", TYPE);
        }

        if (subsystemTypeDao.subsystemTypeExists(request.getType())) {
            throw new SubsystemTypeAlreadyExistsException(request.getType());
        }

        final List<Subtype> subtypeList = request.getSubtypes();

        if (!CollectionUtils.isEmpty(subtypeList)) {

            subtypeList.stream().filter(subtype -> !StringUtils.hasText(subtype.getName())).findFirst().ifPresent(a -> {
                throw new MalformedContentException("SSM-B-48", NAME);
            });

            final Set<String> duplicateSubtypes = new HashSet<>();
            final Optional<Subtype> subsystemSubtype = subtypeList.stream()
                    .filter(duplicateSubtype -> !duplicateSubtypes.add(duplicateSubtype.getName()))
                    .collect(Collectors.toSet()).stream().findFirst();
            if (subsystemSubtype.isPresent()) {
                throw new MalformedContentException("SSM-B-49", subsystemSubtype.get().getName());
            }

            final List<Subtype> subtypes = subsystemSubtypeDao.getSubsystemSubtypes();
            subtypes.forEach(subtype -> request.getSubtypes().stream()
                    .filter(subsystemSubtypes -> subtype.getName().equals(subsystemSubtypes.getName())).findAny().ifPresent(a -> {
                        throw new SubsystemSubtypeAlreadyExistsException(subtype.getName());
                    }));
        }
    }

    /**
     * Validate post subtype request.
     *
     * @param subsystemTypeId the subsystem type id
     * @param subtype the subtype
     */
    private void validatePostSubtypeRequest(Long subsystemTypeId, Subtype subtype) {
        log.info("Validating Post subsystem subtype request subsystemTypeId '{}' and subtype '{}'...", subsystemTypeId, subtype);

        subsystemValidator.isSubsystemTypeIdValid(subsystemTypeId);

        if (!StringUtils.hasText(subtype.getName())) {
            throw new MalformedContentException("SSM-B-48", NAME);
        }

        final List<Subtype> subtypes = subsystemSubtypeDao.getSubsystemSubtypes();
        if (!CollectionUtils.isEmpty(subtypes)) {
            subtypes.stream().filter(subsystemSubtype -> subtype.getName().equals(subsystemSubtype.getName())).findAny().ifPresent(a -> {
                throw new SubsystemSubtypeAlreadyExistsException(subtype.getName());
            });
        }
    }
}