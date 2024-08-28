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

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.*;
import static com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType.DOMAIN_ORCHESTRATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ConnectionPropertiesValidator.
 */
@Component
@Slf4j
public class ConnectionPropertiesValidator {

    public static final String NAME_PROPERTY = "name";

    public static final String TENANT_PROPERTY = "tenant";

    private static final String SSM_A_27 = "SSM-A-27";

    private static final String SSM_B_45 = "SSM-B-45";

    @Autowired
    private SubsystemValidator subsystemValidator;

    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;


    /**
     * Verify property exists.
     *
     * @param connectionProperties the connection properties
     * @param propertyName the property name
     */
    public static void verifyPropertyExists(ConnectionProperties connectionProperties, String propertyName) {
        log.debug("Verifying if '{}' property is present in {}...", propertyName, connectionProperties);
        if (connectionProperties.getProperties().stream()
                .map(Property::getKey)
                .noneMatch(propertyName::equalsIgnoreCase)) {
            throw new MalformedContentException(SSM_A_27, propertyName);
        }
    }

    /**
     * Checks if connectionPropertiesId is valid.
     *
     * @param connectionPropertiesId the connection properties id
     * @return the connection properties
     */
    public ConnectionProperties isConnPropsIdValid(final String connectionPropertiesId) {
        SubsystemRequestValidator.idNotNullCheck(connectionPropertiesId);
        final Optional<ConnectionProperties> connectionPropertiesOpt = connectionPropertiesRepository.findById(Long.valueOf(connectionPropertiesId));
        return connectionPropertiesOpt.orElseThrow(
                () -> new ConnectionPropertiesDoesNotExistException(connectionPropertiesId));
    }

    /**
     * Checks if connectionPropertiesId is valid.
     *
     * @param connPropsId the conn props id
     * @return the connection properties
     */
    public ConnectionProperties isValidConnProps(final String connPropsId) {
        SubsystemRequestValidator.idNotNullCheck(connPropsId);
        return isConnPropsIdValid(connPropsId);
    }

    /**
     * Checks if is valid connection properties.
     *
     * @param connPropsId the conn props id
     * @param subsystemId the subsystem id
     * @return the connection properties
     */
    public ConnectionProperties isValidConnProps(final String connPropsId, final String subsystemId) {
        subsystemValidator.isSubsystemIdValid(subsystemId);
        return isConnPropsIdValid(connPropsId);
    }

    /**
     * Checks if is valid connection properties.
     *
     * @param connPropsId the conn props id
     * @param subsystemId the subsystem id
     * @param patchRequestMap the patch request map
     * @return the connection properties
     */
    public ConnectionProperties isValidConnProps(final String connPropsId, final String subsystemId, final Map<String, Object> patchRequestMap) {
        SubsystemRequestValidator.idNotNullCheck(subsystemId);
        SubsystemRequestValidator.idNotNullCheck(connPropsId);
        SubsystemRequestValidator.mapNotNullCheck(patchRequestMap);
        subsystemValidator.isSubsystemIdValid(subsystemId);

        return isConnPropsIdValid(connPropsId);
    }

    /**
     * Validate connection properties.
     *
     * @param connectionProperty the connection property
     * @param subsystemId the subsystem id
     */
    public void validateConnectionProperty(final ConnectionProperties connectionProperty, final String subsystemId) {
        final Subsystem subsystem = subsystemValidator.isSubsystemIdValid(subsystemId);
        validateConnectionProperty(connectionProperty, subsystem.getSubsystemType());
    }

    /**
     * Validate connection properties.
     *
     * @param connectionProperties the connection properties
     * @param subsystemType the subsystem type
     */
    public void validateConnectionProperties(final List<ConnectionProperties> connectionProperties, SubsystemType subsystemType) {
        log.info("Validating connProt list ");
        for (ConnectionProperties connectionProperty : connectionProperties) {
            validateConnectionProperty(connectionProperty, subsystemType);
        }
        verifyConnectionPropertiesNamesUnique(connectionProperties);
        log.info("Validation of all Connection Properties successful.");
    }

