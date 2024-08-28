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

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemTypeService;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;

import lombok.extern.slf4j.Slf4j;

/**
 * The class SubsystemTypeServiceImpl
 */
@Service
@Slf4j
public class SubsystemTypeServiceImpl implements SubsystemTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemTypeServiceImpl.class);

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    @Override
    public void deleteSubsystemType(long id) {
        LOGGER.info("Deleting Subsystem Type with ID [{}]...", id);
        subsystemTypeDao.deleteSubsystemType(id);
        LOGGER.info("Deleted Subsystem Type with ID [{}].", id);
    }

}