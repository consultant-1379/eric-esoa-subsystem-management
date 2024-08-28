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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemUserDao;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemUserRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The class SubsystemUserDaoImpl
 */
@Component
public class SubsystemUserDaoImpl implements SubsystemUserDao {

    @Autowired
    SubsystemUserRepository subsystemUserRepository;

    @Override
    public SubsystemUser saveSubsystemUser(final SubsystemUser subsystemUserRequest) {
        return subsystemUserRepository.save(subsystemUserRequest);
    }

    @Override
    public void deleteSubsystemUserById(final Long subsystemUserId) {
        subsystemUserRepository.deleteById(subsystemUserId);
    }

}
