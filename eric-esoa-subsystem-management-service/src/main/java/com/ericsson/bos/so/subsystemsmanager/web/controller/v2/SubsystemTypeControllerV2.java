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
package com.ericsson.bos.so.subsystemsmanager.web.controller.v2;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_MANAGER;

import java.util.List;

import jakarta.validation.Valid;

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

import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemTypeServiceV2;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeRequest;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeResponse;
import com.ericsson.bos.so.subsystemsmanager.api.v2.subsystem_type.SubsystemTypesApi;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemTypeControllerV2.
 */
@Slf4j
@RestController
@RequestMapping(SubsystemTypeControllerV2.BASE_PATH)
public class SubsystemTypeControllerV2 implements SubsystemTypesApi {

    public static final String BASE_PATH = SUBSYSTEM_MANAGER + "/v2";

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemTypeControllerV2.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private SubsystemTypeServiceV2 subsystemTypeServiceV2;

    /**
     * Post subsystem type.
     *
     * @param request the request
     * @return the response entity
     */
    @Override
    public ResponseEntity<SubsystemTypeResponse> postSubsystemType(@RequestBody SubsystemTypeRequest request) {
        loggerHandler.logAudit(LOGGER, String.format("Received POST Subsystem Type request: [%s]...", request));
        return new ResponseEntity<>(subsystemTypeServiceV2.postSubsystemType(request), HttpStatus.CREATED);
    }

    /**
     * Post subsystem subtype.
     *
     * @param subsystemTypeId the subsystem type id
     * @param subtype the subtype
     * @return the response entity
     */
    @Override
    public ResponseEntity<List<Subtype>> postSubsystemSubtype(@PathVariable("subsystemTypeId") Long subsystemTypeId,
            @Valid @RequestBody Subtype subtype) {
        loggerHandler.logAudit(LOGGER,
                String.format("Received POST Subsystem Subtype request: subtype [%s] and subsystemTypeId [%s]...", subtype, subsystemTypeId));
        return new ResponseEntity<>(subsystemTypeServiceV2.postSubsystemSubtype(subsystemTypeId, subtype), HttpStatus.CREATED);
    }

    /**
     * Gets the subsystem types.
     *
     * @param type the type
     * @return the subsystem types
     */
    @Override
    public ResponseEntity<List<SubsystemTypeResponse>> getSubsystemTypes(@RequestParam(required = false) String type) {
        loggerHandler.logAudit(LOGGER, String.format("Received GET Subsystem Types request, type: [%s]...", type));
        if (StringUtils.hasText(type)) {
            return new ResponseEntity<>(subsystemTypeServiceV2.getSubsystemTypesByType(type), HttpStatus.OK);
        }

        return new ResponseEntity<>(subsystemTypeServiceV2.getSubsystemTypes(), HttpStatus.OK);
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
        subsystemTypeServiceV2.deleteSubsystemType(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete subsystem subtype.
     *
     * @param subsystemTypeId the subsystem type id
     * @param subsystemSubtypeId the subsystem subtype id
     * @return the response entity
     */
    @Override
    public ResponseEntity<Void> deleteSubsystemSubtype(@PathVariable(value = "subsystemTypeId") Long subsystemTypeId,
            @PathVariable(value = "subtypeId") Long subsystemSubtypeId) {

        loggerHandler.logAudit(LOGGER,
                String.format("Received DELETE Subsystem subtype request for subsystemTypeId: [%s] and subsystemSubtypeId: [%s]...", subsystemTypeId,
                        subsystemSubtypeId));
        subsystemTypeServiceV2.deleteSubsystemSubtype(subsystemTypeId, subsystemSubtypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
