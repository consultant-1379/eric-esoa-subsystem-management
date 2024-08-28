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
package com.ericsson.bos.so.subsystemsmanager.business.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.NameMustBeUniqueException;
import com.ericsson.bos.so.subsystemsmanager.config.features.FeatureRouter;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.VENDOR_NAME;
import static com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType.DOMAIN_ORCHESTRATOR;
import static java.lang.Long.parseLong;

/**
 * The Class SubsystemRequestValidator.
 */
@Component
@Slf4j
public class SubsystemRequestValidator {

    public static final String REQUIRED_SUBSYSTEM_FIELDS_MESSAGE_TEMPLATE = "The SubSystem field '%s' is mandatory";

    public static final String NAME_FIELD = "name";

    public static final String URL_FIELD = "url";

    public static final String VENDOR_FIELD = "vendor";

    private static final String SSM_B_25 = "SSM-B-25";

    private static final String SYSTEM_ID = "subsystemId";

    @Autowired
    private SubsystemValidator subsystemValidator;

    @Autowired
    private SubsystemTypeValidator subsystemTypeValidator;

    @Autowired
    private ConnectionPropertiesValidator connectionPropertiesValidator;

    @Autowired
    private FeatureRouter featureRouter;

    @Autowired
    private SubsystemDao subsystemDao;

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    /**
     * Id not null check.
     *
     * @param id the id
     */
    public static void idNotNullCheck(final Object id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new MalformedContentException(SSM_B_25, (String)id);
        }
    }

    /**
     * Map not null check.
     *
     * @param map the map
     */
    public static void mapNotNullCheck(final Map<String, Object> map) {
        if (ObjectUtils.isEmpty(map)) {
            throw new MalformedContentException(SSM_B_25, ObjectUtils.getDisplayString(map));
        }
    }

    /**
     * Creates the list connection properties.
     *
     * @param connectionProperties the connection properties
     * @return the list
     */
    private List<ConnectionProperties> createListConnectionProperties(List<Map<String, Object>> connectionProperties) {
        final List<ConnectionProperties> listConnProps = new ArrayList<>();
        for (Map<String, Object> singleConnProps : connectionProperties) {
            final ConnectionProperties connProp = new ConnectionProperties();
            for (Map.Entry<String, Object> property : singleConnProps.entrySet()) {
                connProp.setProperty(property.getKey(), property.getValue().toString());
            }
            listConnProps.add(connProp);
        }
        return listConnProps;
    }

    /**
     * Checks if valid patch request.
     *
     * @param subsystemId the subsystem id
     * @param subsystem the subsystem
     */
    @SuppressWarnings("unchecked")
    public void isValidPatchRequest(final String subsystemId, final Map<String, Object> subsystem) {
        idNotNullCheck(subsystemId);
        mapNotNullCheck(subsystem);
        if (subsystem.containsKey("connectionProperties")) {
            final List<Map<String,Object>> connectionProperties = (List<Map<String, Object>>) subsystem.get("connectionProperties");
            for (Map<String, Object> property : connectionProperties) {
                if (property.containsKey("id")) {
                    validateSubsystemIdForConnectionProps(subsystemId, property);
                }
            }
            if (subsystemDao.findSubsystemById(parseLong(subsystemId)).getSubsystemType().getType().equals(DOMAIN_ORCHESTRATOR.getType())
                    && subsystemDao.findSubsystemById(parseLong(subsystemId)).getVendor().equals(VENDOR_NAME)) {
                connectionPropertiesValidator.validateDoConnectionProperties(createListConnectionProperties(connectionProperties));
            }
        }
    }

    /**
     * Checks if encrypted keys empty.
     *
     * @param encryptedKeys the encrypted keys
     * @return true, if is encrypted keys empty
     */
    public static boolean isEncryptedKeysEmpty(final List<String> encryptedKeys) {
        return encryptedKeys == null || encryptedKeys.isEmpty();
    }

    /**
     * Checks if api_key is valid UUID.
     *
     * @param apiKey the api key
     * @return true, if is valid UUID
     */
    public static boolean isValidUUID(final String apiKey) {
        boolean isUUID = false;
        try {
            UUID.fromString(apiKey);
            isUUID = true;
        } catch (IllegalArgumentException exception) {
            log.debug("Id is not a valid UUID: {}", apiKey);
            log.debug("Exception caught: {}", exception);
        }
        return isUUID;
    }

    /**
     * Checks if a valid update request.
     *
     * @param subsystemId the subsystem id
     * @param patchRequestFields the patch request fields
     * @param subsystem the subsystem
     */
    public void isValidUpdateRequest(final String subsystemId, final Map<String, Object> patchRequestFields, final Subsystem subsystem) {
        isValidPatchRequest(subsystemId, patchRequestFields);
        subsystemValidator.isValidSubsystem(subsystem);
    }

    /**
     * Checks if it is a valid connection properties post request.
     *
     * @param connProps the conn props
     * @param subsystemId the subsystem id
     */
    public void isValidConnPropsPostRequest(final ConnectionProperties connProps, final String subsystemId) {
        if ((connProps != null)) {
            idNotNullCheck(connProps.getSubsystemId());
            idNotNullCheck(subsystemId);
            final Subsystem subsystem = subsystemValidator.isSubsystemIdValid(subsystemId);
            if(PredefinedSubsystemType.isSupportedSingleConnectionProperty(subsystem.getSubsystemType().getType()) && 
                    !CollectionUtils.isEmpty(subsystem.getConnectionProperties())) {
                throw new MalformedContentException("SSM-B-37", subsystem.getName(), subsystem.getSubsystemType().getType());
            }
            else {
                connectionPropertiesValidator.validateConnectionProperty(connProps, subsystem.getSubsystemType());
            }
        } else {
            throw new MalformedContentException("SSM-B-35");
        }
    }

    /**
     * Validate post request.
     *
     * @param subsystemRequest the subsystem request
     */
    public void validatePostRequest(Subsystem subsystemRequest) {
        log.info("Validating request");
        validateRequiredFieldsPresent(subsystemRequest);
        connectionPropertiesValidator.validateConnectionProperties(subsystemRequest.getConnectionProperties(), subsystemRequest.getSubsystemType());
        if (subsystemRequest.getSubsystemType().getType().equals(DOMAIN_ORCHESTRATOR.getType())
                && (VENDOR_NAME.equals(subsystemRequest.getVendor()))) {
            connectionPropertiesValidator.validateDoConnectionProperties(subsystemRequest.getConnectionProperties());
        }

        subsystemTypeValidator.validateSubsystemType(subsystemRequest);
        verifyUniqueSubsystemName(subsystemRequest.getName());

        if (Boolean.TRUE.equals(featureRouter.featureIsEnabled(FeatureRouter.SOL005_FEATURE))) {
            subsystemValidator.validateSubsystemAdapterLink(subsystemRequest.getAdapterLink(), subsystemRequest.getSubsystemType(),
                    subsystemRequest.getVendor());
        }

        log.info("Validation of Subsystem: [{}] successful.", subsystemRequest.getName());
    }

    /**
     * Validate required fields present.
     *
     * @param subsystem the subsystem
     */
    public void validateRequiredFieldsPresent(Subsystem subsystem) {
        log.info("Validating if required fields are supplied for Subsystem: [{}]...", subsystem.getName());
        if (ObjectUtils.isEmpty(subsystem.getSubsystemType())
                || (!StringUtils.hasText(subsystem.getSubsystemType().getType()) &&
                        ObjectUtils.isEmpty(subsystem.getSubsystemType().getId()))) {
            throw new MalformedContentException(SSM_B_25, "type or id");
        }

        if (ObjectUtils.isEmpty(subsystem.getConnectionProperties())) {
            throw new MalformedContentException("SSM-B-35");
        }

        final Map<String, String> requiredFields = new HashMap<>();
        requiredFields.put(NAME_FIELD, subsystem.getName());
        subsystem.getSubsystemType().setType(getSubsytemTypeName(subsystem.getSubsystemType()));

        if (isSubTypeFieldRequired(subsystem.getSubsystemType().getType())) {
            final Subtype subtype = subsystem.getSubsystemType().getSubtype();
            if (ObjectUtils.isEmpty(subtype) || (ObjectUtils.isEmpty(subtype.getId()) && !StringUtils.hasText(subtype.getName()))) {
                throw new MalformedContentException(SSM_B_25, "subtype");
            }
        }

        if (SubsystemTypeValidator.isUrlFieldRequired(subsystem.getSubsystemType().getType())) {
            requiredFields.put(URL_FIELD, subsystem.getUrl());
        }

        if (SubsystemTypeValidator.isVendorFieldRequired(subsystem.getSubsystemType().getType())) {
            requiredFields.put(VENDOR_FIELD, subsystem.getVendor());
        }

        for (Map.Entry<String, String> requiredField : requiredFields.entrySet()) {
            final String fieldValue = requiredField.getValue();
            log.debug("Validating [{} : {}]...", requiredField.getKey(), fieldValue);
            if (!StringUtils.hasText(fieldValue) || fieldValue.trim().isEmpty()) {
                throw new MalformedContentException(SSM_B_25, requiredField.getKey());
            }
        }
    }

    /**
     * Gets the subsytem type name.
     *
     * @param subsystemType the subsystem type
     * @return the subsytem type name
     */
    private String getSubsytemTypeName(final SubsystemType subsystemType) {
        return StringUtils.hasText(subsystemType.getType()) ? subsystemType.getType()
                : subsystemTypeDao.findById(subsystemType.getId()).getType();
    }

    /**
     * Verify unique subsystem name.
     *
     * @param subsystemName the subsystem name
     */
    private void verifyUniqueSubsystemName(String subsystemName) {
        log.debug("Verifying if Subsystem name: [{}] is unique...", subsystemName);
        if (subsystemDao.existsByName(subsystemName)) {
            throw new NameMustBeUniqueException(subsystemName);
        }
    }

    /**
     * Validate subsystem id for connection properties.
     *
     * @param subsystemId the subsystem id
     * @param property the property
     */
    private void validateSubsystemIdForConnectionProps(final String subsystemId, Map<String, Object> property) {
        if ((property.get(SYSTEM_ID) != null) && (!property.get(SYSTEM_ID).toString().equals(subsystemId))) {
            throw new MalformedContentException(SSM_B_25, property.get(SYSTEM_ID).toString());
        }
        final Object propertyId = property.get("id");
        if (!ObjectUtils.isEmpty(propertyId) && connectionPropertiesValidator.isValidConnProps(propertyId.toString(), subsystemId) == null) {
            throw new MalformedContentException(SSM_B_25, propertyId.toString());
        }
    }

    /**
     * Checks if subtype field required.
     *
     * @param subsystemType the subsystem type
     * @return true, if is sub type field required
     */
    private boolean isSubTypeFieldRequired(final String subsystemType) {
        return !CollectionUtils.isEmpty(subsystemTypeDao.findByType(subsystemType).getSubtypes());
    }
}