    /**
     * Validate connection property.
     *
     * @param connectionProperties the connection properties
     * @param subsystemType the subsystem type
     */
    public void validateConnectionProperty(final ConnectionProperties connectionProperties, final SubsystemType subsystemType) {
        log.debug("Validating connProt {}...", connectionProperties);
        validateProperties(connectionProperties.getProperties(), subsystemType);
        validatePasswordAndClientSecretFieldsIfPresent(connectionProperties);
        if (connectionProperties.getEncryptedKeys() != null) {
            validateEncryptedKeys(connectionProperties);
        }
        log.debug("Validation of Connection Properties successful.");
    }

    /**
     * Validate DomainOrchestrator connection properties.
     *
     * @param connectionProperties the connection properties
     */
    public void validateDoConnectionProperties(final List<ConnectionProperties> connectionProperties) {
        log.info("Validating DomainOrchestrator connProt list ");
        for (ConnectionProperties connectionProperty : connectionProperties) {
            validateDoConnectionProperty(connectionProperty);
        }
    }

    /**
     * Validate DomainOrchestrator connection properties.
     *
     * @param connectionProperty the connection property
     */
    public void validateDoConnectionProperty(final ConnectionProperties connectionProperty) {
        log.debug("Validating connProt {}...", connectionProperty);
        verifyDoRequiredPropertiesPresent(connectionProperty.getProperties());
        log.debug("Validation of DomainOrchestrator Connection Properties successful.");
    }

