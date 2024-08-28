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

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.DEFAULT_PAGE_LIMIT;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.DEFAULT_PAGE_OFFSET;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.DEFAULT_SORT_ATTR;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.DEFAULT_SORT_DIR;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.GREETING_MESSAGE;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_MANAGER;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.TOTAL;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.TAG_PRIV11;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.bos.so.subsystemsmanager.business.api.GenericSerializer;
import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemsServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v1.SubsystemFilterServiceV1;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.web.controller.SubsystemCommonController;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.subsystems.SubsystemsApi;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemController.
 */
@Slf4j
@RestController
@RequestMapping(SubsystemController.BASE_PATH)
public class SubsystemController extends SubsystemCommonController implements SubsystemsApi {

    public static final String BASE_PATH = SUBSYSTEM_MANAGER + "/v1";

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemController.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SubsystemsServiceV1 subsystemsServiceV1;

    @Autowired
    private GenericSerializer genericSerializer;

    @Autowired
    private SubsystemFilterServiceV1 subsystemJsonFilterServiceV1;

    /**
     * Greeting.
     *
     * @return the response entity
     */
    @GetMapping()
    public ResponseEntity<String> greeting() {
        return new ResponseEntity<>(GREETING_MESSAGE, HttpStatus.OK);
    }

    /**
     * Post subsystem.
     *
     * @param subsystem the subsystem
     * @return the response entity
     */
    @Override
    public ResponseEntity<Subsystem> postSubsystem(@RequestBody final Subsystem subsystem) {
        final String privacyStr = String
            .format("Received POST subsystem request: %s", loggerHandler.getPrivacyTaggedData(subsystem.toString(), TAG_PRIV11));
        loggerHandler.logPrivacy(LOGGER, privacyStr);
        return new ResponseEntity<>(subsystemsServiceV1.postSubsystem(subsystem), HttpStatus.CREATED);
    }

