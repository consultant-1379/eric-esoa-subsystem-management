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

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.VENDOR_NAME;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.ConnectionPropertiesDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemUserDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemUserRepository;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;

/**
 * The Class SubsystemValidator.
 */
@Component
public class SubsystemValidator {

    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    @Autowired
    private SubsystemDao subsystemDao;

    @Autowired
    private SubsystemTypeDao subsystemTypeDao;

    @Autowired
    private SubsystemUserRepository subsystemUserRepository;

    /**
     * Checks if subsystem id is valid.
     *
     * @param id the id
     * @return the subsystem
     */
    public Subsystem isSubsystemIdValid(final String id) {
        SubsystemRequestValidator.idNotNullCheck(id);
        if (SubsystemRequestValidator.isValidUUID(id)) {
            return subsystemDao.findSubsystemByApiKeyWithException(UUID.fromString(id));
        } else {
            return subsystemDao.findSubsystemByIdWithException(Long.valueOf(id));
        }
    }

    /**
     * Checks if subsystem id is valid and not null.
     *
     * @param subsystemId the subsystem id
     * @return the subsystem
     */
    public Subsystem isSubsystemIdValidNoException(final String subsystemId) {
        SubsystemRequestValidator.idNotNullCheck(subsystemId);
        return subsystemDao.findSubsystemById(Long.valueOf(subsystemId));
    }

    /**
     * Checks if subsystem is valid.
     *
     * @param subsystem the subsystem
     */
    public void isValidSubsystem(final Subsystem subsystem) {
        if (subsystem != null) {
            SubsystemRequestValidator.idNotNullCheck(subsystem.getId());
        } else {
            throw new MalformedContentException("SSM-B-25","subsystem");
        }
    }

    /**
     * Checks if subsystemtype id is valid.
     *
     * @param subsystemTypeId the subsystem type id
     * @return the subsystem type
     */
    public SubsystemType isSubsystemTypeIdValid(final Long subsystemTypeId) {
        SubsystemRequestValidator.idNotNullCheck(subsystemTypeId);
        return subsystemTypeDao.findById(subsystemTypeId);
    }


    /**
     * Checks whether nfvo adapterLink is null or empty.
     *
     * @param adapterLink the adapter link
     * @param subsystemType the subsystem type
     * @return true, if successful
     */
    private boolean nfvoWithAdapterLinkNullorEmpty(final String adapterLink, final SubsystemType subsystemType) {
        return  (PredefinedSubsystemType.NFVO.toString().equalsIgnoreCase(subsystemType.getType())
            && (adapterLink == null || adapterLink.trim().isEmpty()));
    }

    /**
     * Checks whether domain manager adapter link null or empty and vendor.
     *
     * @param adapterLink the adapter link
     * @param subsystemType the subsystem type
     * @param vendor the vendor
     * @return true, if successful
     */
    private boolean domainManagerWithAdapterLinkNullorEmptyAndVendor(final String adapterLink, final SubsystemType subsystemType
            , final String vendor) {
        return (PredefinedSubsystemType.DOMAIN_MANAGER.toString().equalsIgnoreCase(subsystemType.getType())
                && (adapterLink == null || adapterLink.trim().isEmpty())
                && (VENDOR_NAME.equals(vendor)));
    }

    /**
     * Validates the subsystem adapter link.
     *
     * @param adapterLink the adapter link
     * @param subsystemType the subsystem type
     * @param vendor the vendor
     */
    public void validateSubsystemAdapterLink(final String adapterLink, final SubsystemType subsystemType, final String vendor) {
        if (nfvoWithAdapterLinkNullorEmpty(adapterLink, subsystemType) ||
            domainManagerWithAdapterLinkNullorEmptyAndVendor(adapterLink, subsystemType, vendor))    {
            throw new MalformedContentException("SSM-B-28", subsystemType.getType());
        }
    }

    /**
     * Checks if subsystem user id is valid.
     *
     * @param subsystemUserId the subsystem user id
     * @return the subsystem user
     */
    public SubsystemUser isSubsystemUserIdValid(final String subsystemUserId) {
        SubsystemRequestValidator.idNotNullCheck(subsystemUserId);
        final Optional<SubsystemUser> subsystemUserOpt = subsystemUserRepository.findById(Long.valueOf(subsystemUserId));
        return subsystemUserOpt.orElseThrow(
                () -> new SubsystemUserDoesNotExistException(subsystemUserId));
    }

    /**
     * Checks if connection properties id is valid.
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
}
