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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.RecordCannotBeDeletedException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemSubtypeRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemSubtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

/**
 * The class SubsystemTypeDaoImpl
 */
@Component
@Slf4j
public class SubsystemTypeDaoImpl implements SubsystemTypeDao {

    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    @Autowired
    private SubsystemSubtypeRepository subsystemSubtypeRepository;

    @Override
    public SubsystemType saveSubsystemType(SubsystemType subsystemType) {
        subsystemType.resolveCategory();
        return subsystemTypeRepository.save(subsystemType);
    }

    @Override
    public List<Subtype> saveSubsystemSubtype(Long subsystemTypeId, Subtype subtype) {
        final SubsystemType subsystemType = subsystemTypeRepository.findById(subsystemTypeId).get();
        subsystemType.getSubtypes().add(subtype);
        return subsystemTypeRepository.save(subsystemType).getSubtypes();
    }

    @Override
    public void deleteSubsystemType(long id) {
        final SubsystemType subsystemType = subsystemTypeRepository.findById(id).orElseThrow(() -> new SubsystemTypeDoesNotExistException(id));

        if (PredefinedSubsystemType.isPredefinedType(subsystemType.getType())) {
            throw new RecordCannotBeDeletedException(subsystemType.getType());
        }

        subsystemTypeRepository.deleteById(id);
    }

    @Override
    public SubsystemType findById(final long subsystemTypeId) {
        final SubsystemType subsystemType = subsystemTypeRepository.findById(subsystemTypeId)
                .orElseThrow(() -> new SubsystemTypeDoesNotExistException(subsystemTypeId));
        subsystemType.resolveCategory();

        return subsystemType;
    }

    @Override
    public List<SubsystemType> findAll() {
        final List<SubsystemType> subsystemTypes = subsystemTypeRepository.findAll();
        subsystemTypes.forEach(SubsystemType::resolveCategory);

        return subsystemTypes;
    }

    @Override
    public SubsystemType findByType(String type) {
        final SubsystemType subsystemType = subsystemTypeRepository.findByType(type).orElseThrow(() -> new SubsystemTypeDoesNotExistException(type));
        subsystemType.resolveCategory();

        return subsystemType;
    }

    @Override
    public boolean subsystemTypeExists(String subsystemType) {
        return subsystemTypeRepository.existsByType(subsystemType);
    }

    @Override
    public void deleteSubsystemSubtype(Long subsystemTypeId, Long subsystemSubtypeId) {
        final SubsystemType subsystemType = subsystemTypeRepository.findById(subsystemTypeId)
                .orElseThrow(() -> new SubsystemTypeDoesNotExistException(subsystemTypeId));

        final Subtype subsystemSubtype = subsystemSubtypeRepository.findById(subsystemSubtypeId)
                .orElseThrow(() -> new SubsystemSubtypeDoesNotExistException(subsystemTypeId, subsystemSubtypeId));

        if (PredefinedSubsystemSubtype.isPredefinedSubtype(subsystemSubtype.getName())) {
            throw new RecordCannotBeDeletedException(subsystemSubtype.getName());
        }

        final List<Subtype> subtypes = subsystemType.getSubtypes();
        log.debug("subtypes:{}", subtypes);
        if (!CollectionUtils.isEmpty(subtypes)) {
            subtypes.stream().filter(subtype -> subtype.getId().equals(subsystemSubtypeId)).findAny()
                    .orElseThrow(() -> new SubsystemSubtypeDoesNotExistException(subsystemTypeId, subsystemSubtypeId));
            subtypes.removeIf(subtype -> subtype.getId().equals(subsystemSubtypeId));
            subsystemTypeRepository.save(subsystemType);
        } else {
            throw new SubsystemSubtypeDoesNotExistException(subsystemTypeId, subsystemSubtypeId);
        }
    }

}
