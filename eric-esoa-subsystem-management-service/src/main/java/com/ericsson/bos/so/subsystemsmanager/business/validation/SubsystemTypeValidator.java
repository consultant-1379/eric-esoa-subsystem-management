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

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.AdapterErrorException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemInUseException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeDoesNotSupportException;
import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

import static com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType.*;

/**
 * The Class SubsystemTypeValidator.
 */
@Slf4j
@Component
public class SubsystemTypeValidator {

    public static final String ERICSSON_VENDOR = "Ericsson";

    public static final long MAX_INVENTORY_SYSTEM_INSTANCES = 1;

    public static final long MAX_NFVO_INSTANCES = 1;

    public static final long MAX_CONNECTION_PROPERTIES = 1;

    private static final String SSM_B_53 = "SSM-B-53";

    @Autowired
    private SubsystemDao subsystemDao;

    @Autowired
    private AdaptersLinksService adaptersLinksService;

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    /**
     * Checks if the vendor field is required.
     *
     * @param subsystemType the subsystem type
     * @return true, if is vendor field required
     */
    public static boolean isVendorFieldRequired(String subsystemType) {
        return !Arrays.asList(PHYSICAL_DEVICE.toString(), CM_GATEWAY.toString(), INVENTORY_SYSTEM.toString(), Constants.AUTHENTICATION_SYSTEMS)
                .contains(subsystemType);
    }

    /**
     * Checks if the url field required.
     *
     * @param subsystemType the subsystem type
     * @return true, if is url field required
     */
    public static boolean isUrlFieldRequired(String subsystemType) {
        return !subsystemType.equals(Constants.AUTHENTICATION_SYSTEMS);
    }

    /**
     * Validate subsystem type.
     *
     * @param subsystem the subsystem
     */
    public void validateSubsystemType(final Subsystem subsystem) {
        final String subsystemType = subsystem.getSubsystemType().getType();
        log.info("Performing subsystem type specific validation for: [{}] of type: [{}]...", subsystem.getName(), subsystemType);

        if (subsystemType.equalsIgnoreCase(NFVO.getType())) {
            validateNfvoType(subsystem);
        } else if (subsystemType.equalsIgnoreCase(INVENTORY_SYSTEM.getType())) {
            verifyInventorySystemSubsystemsLimit();
        } else if (subsystemType.equalsIgnoreCase(DOMAIN_MANAGER.getType()) || subsystemType.equalsIgnoreCase(Constants.AUTHENTICATION_SYSTEMS)) {
            verifySubsystemConnectionPropertiesLimit(subsystem);
        } else if (subsystemType.equalsIgnoreCase(DOMAIN_ORCHESTRATOR.getType())) {
            validateDomainOrchestratorType(subsystem);
        }
        validateSubtypeForSubsystemTypeIfPresent(subsystem.getSubsystemType());
    }

    /**
     * Validate NFVO type.
     *
     * @param subsystem the subsystem
     */
    private void validateNfvoType(Subsystem subsystem) {
        log.debug("Verifying if subsystem [{}] conforms to NFVO subsystem type rules...", subsystem.getName());
        verifyNfvoRequiredProperties(subsystem);
        verifyExistAdapterLink(subsystem,NFVO.getType());
        verifyNfvoSubsystemsLimit();
    }

    /**
     * Verify nfvo required properties.
     *
     * @param subsystem the subsystem
     */
    private void verifyNfvoRequiredProperties(Subsystem subsystem) {
        for (ConnectionProperties connectionProperties : subsystem.getConnectionProperties()) {
            ConnectionPropertiesValidator.verifyPropertyExists(connectionProperties, ConnectionPropertiesValidator.NAME_PROPERTY);
        }

        if (subsystem.getVendor().equalsIgnoreCase(ERICSSON_VENDOR)) {
            for (ConnectionProperties connectionProperties : subsystem.getConnectionProperties()) {
                ConnectionPropertiesValidator.verifyPropertyExists(connectionProperties, ConnectionPropertiesValidator.TENANT_PROPERTY);
            }
        }
    }

