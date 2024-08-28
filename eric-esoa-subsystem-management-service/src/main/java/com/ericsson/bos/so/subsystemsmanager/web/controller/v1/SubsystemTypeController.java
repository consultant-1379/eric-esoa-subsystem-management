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
package com.ericsson.bos.so.subsystemsmanager.web.controller.v1;

import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemTypeServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemTypeCategory;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.SubsystemTypeRequest;
import com.ericsson.bos.so.subsystemsmanager.api.subsystem_type.SubsystemTypesApi;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemTypeController.
 */
@Slf4j
@RestController
@RequestMapping(SubsystemController.BASE_PATH)
public class SubsystemTypeController implements SubsystemTypesApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemTypeController.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private SubsystemTypeServiceV1 subsystemTypeServiceV1;

    /**
     * Post subsystem type.
     *
     * @param request the request
     * @return the response entity
     */
    @Override
    public ResponseEntity<SubsystemType> postSubsystemType(@RequestBody SubsystemTypeRequest request) {
        loggerHandler.logAudit(LOGGER, String.format("Received POST Subsystem Type request: [%s]...", request));
        return new ResponseEntity<>(subsystemTypeServiceV1.postSubsystemType(request), HttpStatus.CREATED);
    }

    /**
     * Delete subsystem type.
     *
     * @param id the id
     * @return the response entity
     */
    @Override
    public ResponseEntity<Void> deleteSubsystemType(@PathVariable Long id) {
        loggerHandler.logAudit(LOGGER, String.format("Received DELETE Subsystem Type request for ID: [%s]...", id));
        subsystemTypeServiceV1.deleteSubsystemType(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Gets the subsystem types.
     *
     * @param category the category
     * @return the subsystem types
     */
    @Override
    public ResponseEntity<List<SubsystemType>> getSubsystemTypes(@RequestParam(required = false) String category) {
        loggerHandler.logAudit(LOGGER, String.format("Received GET Subsystem Types request, category: [%s]...", category));
        if (!StringUtils.hasText(category)) {
            return new ResponseEntity<>(subsystemTypeServiceV1.getSubsystemTypes(), HttpStatus.OK);
        }

        if (!EnumUtils.isValidEnumIgnoreCase(SubsystemTypeCategory.class, category)) {
            throw new MalformedContentException("SSM-B-36", category);
        }

        final SubsystemTypeCategory subsystemTypeCategory = EnumUtils.getEnumIgnoreCase(SubsystemTypeCategory.class, category);
        return new ResponseEntity<>(subsystemTypeServiceV1.getSubsystemTypes(subsystemTypeCategory), HttpStatus.OK);
    }

}
