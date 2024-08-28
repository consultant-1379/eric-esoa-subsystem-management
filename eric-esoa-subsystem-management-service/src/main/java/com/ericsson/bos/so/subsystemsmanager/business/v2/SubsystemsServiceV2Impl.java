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

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.OPERATIONAL_STATE;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_TYPE_FILTER_NAME;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBTYPE;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.TAG_PRIV11;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.flywaydb.core.internal.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ericsson.bos.so.subsystemsmanager.business.SubsystemsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemsServiceV2;
import com.ericsson.bos.so.subsystemsmanager.business.exception.FailedJSONParameterException;
import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemsServiceV2Impl.
 */
@Service
@Slf4j
public class SubsystemsServiceV2Impl extends SubsystemsServiceImpl implements SubsystemsServiceV2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemsServiceV2Impl.class);

    @Autowired
    private LoggerHandler loggerHandler;

    /**
     * Post subsystem.
     *
     * @param subsystemRequest the subsystem request
     * @return the subsystem
     */
    @Override
    public Subsystem postSubsystem(Subsystem subsystemRequest) {
        final String privacyStr = String.format("Creating subsystem: %s",
            loggerHandler.getPrivacyTaggedData(subsystemRequest.toString(), TAG_PRIV11));
        loggerHandler.logPrivacy(LOGGER, privacyStr);
        subsystemRequestValidator.validatePostRequest(subsystemRequest);
        Subsystem subsystem = super.postSubsystem(subsystemRequest);
        subsystem = setNullableFields(subsystem);
        return subsystem;
    }

    /**
     * Gets the list of subsystems pagination.
     *
     * @param offset the offset
     * @param limit the limit
     * @param sortAttr the sort attr
     * @param sortDir the sort dir
     * @param filter the filter
     * @param tenantName the tenant name
     * @return the all subsystems pagination
     */
    @Override
    public SubsystemList getAllSubsystemsPagination(Integer offset, Integer limit, String sortAttr, String sortDir, String filter,
                                                    String tenantName) {
        LOGGER.info("getAllSubsystemsPagination() received request to get subsystems and form in pagination, offset: {}, limit: {}, sortAttr: {}, "
                + "sortDir: {}, tenantName: {}", offset, limit, sortAttr, sortDir, tenantName);

        final String filterJson = (null == filter || filter.isEmpty()) ? "{}" : filter;
        final Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(FILTERS, filterJson);
        final Example<Subsystem> criteria = buildSubsystemFilterCriteria(filterMap);

        final List<Subsystem> subsystems = subsystemDao.getFullPaginatedSubsystems(offset, limit, sortAttr, sortDir, criteria);
        final SubsystemList subsystemList = new SubsystemList();
        subsystemList.setItems(updateSubsystemFieldValues(iterateSubsystems(subsystems)));
        subsystemList.setTotal(subsystems.size());
        return subsystemList;
    }

    /**
     * Gets all the subsystems.
     *
     * @param tenantName the tenant name
     * @return the all subsystems
     */
    @Override
    public List<Subsystem> getAllSubsystems(final String tenantName) {
        LOGGER.info("getAllSubsystems() request received.");
        return updateSubsystemFieldValues(super.getAllSubsystems(tenantName));
    }

    /**
     * Fetch subsystem by query.
     *
     * @param tenantName the tenant name
     * @param searchMap the search map
     * @return the list
     */
    @Override
    public List<Subsystem> fetchSubsystemByQuery(final String tenantName, final Map<String, Object> searchMap) {
        LOGGER.info("fetchSubsystemByQuery() request received for tenantName: {}", tenantName);
        searchMap.remove("tenantName"); //Tenancy is kept separate to maintain the existing code.
        if (!searchMap.isEmpty()) {
            final Example<Subsystem> filterCriteria = buildSubsystemFilterCriteria(searchMap);
            return updateSubsystemFieldValues(iterateSubsystems(subsystemDao.getAllSubsystems(filterCriteria)));
        } else {
            return updateSubsystemFieldValues(iterateSubsystems(subsystemDao.getAllSubsystems()));
        }
    }

    /**
     * Gets the subsystem by id.
     *
     * @param subsystemId the subsystem id
     * @param tenantName the tenant name
     * @return the subsystem by id
     */
    @Override
    public Subsystem getSubsystemById(String subsystemId, String tenantName) {
        final Subsystem subsystem = super.getSubsystemById(subsystemId, tenantName);
        return setNullableFields(subsystem);
    }

    /**
     * Patch subsystem.
     *
     * @param subsystemId the subsystem id
     * @param patchRequestFields the patch request fields
     * @return the subsystem
     */
    @Override
    public Subsystem patchSubsystem(String subsystemId, Map<String, Object> patchRequestFields) {
        Subsystem subsystem = super.patchSubsystem(subsystemId, patchRequestFields);
        subsystem = setNullableFields(subsystem);
        final String privacyStr = String.format("Response received for patched Subsystem: %s",
            loggerHandler.getPrivacyTaggedData(subsystem.toString(), TAG_PRIV11));
        loggerHandler.logPrivacy(LOGGER, privacyStr);
        return subsystem;
    }

    /**
     * Iterate subsystems.
     *
     * @param subsystems the subsystems
     * @return the list
     */
    private List<Subsystem> iterateSubsystems(final List<Subsystem> subsystems) {
        subsystems.forEach(subsystem -> cipherUtil.encryptOrDecryptProperties(subsystem.getConnectionProperties(),
                EncryptDecryptConnectionPropertiesUtil.DECRYPT));
        return subsystems;
    }

    /**
     * Update subsystem field values.
     *
     * @param subsystems the subsystems
     * @return the list
     */
    private List<Subsystem> updateSubsystemFieldValues(List<Subsystem> subsystems) {
        final List<Subsystem> updatedSubsystems = new ArrayList<>();
        for (Subsystem subsystem : subsystems) {
            updatedSubsystems.add(setNullableFields(subsystem));
        }
        return updatedSubsystems;
    }

    /**
     * Sets the nullable fields.
     *
     * @param subsystem the subsystem
     * @return the subsystem
     */
    private Subsystem setNullableFields(Subsystem subsystem) {
        final SubsystemType subsystemType = subsystem.getSubsystemType().toBuilder().category(null).subtypes(null).build();
        return subsystem.toBuilder().subsystemTypeId(null).subsystemSubtypeId(null).subsystemType(subsystemType).build();
    }

    /**
     * Builds the subsystem filter criteria.
     *
     * @param search the search
     * @return the example
     */
    private Example<Subsystem> buildSubsystemFilterCriteria(final Map<String, Object> search) {
        final Map<String, Object> filterMap = new HashMap<>();
        populateFilterMap(search, filterMap);
        final Subsystem subsystem = new Subsystem();
        if (!filterMap.isEmpty()) {
            try {
                checkAndConvertSubsystemTypeInMap(filterMap);
                checkAndConvertApiKeyInMap(filterMap);
                checkAndConvertOperationalStateInMap(filterMap);
                BeanUtils.populate(subsystem, filterMap);
            } catch (Exception exception) {
                LOGGER.info("conversion error: {} {}", exception.getMessage(), exception);
            }
        }
        final String privacyStr = String.format("subsystem built with filter Criteria: %s",
            loggerHandler.getPrivacyTaggedData(subsystem.toString(), TAG_PRIV11));
        loggerHandler.logPrivacy(LOGGER, privacyStr);

        final ExampleMatcher tempMatcher = ExampleMatcher.matching().withIgnoreNullValues();
        return Example.of(subsystem, tempMatcher);
    }

    /**
     * Populate filter map.
     *
     * @param searchMap the search map
     * @param filterMap the filter map
     */
    private void populateFilterMap(final Map<String, Object> searchMap, Map<String, Object> filterMap) {

        if (searchMap.get(FILTERS) != null && !searchMap.get(FILTERS).toString().isEmpty()) {
            final String filterValue = searchMap.get(FILTERS).toString();
            for (Map.Entry<String, Object> entry : buildFilterMap(filterValue).entrySet()) {
                filterMap.put(entry.getKey(), entry.getValue());
            }
        } else {
            buildMapWithFields(searchMap.toString(), filterMap);
        }
        LOGGER.debug("populated fitler map: {}", filterMap);
    }

    /**
     * Builds the filter map.
     *
     * @param filterJson the filter json
     * @return the map
     */
    private Map<String, Object> buildFilterMap(final String filterJson) {
        final Object subsystemType = fetchValueOfChildEntity(SUBSYSTEM_TYPE_FILTER_NAME, filterJson);
        final Map<String, Object> filterValue = convertIntoMap(filterJson);
        if (!ObjectUtils.isEmpty(subsystemType)) {
            filterValue.put(SUBSYSTEM_TYPE_FILTER_NAME, subsystemType);
        }
        LOGGER.debug("Map built with filter json: {}", filterValue);
        return filterValue;
    }

    /**
     * Fetch value of child entity.
     *
     * @param childField the child field
     * @param filterJson the filter json
     * @return the object
     */
    private Object fetchValueOfChildEntity(final String childField, final String filterJson) {
        Object objectField = null;
        if (filterJson.contains(childField)) {
            try {
                objectField = new JSONObject(filterJson).get(childField);
            } catch (JSONException exception) {
                LOGGER.error("Error occurred while parsing passed json value: {}, {}", exception.getMessage(), exception);
                throw new FailedJSONParameterException(filterJson);
            }
        }
        return objectField;
    }

    /**
     * Convert into map.
     *
     * @param filterJson the filter json
     * @return the map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertIntoMap(final String filterJson) {
        Map<String, Object> fieldMap = new HashMap<>();
        try {
            fieldMap = new ObjectMapper().readValue(filterJson, Map.class);
        } catch (JsonProcessingException exception) {
            LOGGER.error("Error occurred while mapping json value: {}, {}", exception.getMessage(), exception);
            throw new FailedJSONParameterException(filterJson);
        }
        LOGGER.debug("filterJson converted to map: {}", fieldMap);
        return fieldMap;
    }

    /**
     * Check and convert subsystem type in map.
     *
     * @param filterMap the filter map
     */
    private void checkAndConvertSubsystemTypeInMap(Map<String, Object> filterMap) {

        if (filterMap.containsKey(SUBSYSTEM_TYPE_FILTER_NAME)) {
            final String subsystemTypeValue = filterMap.get(SUBSYSTEM_TYPE_FILTER_NAME).toString();

            final Object subType = fetchValueOfChildEntity(SUBTYPE, subsystemTypeValue);
            final Map<String, Object> subsystemTypeMap = convertIntoMap(subsystemTypeValue);

            if (!ObjectUtils.isEmpty(subType)) {
                convertSubtypeInMap(subsystemTypeMap, subType.toString());
            }

            final SubsystemType subsystemType = new SubsystemType();
            setProperty(subsystemType, subsystemTypeMap);
            filterMap.put(SUBSYSTEM_TYPE_FILTER_NAME, subsystemType);
            LOGGER.debug("filterMap after setting subsystemType: {}", filterMap);
        }
    }

    /**
     * Convert subtype in map.
     *
     * @param subsystemTypeMap the subsystem type map
     * @param subTypeString the sub type string
     */
    private void convertSubtypeInMap(Map<String, Object> subsystemTypeMap, final String subTypeString) {
        final Map<String, Object> subTypeMap = convertIntoMap(subTypeString);
        final Subtype subType = new Subtype();
        setProperty(subType, subTypeMap);
        subsystemTypeMap.put(SUBTYPE, subType);
        LOGGER.info("subsystemTypeMap after adding subtype: {}", subsystemTypeMap);
    }

    /**
     * Builds the map with fields.
     *
     * @param subsystemAttributes the subsystem attributes
     * @param fieldMap the field map
     */
    private void buildMapWithFields(final String subsystemAttributes, Map<String, Object> fieldMap) {
        final String value = subsystemAttributes.substring(1, subsystemAttributes.length() - 1);
        if (StringUtils.hasText(value)) {
            final String[] keyValuePairs = value.split(",");
            for (String pair : keyValuePairs) {
                final String[] entry = pair.split("=");
                fieldMap.put(entry[0].trim().replace("\"", ""), entry[1].trim().replace("\"", ""));
            }
        }
        LOGGER.debug("Map built after inserting filter fields: {}", fieldMap);
    }

    /**
     * Check and convert api key in map.
     *
     * @param filterMap the filter map
     */
    private void checkAndConvertApiKeyInMap(final Map<String, Object> filterMap) {
        if (filterMap.containsKey(API_KEY)) {
            filterMap.put(API_KEY, UUID.fromString(filterMap.get(API_KEY).toString()));
        }
    }

    /**
     * Check and convert operational state in map.
     *
     * @param filterMap the filter map
     */
    private void checkAndConvertOperationalStateInMap(final Map<String, Object> filterMap) {
        if (filterMap.containsKey(OPERATIONAL_STATE)) {
            final String operationalStateValue = filterMap.get(OPERATIONAL_STATE).toString();
            filterMap.put(OPERATIONAL_STATE, OperationalState.valueOf(operationalStateValue));
        }
    }

    /**
     * Sets the property.
     *
     * @param object the object
     * @param fieldMap the field map
     */
    private void setProperty(Object object, Map<String, Object> fieldMap) {
        try {
            BeanUtils.populate(object, fieldMap);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            LOGGER.warn("conversion error: {}, {}", exception.getMessage(), exception);
        }
    }

}