    /**
     * Verify nfvo subsystems limit.
     */
    private void verifyNfvoSubsystemsLimit() {
        if (subsystemDao.countBySubsystemType(NFVO.getType()) >= MAX_NFVO_INSTANCES) {
            throw new SubsystemInUseException("SSM-H-14", NFVO.getType(), String.valueOf(MAX_NFVO_INSTANCES) , NFVO.getType());
        }
    }

    /**
     * Validate domain orchestrator type.
     *
     * @param subsystem the subsystem
     */
    private void validateDomainOrchestratorType(final Subsystem subsystem) {
        log.info("Verifying if subsystem [{}] conforms to Domain Orchestrator subsystem type rules...", subsystem.getName());
        verifySubsystemConnectionPropertiesLimit(subsystem);
        verifyExistAdapterLink(subsystem, DOMAIN_ORCHESTRATOR.getType());
    }

    /**
     * Verify subsystem connection properties limit.
     *
     * @param subsystem the subsystem
     */
    private void verifySubsystemConnectionPropertiesLimit(Subsystem subsystem) {
        if (subsystem.getConnectionProperties().size() > MAX_CONNECTION_PROPERTIES) {
            throw new MalformedContentException("SSM-B-37", subsystem.getName(), subsystem.getSubsystemType().getType());
        }
    }

    /**
     * Verify inventory system subsystems limit.
     */
    private void verifyInventorySystemSubsystemsLimit() {
        if (subsystemDao.countBySubsystemType(INVENTORY_SYSTEM.getType()) >= MAX_INVENTORY_SYSTEM_INSTANCES) {
            throw new SubsystemInUseException("SSM-H-14", INVENTORY_SYSTEM.getType(), String.valueOf(MAX_INVENTORY_SYSTEM_INSTANCES)
                    , INVENTORY_SYSTEM.getType());
        }
    }

    /**
     * Verify exist adapter link.
     *
     * @param subsystem the subsystem
     * @param subsystemType the subsystem type
     */
    private void verifyExistAdapterLink(Subsystem subsystem, String subsystemType) {
        final String adapterLink = subsystem.getAdapterLink();
        if(!StringUtils.hasText(subsystem.getAdapterLink())) {
            log.error("AdapterLink is mandatory for subsystemType {}", subsystemType);
            throw new AdapterErrorException("SSM-B-28", subsystem.getName());
        }

        final List<String> lisOfExistingAdapterLink = adaptersLinksService.fetchAdapterLinksByType(subsystemType);
        boolean adapterLinkFound = false;

        for(String adapterLinkTmp: lisOfExistingAdapterLink) {
            if(adapterLinkTmp.contentEquals(adapterLink)) {
                adapterLinkFound = true;
                break;
            }
        }

        if (!adapterLinkFound) {
            log.error("Requested AdapterLink not found: {}", adapterLink);
            throw new AdapterErrorException("SSM-J-42", adapterLink);
        }
    }

    /**
     * Validate subtype for subsystem type if present.
     *
     * @param subsystemTypeRequest the subsystem type request
     */
    private void validateSubtypeForSubsystemTypeIfPresent(final SubsystemType subsystemTypeRequest) {
        if (!ObjectUtils.isEmpty(subsystemTypeRequest.getSubtype())) {
            final Subtype subtypeRequest = subsystemTypeRequest.getSubtype();
            final SubsystemType subsystemType = subsystemTypeDao.findByType(subsystemTypeRequest.getType());
            if (CollectionUtils.isEmpty(subsystemType.getSubtypes())) {
                throw new SubsystemSubtypeDoesNotSupportException(SSM_B_53, subsystemType.getType());
            }
            subsystemType.getSubtypes().stream()
                    .filter(subtype -> (subtype.getId().equals(subtypeRequest.getId()) || subtype.getName().equals(subtypeRequest.getName())))
                    .findAny().orElseThrow(() -> new SubsystemSubtypeDoesNotExistException(subsystemTypeRequest.getType(),
                            ObjectUtils.isEmpty(subtypeRequest.getId()) ? subtypeRequest.getName() : subtypeRequest.getId()));
        }
    }

}
