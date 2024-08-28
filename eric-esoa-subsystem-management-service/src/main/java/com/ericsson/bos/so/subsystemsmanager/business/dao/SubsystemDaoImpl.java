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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemSubtypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.pagination.SubsystemPaginationUtil;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

/**
 * The class SubsystemDaoImpl
 */
@Component
@Slf4j
public class SubsystemDaoImpl implements SubsystemDao {

    @Autowired
    private SubsystemRepository subsystemRepository;
    @Autowired
    private SubsystemSubtypeDao subsystemSubtypeDao;

    @Override
    public List<Subsystem> getAllSubsystems() {
        final List<Subsystem> subsystems = new ArrayList<>();
        for (Subsystem subsystem : subsystemRepository.findAll()) {
            subsystem.getSubsystemType().resolveCategory();
            final Subsystem resolvedSubsystem = resolveSubsystemType(subsystem);
            subsystems.add(resolvedSubsystem);
        }
        return subsystems;
    }

    @Override
    public List<Subsystem> getAllSubsystems(Example<Subsystem> example) {
        final List<Subsystem> subsystems = new ArrayList<>();
        for (Subsystem subsystem : subsystemRepository.findAll(example)) {
            subsystem.getSubsystemType().resolveCategory();
            final Subsystem resolvedSubsystem = resolveSubsystemType(subsystem);
            subsystems.add(resolvedSubsystem);
        }
        return subsystems;
    }

    @Override
    public Subsystem saveSubsystem(final Subsystem subsystem) {
        subsystem.getSubsystemType().resolveCategory();
        return subsystemRepository.save(subsystem);
    }

    @Override
    public Subsystem findSubsystemByIdWithException(Long subsystemId) {
        final Subsystem subsystem = subsystemRepository.findById(subsystemId)
                .orElseThrow(() -> new SubsystemDoesNotExistException(String.valueOf(subsystemId)));
        subsystem.getSubsystemType().resolveCategory();
        subsystem.getSubsystemType().setSubtype(getSubsytemSubtype(subsystem.getSubsystemSubtypeId()));
        return subsystem;
    }

    @Override
    public Subsystem findSubsystemById(Long subsystemId) {
        final Subsystem subsystem = subsystemRepository.findById(subsystemId).orElse(null);
        if (subsystem != null) {
            subsystem.getSubsystemType().resolveCategory();
            subsystem.getSubsystemType().setSubtype(getSubsytemSubtype(subsystem.getSubsystemSubtypeId()));
        }
        return subsystem;
    }

    @Override
    public void deleteSubsystemById(final Long subsystemId) {
        subsystemRepository.deleteById(subsystemId);
    }

    @Override
    public SubsystemList getFullPaginatedSubsystems(Integer offset, Integer limit, String sortAttr, String sortDir, String filter) {
        log.info("getFullPaginatedSubsystems() is called with offset: {}, limit: {}, sortAttr: {}, sortDir: {}", offset, limit, sortAttr, sortDir);
        final int offsetCheck = (null != offset) ? offset : 0;
        final int limitCheck = (null != limit) ? limit : Integer.MAX_VALUE;
        final String filterJson = (null == filter || filter.isEmpty()) ? "{}" : filter;

        final Comparator<Subsystem> comparator = SubsystemPaginationUtil.getComporatorBySortAttrAndSortDir(sortDir, sortAttr);
        final List<Subsystem> subsystems = getAllSubsystems().stream().filter(e -> SubsystemPaginationUtil.findMatchingFilterObject(e, filterJson))
                .sorted(comparator).skip(offsetCheck).limit(limitCheck).collect(Collectors.toList());

        final SubsystemList subsystemPage = new SubsystemList();
        subsystemPage.setItems(subsystems);
        subsystemPage.setTotal(subsystems.size());

        return subsystemPage;
    }

    @Override
    public List<Subsystem> getFullPaginatedSubsystems(Integer offset, Integer limit, String sortAttr, String sortDir,
                                                      Example<Subsystem> filterCriteria) {
        log.info("getFullPaginatedSubsystems() is called with offset: {}, limit: {}, sortAttr: {}, sortDir: {}", offset, limit, sortAttr, sortDir);
        final int offsetCheck = (null != offset) ? offset : 0;
        final int limitCheck = (null != limit) ? limit : Integer.MAX_VALUE;

        final Comparator<Subsystem> comparator = SubsystemPaginationUtil.getComporatorBySortAttrAndSortDir(sortDir, sortAttr);
        return getAllSubsystems(filterCriteria).stream().sorted(comparator).skip(offsetCheck).limit(limitCheck).collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(final String subsystemName) {
        return subsystemRepository.existsByName(subsystemName);
    }

    @Override
    public long countBySubsystemType(final String subsystemType) {
        return subsystemRepository.countBySubsystemType_type(subsystemType);
    }

    @Override
    public Subsystem findSubsystemByApiKeyWithException(UUID apiKey) {
        final Subsystem subsystem = subsystemRepository.findByApiKey(apiKey).orElseThrow(() -> new SubsystemDoesNotExistException(apiKey.toString()));
        subsystem.getSubsystemType().setSubtype(getSubsytemSubtype(subsystem.getSubsystemSubtypeId()));
        return subsystem;
    }

    @Override
    public Subtype getSubsytemSubtype(Long subtypeId) {
        return !ObjectUtils.isEmpty(subtypeId) ? subsystemSubtypeDao.findById(subtypeId) : null;
    }

    private Subsystem resolveSubsystemType(Subsystem subsystem) {
        final SubsystemType subsystemType = subsystem.getSubsystemType().toBuilder().subtypes(null)
                .subtype(getSubsytemSubtype(subsystem.getSubsystemSubtypeId())).build();
        return subsystem.toBuilder().subsystemType(subsystemType).build();
    }
}