    /**
     * Validate whether DomainOrchestrator connection properties present.
     *
     * @param properties the properties
     */
    private void verifyDoRequiredPropertiesPresent(List<Property> properties) {
        if (!getPropertyKeys(properties).contains(AUTH_TYPE)) {
            throw new MalformedContentException(SSM_B_45, AUTH_TYPE, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
        }
        for (Property property : properties) {
            if (property.getKey().equals(AUTH_TYPE)) {
                switch(property.getValue()) {
                    case SUBSYSTEM_AUTH_TYPE_COOKIE:
                        checkCookie(properties);
                        break;
                    case SUBSYSTEM_AUTH_TYPE_BEARER:
                        checkBearer(properties);
                        break;
                    case SUBSYSTEM_AUTH_TYPE_BASIC_AUTH:
                        checkBasic(properties);
                        break;
                    case SUBSYSTEM_AUTH_TYPE_BASIC_AUTH_TOKEN:
                        checkBasicToken(properties);
                        break;
                    case SUBSYSTEM_AUTH_TYPE_NO_AUTH:
                        log.info("noAuth case: no mandatory parameter is required");
                        break;
                    case SUBSYSTEM_AUTH_TYPE_API_KEY:
                        checkApiKey(properties);
                        break;
                    case SUBSYSTEM_AUTH_TYPE_AWS:
                        checkAws(properties);
                        break;
                    default:
                        log.info("the received authorization is not managed by subsystem-management-api: {}", property.getValue());
                }
            }
        }
    }

    /**
     * Check cookie.
     *
     * @param properties the properties
     */
    private void checkCookie(List<Property> properties) {
        log.info("cookie case: mandatory parameters are required");
        final List<String> requiredProperties = ImmutableList.of(AUTH_URL, AUTH_BODY, AUTH_HEADERS, AUTH_KEY, SUBSYSTEM_CONNECTION_PROPERTY_USERNAME,
                SUBSYSTEM_CONNECTION_PROPERTY_PASSWORD);
        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_B_45, requiredProperty, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
            }
        }
    }

    /**
     * Check bearer.
     *
     * @param properties the properties
     */
    private void checkBearer(List<Property> properties) {
        log.info("bearer case: mandatory parameters are required");
        final List<String> requiredProperties = ImmutableList.of(AUTH_URL, AUTH_HEADERS, AUTH_KEY, AUTH_BODY, TOKEN_REF);
        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_B_45, requiredProperty, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
            }
        }
    }

    /**
     * Check basic.
     *
     * @param properties the properties
     */
    private void checkBasic(List<Property> properties) {
        log.info("basic case: mandatory parameters are required");
        final List<String> requiredProperties = ImmutableList.of(AUTH_KEY, SUBSYSTEM_CONNECTION_PROPERTY_USERNAME,
                SUBSYSTEM_CONNECTION_PROPERTY_PASSWORD);
        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_B_45, requiredProperty, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
            }
        }
    }

    /**
     * Check basic token.
     *
     * @param properties the properties
     */
    private void checkBasicToken(List<Property> properties) {
        log.info("basicToken case: mandatory parameters are required");
        final List<String> requiredProperties = ImmutableList.of(AUTH_URL, AUTH_HEADERS, AUTH_KEY, TOKEN_REF,
                SUBSYSTEM_CONNECTION_PROPERTY_USERNAME, SUBSYSTEM_CONNECTION_PROPERTY_PASSWORD);
        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_B_45, requiredProperty, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
            }
        }
    }

    /**
     * Check api key.
     *
     * @param properties the properties
     */
    private void checkApiKey(List<Property> properties) {
        log.info("apiKey case: mandatory parameters are required");
        final List<String> requiredProperties = ImmutableList.of(AUTH_KEY, TOKEN_REF);
        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_B_45, requiredProperty, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
            }
        }
    }

    /**
     * Check aws.
     *
     * @param properties the properties
     */
    private void checkAws(List<Property> properties) {
        log.info("aws case: mandatory parameters are required");
        final List<String> requiredProperties = ImmutableList.of(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_B_45, requiredProperty, String.valueOf(DOMAIN_ORCHESTRATOR.getType()) );
            }
        }
    }

    /**
     * Verify connection properties names unique.
     *
     * @param connectionProperties the connection properties
     */
    private void verifyConnectionPropertiesNamesUnique(final List<ConnectionProperties> connectionProperties) {
        log.debug("Verifying if all Connection Properties have unique names...");

        final List<Property> nameProperties = connectionProperties.stream()
                .map(ConnectionProperties::getProperties)
                .flatMap(Collection::stream)
                .filter(property -> property.getKey().equalsIgnoreCase(NAME))
                .collect(Collectors.toList());

        if (nameProperties.stream().distinct().count() < nameProperties.size()) {
            throw new MalformedContentException("SSM-B-30", nameProperties.toString());
        }
    }

    /**
     * Checks if is valid subsystem user.
     *
     * @param connPropsId the conn props id
     * @param subsystemId the subsystem id
     * @param subsystemUserId the subsystem user id
     */
    public void isValidSubsystemUser(final String connPropsId, final String subsystemId, final String subsystemUserId) {
        subsystemValidator.isSubsystemIdValid(subsystemId);
        isConnPropsIdValid(connPropsId);
        subsystemValidator.isSubsystemUserIdValid(subsystemUserId);
    }

    /**
     * Validate properties.
     *
     * @param properties the properties
     * @param subsystemType the subsystem type
     */
    private void validateProperties(final List<Property> properties, final SubsystemType subsystemType) {
        log.debug("Validating properties: {}...", properties);
        if (CollectionUtils.isEmpty(properties)) {
            throw new MalformedContentException(SSM_A_27, "properties");
        }

        if (subsystemType.getType().equalsIgnoreCase(AUTHENTICATION_SYSTEMS)) {
            verifRequriredpropertiespresentForAuthSystems(properties);
        } else {
            verifyRequiredPropertiesPresent(properties);
        }
        verifyNoDuplicateKeys(properties);
        verifyPropertiesLength(properties);
        log.debug("Validation of properties successful.");
    }

    /**
     * Verify required properties present.
     *
     * @param properties the properties
     */
    private void verifyRequiredPropertiesPresent(List<Property> properties) {
        final List<String> requiredProperties = ImmutableList.of(USERNAME, PW);

        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                throw new MalformedContentException(SSM_A_27, requiredProperty );
            }
        }
    }

    /**
     * Verify no duplicate keys.
     *
     * @param properties the properties
     */
    private void verifyNoDuplicateKeys(final List<Property> properties) {
        if (getPropertyKeys(properties).size() < properties.size()) {
            throw new MalformedContentException("SSM-B-32", listOfDuplicatedKeys(properties).toString());
        }
    }

    /**
     * Verify properties length.
     *
     * @param properties the properties
     */
    private void verifyPropertiesLength(final List<Property> properties) {
        for (Property property : properties) {
            if (!Pattern.matches(Constants.REGEX_CORRECT_KEY_LENGTH, property.getKey())
                    || !Pattern.matches(Constants.REGEX_CORRECT_VALUE_LENGTH, property.getValue())) {
                throw new MalformedContentException("SSM-B-33", property.getKey());
            }
        }
    }

    /**
     * Validate password and client secret fields if present.
     *
     * @param connectionProperty the connection property
     */
    private void validatePasswordAndClientSecretFieldsIfPresent(final ConnectionProperties connectionProperty) {
        final List<String> propertiesKeys = connectionProperty.getProperties().stream().map(Property::getKey).collect(Collectors.toList());

        for (String key : propertiesKeys) {
            if (PW.equalsIgnoreCase(key) || CLIENT_SECRET.equalsIgnoreCase(key)) {
                if (null == connectionProperty.getEncryptedKeys()) {
                    final List<String> encryptedKeys = new ArrayList<>();
                    connectionProperty.setEncryptedKeys(encryptedKeys);
                }
                if (connectionProperty.getEncryptedKeys().stream().noneMatch(key::equalsIgnoreCase)) {
                    connectionProperty.getEncryptedKeys().add(key);
                }
            }
        }
    }

    /**
     * Validate encrypted keys.
     *
     * @param connectionProperty the connection property
     */
    private void validateEncryptedKeys(final ConnectionProperties connectionProperty) {
        if (connectionProperty.getEncryptedKeys().contains("*")) {
            connectionProperty.setEncryptedKeys(Collections.singletonList("*"));
        } else {
            encryptedKeysNotExistCheck(connectionProperty);
        }
    }

    /**
     * Encrypted keys not exist check.
     *
     * @param connectionProperty the connection property
     */
    private void encryptedKeysNotExistCheck(final ConnectionProperties connectionProperty) {
        for (String encryptionKey : connectionProperty.getEncryptedKeys()) {
            boolean isKeyPresent = false;
            for (Property p : connectionProperty.getProperties()) {
                if (p.getKey().equalsIgnoreCase(encryptionKey)) {
                    isKeyPresent = true;
                    break;
                }
            }
            if (!isKeyPresent) {
                throw new MalformedContentException("SSM-B-34", encryptionKey);
            }
        }
    }

    /**
     * Gets the property keys.
     *
     * @param properties the properties
     * @return the property keys
     */
    private Set<String> getPropertyKeys(List<Property> properties) {
        return properties.stream()
                .map(Property::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * List of duplicated keys.
     *
     * @param properties the properties
     * @return the sets the
     */
    private Set<String> listOfDuplicatedKeys(List<Property> properties) {
        final List<String> list = new ArrayList<>();
        for(Property property:properties) {
            list.add(property.getKey());
        }
        return list.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                .entrySet().stream().filter(e->e.getValue() > 1)
                .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Verify required properties present for auth systems.
     *
     * @param properties the properties
     */
    private void verifRequriredpropertiespresentForAuthSystems(List<Property> properties) {
        final List<String> requiredProperties = ImmutableList.of(AUTH_URL, AUTH_TYPE, AUTH_HEADERS, CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);

        for (String requiredProperty : requiredProperties) {
            if (!getPropertyKeys(properties).contains(requiredProperty)) {
                final String errroMessage = Constants.AUTHENTICATION_SYSTEMS + " ( " + Constants.OAUTH2_CLIENT_CREDENTIALS + " )";
                throw new MalformedContentException(SSM_B_45, requiredProperty, errroMessage);
            }
        }
    }

}

