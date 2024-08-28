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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemSubtypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemSubtypeRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

/**
 * The class SubsystemSubtypeDaoImpl
 */
@Component
@Slf4j
public class SubsystemSubtypeDaoImpl implements SubsystemSubtypeDao {

    private static final String SSM_J_51 = "SSM-J-51";

    @Autowired
    private SubsystemSubtypeRepository subsystemSubtypeRepository;

    @Override
    public List<Subtype> getSubsystemSubtypes() {
        return subsystemSubtypeRepository.findAll();
    }

    @Override
    public Subtype findByName(String name) {
        return subsystemSubtypeRepository.findByName(name).orElseThrow(() -> new SubsystemSubtypeDoesNotExistException(name, SSM_J_51));
    }

    @Override
    public Subtype findById(Long subtypeId) {
        return subsystemSubtypeRepository.findById(subtypeId).orElseThrow(() -> new SubsystemSubtypeDoesNotExistException(subtypeId, SSM_J_51));
    }

}
