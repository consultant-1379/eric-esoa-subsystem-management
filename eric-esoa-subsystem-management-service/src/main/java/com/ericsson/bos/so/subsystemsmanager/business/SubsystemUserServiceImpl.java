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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemUserService;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemUserDao;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The class SubsystemUserServiceImpl
 */
@Service
public class SubsystemUserServiceImpl implements SubsystemUserService {

    private static final Logger LOG = LoggerFactory.getLogger(SubsystemUserServiceImpl.class);

    @Autowired
    private ConnectionPropertiesValidator connectionPropertiesValidator;
    @Autowired
    private SubsystemValidator subsystemValidator;
    @Autowired
    private SubsystemUserDao subsystemUserDao;

    @Override
    public SubsystemUser postUserByConnsPropId(final String subsystemId, final String connectionPropertiesId) {
        LOG.info("postUserByConnPropId() request received for connection property id: {}", connectionPropertiesId);
        connectionPropertiesValidator.isValidConnProps(connectionPropertiesId, subsystemId);
        final SubsystemUser subsystemUserRequest = new SubsystemUser();
        subsystemUserRequest.setConnectionPropsId(Long.valueOf(connectionPropertiesId));
        return subsystemUserDao.saveSubsystemUser(subsystemUserRequest);
    }

    @Override
    public void deleteSubsystemUserById(final String subsystemId, final String connectionPropertiesId, final String subsystemUserId) {
        LOG.info("deleteSubsystemUserById() request received for subsystemUserId id : {}", subsystemUserId);
        connectionPropertiesValidator.isValidSubsystemUser(connectionPropertiesId, subsystemId, subsystemUserId);
        subsystemUserDao.deleteSubsystemUserById(Long.valueOf(subsystemUserId));
    }

}
