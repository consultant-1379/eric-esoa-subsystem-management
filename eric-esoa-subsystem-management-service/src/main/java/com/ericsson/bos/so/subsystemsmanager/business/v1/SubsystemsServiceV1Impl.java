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
package com.ericsson.bos.so.subsystemsmanager.business.v1;

import com.ericsson.bos.so.subsystemsmanager.business.SubsystemsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.api.v1.SubsystemsServiceV1;
import com.ericsson.bos.so.subsystemsmanager.business.pagination.SubsystemPaginationUtil;
import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_TYPE_FILTER_NAME;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.TAG_PRIV11;

/**
 * The Class SubsystemsServiceV1Impl.
 */
@Service
@Slf4j
public class SubsystemsServiceV1Impl extends SubsystemsServiceImpl implements SubsystemsServiceV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemsServiceV1Impl.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private SubsystemPaginationUtil subsystemPaginationUtil;

    /**
     * Post subsystem.
     *
     * @param subsystemRequest the subsystem request
     * @return the subsystem
     */
    @Override
    public Subsystem postSubsystem(final Subsystem subsystemRequest) {
        final String privacyStr = String.format("Creating subsystem: %s",
            loggerHandler.getPrivacyTaggedData(subsystemRequest.toString(), TAG_PRIV11));
        loggerHandler.logPrivacy(LOGGER, privacyStr);
        subsystemRequestValidator.validatePostRequest(subsystemRequest);
        Subsystem subsystem = super.postSubsystem(subsystemRequest);
        subsystem = setNullableFields(subsystem);
        return subsystem;
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
     * Gets the list of subsystems pagination.
     *
     * @param offset the offset
     * @param limit the limit
     * @return the all subsystems pagination
     */
    @Override
    public List<Subsystem> getAllSubsystemsPagination(final Integer offset, final Integer limit) {
        final List<Subsystem> subsystems = subsystemPaginationUtil.getPaginatedSubsystems(offset, limit);
        return updateSubsystemFieldValues(subsystems);
    }

    /**
     * This method has a point cut. To debug please check Validation class TenantFilterConnectionPropertiesValidation.
     *
     * @param tenantName the tenant name
     * @param search the search
     * @return the list
     */
    @Override
    public List<Subsystem> fetchSubsystemByQuery(final String tenantName, final Map<String, Object> search) {
        LOGGER.info("fetchSubsystemByQuery() request received.");
        search.remove("tenantName"); // Tenancy is kept separate to maintain the existing code.
        if (!search.isEmpty()) {
            final Example<Subsystem> criteria = buildFilterCriteria(search);
            return updateSubsystemFieldValues(resolveSubsystemType(subsystemDao.getAllSubsystems(criteria)));
        } else {
            return updateSubsystemFieldValues(resolveSubsystemType(subsystemDao.getAllSubsystems()));
        }
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
    public SubsystemList getAllSubsystemsPagination(final Integer offset, final Integer limit, final String sortAttr, final String sortDir,
                                                    String filter, String tenantName) {
        LOGGER.info("getAllSubsystemsPagination() received request to get subsystems and form in pagination, offset: {}, limit: {}, sortAttr: {}, "
                + "sortDir: {}", offset, limit, sortAttr, sortDir);
        subsystemDao.getFullPaginatedSubsystems(offset, limit, sortAttr, sortDir, filter)
                .setItems(resolveSubsystemType(subsystemDao.getAllSubsystems()));

        final SubsystemList subsystemList = subsystemDao.getFullPaginatedSubsystems(offset, limit, sortAttr, sortDir, filter);
        subsystemList.setItems(updateSubsystemFieldValues(subsystemList.getItems()));
        return subsystemList;
    }

    /**
     * Gets the subsystem by id.
     *
     * @param subsystemId the subsystem id
     * @param tenantName the tenant name
     * @return the subsystem by id
     */
    @Override
    public Subsystem getSubsystemById(final String subsystemId, final String tenantName) {
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
    public Subsystem patchSubsystem(final String subsystemId, final Map<String, Object> patchRequestFields) {
        Subsystem subsystem = super.patchSubsystem(subsystemId, patchRequestFields);
        subsystem = setNullableFields(subsystem);
        final String privacyStr = String.format("Response received for patched Subsystem: %s",
            loggerHandler.getPrivacyTaggedData(subsystem.toString(), TAG_PRIV11));
        loggerHandler.logPrivacy(LOGGER, privacyStr);
        return subsystem;
    }

    /**
     * Builds the filter criteria.
     *
     * @param search the search
     * @return the example
     */
    private Example<Subsystem> buildFilterCriteria(final Map<String, Object> search) {
        final Map<String, Object> filterMap = new HashMap<>();
        populateFilterMap(search, filterMap);

        final Subsystem subsystem = new Subsystem();
        if (!filterMap.isEmpty()) {
            try {
                checkAndConvertSubsystemTypeInMap(filterMap);
                BeanUtils.populate(subsystem, filterMap);
            } catch (Exception exception) {
                LOGGER.info("conversion error: {} {}", exception.getMessage(), exception);
            }
        }

        final ExampleMatcher tempMatcher = ExampleMatcher.matching().withIgnoreNullValues();
        return Example.of(subsystem, tempMatcher);
    }

    /**
     * Populate filter map.
     *
     * @param search the search
     * @param filterMap the filter map
     */
    private void populateFilterMap(final Map<String, Object> search, Map<String, Object> filterMap) {
        String value;
        final String replaceChar="\"";
        if (search.get(FILTERS) != null && !search.get(FILTERS).toString().isEmpty()) {
            value = search.get(FILTERS).toString();
            value = value.substring(1, value.length() - 1);
            final String[] keyValuePairs = value.split(",");

            for (String pair : keyValuePairs) {
                final String[] entry = pair.split(":");
                filterMap.put(entry[0].trim().replace(replaceChar, ""), entry[1].trim().replace(replaceChar, ""));
            }
        } else {
            value = search.toString();
            value = value.substring(1, value.length() - 1);
            final String[] keyValuePairs = value.split(",");

            for (String pair : keyValuePairs) {
                final String[] entry = pair.split("=");
                filterMap.put(entry[0].trim().replace(replaceChar, ""), entry[1].trim().replace(replaceChar, ""));
            }
        }
    }

    /**
     * Check and convert subsystem type in map.
     *
     * @param filterMap the filter map
     */
    private void checkAndConvertSubsystemTypeInMap(final Map<String, Object> filterMap) {
        if (filterMap.containsKey(SUBSYSTEM_TYPE_FILTER_NAME)) {
            final SubsystemType subsystemType = new SubsystemType();
            subsystemType.setType((String) filterMap.get(SUBSYSTEM_TYPE_FILTER_NAME));
            filterMap.put(SUBSYSTEM_TYPE_FILTER_NAME, subsystemType);
        }
    }

    /**
     * Resolve subsystem type.
     *
     * @param subsystems the subsystems
     * @return the list
     */
    private List<Subsystem> resolveSubsystemType(final List<Subsystem> subsystems) {
        subsystems.forEach(subsystem -> {
            cipherUtil.encryptOrDecryptProperties(subsystem.getConnectionProperties(), EncryptDecryptConnectionPropertiesUtil.DECRYPT);
            subsystem.getSubsystemType().resolveCategory();
        });

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
        final SubsystemType subsystemType = subsystem.getSubsystemType().toBuilder().subtypes(null).alias(null).subtype(null).build();
        return subsystem.toBuilder().apiKey(null).subsystemSubtypeId(null).subsystemType(subsystemType).build();
    }

}