    /**
     * Gets the subsystems.
     *
     * @param select the select
     * @param offset the offset
     * @param limit the limit
     * @param sortAttr the sort attr
     * @param sortDir the sort dir
     * @param filters the filters
     * @param tenantName the tenant name
     * @param paramsMap the params map
     * @return the subsystems
     */
    @Override
    public ResponseEntity<Object> getSubsystems(
            @RequestParam(value = "select", required = false) String select,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortAttr,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) final String filters,
            @RequestParam(required = false) final String tenantName,
            @RequestParam(required = false) final Map<String, Object> paramsMap) {
        if (!StringUtils.hasText(select)) {
            return getPaginatedSubsystems(offset, limit, sortAttr, sortDir, filters, tenantName, paramsMap);
        } else {
            return getSubsystemByFilter(select, tenantName);
        }
    }

    /**
     * Gets the paginated subsystems.
     *
     * @param offset the offset
     * @param limit the limit
     * @param sortAttr the sort attr
     * @param sortDir the sort dir
     * @param filters the filters
     * @param tenantName the tenant name
     * @param paramsMap the params map
     * @return the paginated subsystems
     */
    private ResponseEntity<Object> getPaginatedSubsystems(Integer offset, Integer limit, String sortAttr, String sortDir,
            final String filters, final String tenantName, final Map<String, Object> paramsMap) {
        final HttpHeaders header = new HttpHeaders();

        if (null != offset || null != limit || null != sortAttr || null != sortDir) {
            offset = ((null == offset) ? Integer.valueOf(DEFAULT_PAGE_OFFSET) : offset);
            limit = ((null == limit) ? Integer.valueOf(DEFAULT_PAGE_LIMIT) : limit);
            sortAttr = ((null == sortAttr) ? DEFAULT_SORT_ATTR : sortAttr);
            sortDir = ((null == sortDir) ? DEFAULT_SORT_DIR : sortDir);

            loggerHandler.logAudit(LOGGER, String.format("getSubsystemsByPagination() offset: %s, limit: %s, sortAttr: %s, sortDir: %s, filters: %s, "
                + "tenantName: %s ", offset, limit, sortAttr, sortDir, filters, tenantName));
            final SubsystemList subsystemList = subsystemsServiceV1.getAllSubsystemsPagination(offset, limit, sortAttr, sortDir, filters, tenantName);
            header.add(TOTAL, String.valueOf(subsystemList.getTotal()));
            return new ResponseEntity<>(genericSerializer.serializeObjectToExpectedJson(subsystemList.getItems()), header, HttpStatus.OK);

        } else {
            loggerHandler.logAudit(LOGGER, String.format("getFilteredSubsystem() request received with filter param : %s", paramsMap));
            final List<Subsystem> result = subsystemsServiceV1.fetchSubsystemByQuery(tenantName, paramsMap);
            header.add(TOTAL, String.valueOf(result.size()));
            return new ResponseEntity<>(genericSerializer.serializeObjectToExpectedJson(result), header, HttpStatus.OK);
        }
    }

    /**
     * Gets the subsystem by filter.
     *
     * @param fields the fields
     * @param tenantName the tenant name
     * @return the subsystem by filter
     */
    private ResponseEntity<Object> getSubsystemByFilter(final String fields, final String tenantName) {
        loggerHandler.logAudit(LOGGER, String.format("getSubsystemsFilteredJsonResponse() inclusionFields : %s", fields));
        final String[] filterFields = fields.split(",");

        if (filterFields.length == 1) {
            return new ResponseEntity<>(genericSerializer
                    .serializeObjectToExpectedJson(subsystemJsonFilterServiceV1.filterResponseSingleField(filterFields[0], tenantName))
                    , HttpStatus.OK);

        } else {
            return new ResponseEntity<>(
                    genericSerializer.serializeObjectToExpectedJson(subsystemJsonFilterServiceV1.filterResponseFields(fields, tenantName)),
                    HttpStatus.OK);
        }
    }

    /**
     * Gets the subsystem.
     *
     * @param subsystemId the subsystem id
     * @param select the select
     * @param tenantName the tenant name
     * @return the subsystem
     */
    @Override
    public ResponseEntity<Object> getSubsystem(@PathVariable("subsystemId") String subsystemId,
            @RequestParam(value = "select", required = false) String select, @RequestParam(required = false) String tenantName) {
        if (!StringUtils.hasText(select)) {
            return getSubsystemById(subsystemId, tenantName);
        } else {
            return getSubsystemByFilter(subsystemId, select, tenantName);
        }
    }

    /**
     * Gets the subsystem by id.
     *
     * @param subsystemId the subsystem id
     * @param tenantName the tenant name
     * @return the subsystem by id
     */
    private ResponseEntity<Object> getSubsystemById(final String subsystemId, final String tenantName) {
        loggerHandler.logAudit(LOGGER, String.format("getSubsystemById() for subsystemId: %s", subsystemId));
        return new ResponseEntity<>(genericSerializer.serializeObjectToExpectedJson(subsystemsServiceV1.getSubsystemById(subsystemId, tenantName)),
                HttpStatus.OK);
    }

    /**
     * Gets the subsystem by filter.
     *
     * @param subsystemId the subsystem id
     * @param fields the fields
     * @param tenantName the tenant name
     * @return the subsystem by filter
     */
    private ResponseEntity<Object> getSubsystemByFilter(final String subsystemId,
            final String fields,
            final String tenantName) {
        loggerHandler.logAudit(LOGGER, String.format("getSubsystemsByFilteredJsonWithinKnownSubsystem where subsystem id: %s and inclusionFields: %s",
            subsystemId, fields));
        final String[] filterFields = fields.split(",");
        if (filterFields.length == 1) {
            return new ResponseEntity<>(
                    genericSerializer.serializeObjectToExpectedJson(
                            subsystemJsonFilterServiceV1.filterResponseSingleFieldFromKnownSubsystem(filterFields[0], subsystemId, tenantName)),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    genericSerializer.serializeObjectToExpectedJson(subsystemJsonFilterServiceV1.filterResponseFields(fields, subsystemId, tenantName
                            )),HttpStatus.OK);
        }
    }

    /**
     * Put subsystem.
     *
     * @param subsystemId the subsystem id
     * @param updatedSubsystem the updated subsystem
     * @return the response entity
     */
    @Override
    public ResponseEntity<Subsystem> putSubsystem(@PathVariable final String subsystemId, @RequestBody final Map<String, Object> updatedSubsystem) {
        loggerHandler.logAudit(LOGGER, String.format("putSubsystem: request for subsystemId: %s with new updated Subsystem values",
            subsystemId));
        final Subsystem savedEntity = subsystemsServiceV1.patchSubsystem(subsystemId, updatedSubsystem);
        subsystemsServiceV1.clearGhostConnnectionProperties();
        return new ResponseEntity<>(savedEntity, HttpStatus.OK);
    }

    /**
     * Patch subsystem.
     *
     * @param subsystemId the subsystem id
     * @param updatedSubsystem the updated subsystem
     * @return the response entity
     */
    @Override
    public ResponseEntity<Subsystem> patchSubsystem(@PathVariable final String subsystemId, @RequestBody final Map<String, Object> updatedSubsystem) {
        loggerHandler.logAudit(LOGGER, String.format("patchSubsystem: request for subsystemId: %s with new updated Subsystem values",
            subsystemId));
        final Subsystem savedEntity = subsystemsServiceV1.patchSubsystem(subsystemId, updatedSubsystem);
        subsystemsServiceV1.clearGhostConnnectionProperties();
        return new ResponseEntity<>(savedEntity, HttpStatus.OK);
    }

    /**
     * Delete subsystem.
     *
     * @param subsystemId the subsystem id
     * @return the response entity
     */
    @Override
    public ResponseEntity<Void> deleteSubsystem(@PathVariable("subsystemId") final String subsystemId) {
        loggerHandler.logAudit(LOGGER, String.format("deleteSubsystemById() for subsystemId:: %s",
            subsystemId));
        if (!subsystemsServiceV1.deleteSubsystemById(subsystemId)) {
            throw new SubsystemDoesNotExistException(String.valueOf(subsystemId));
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete subsystems.
     *
     * @param subsystemIds the subsystem ids
     * @return the response entity
     */
    @Override
    public ResponseEntity<Void> deleteSubsystems(@RequestBody final Set<String> subsystemIds) {
        return super.deleteAllSubsystems(subsystemIds.stream().collect(Collectors.toList()));
    }
}