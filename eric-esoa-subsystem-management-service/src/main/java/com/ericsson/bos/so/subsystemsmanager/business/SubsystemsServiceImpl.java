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

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemsService;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemSubtypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.AdapterErrorException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.ApiKeyErrorException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.NameMustBeUniqueException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemCannotBeDeletedException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeDoesNotSupportException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeErrorException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.PropertyRepository;
import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import com.ericsson.bos.so.subsystemsmanager.business.util.EncryptDecryptConnectionPropertiesUtil;
import com.ericsson.bos.so.subsystemsmanager.business.util.PatchUtil;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.DeleteRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * The class SubsystemsServiceImpl
 */
@Component("subsystemsService")
@Service
@Slf4j
public class SubsystemsServiceImpl implements SubsystemsService {

    protected static final String API_KEY = "apiKey";
    protected static final String FILTERS = "filters";

    private static final String SUBSYSTEM_TYPE_ID = "subsystemTypeId";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String SUBTYPE = "subtype";
    private static final String SSM_B_53 = "SSM-B-53";
    private static final String SSM_B_54 = "SSM-B-54";
    private static final String SSM_B_52 = "SSM-B-52";
    private static final String SUBSYSTEM_TYPE = "subsystemType";

    @Autowired
    protected SubsystemDao subsystemDao;
    @Autowired
    protected EncryptDecryptConnectionPropertiesUtil cipherUtil;
    @Autowired
    protected SubsystemRequestValidator subsystemRequestValidator;
    @Autowired
    private ConnectionPropertiesRepository connectionPropertiesRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DeleteRequestValidator deleteRequestValidator;
    @Autowired
    private SubsystemValidator subsystemValidator;
    @Autowired
    private SubsystemTypeDao subsystemTypeDao;
    @Autowired
    private SubsystemSubtypeDao subsystemSubtypeDao;
    @Autowired
    private ConnectionPropertiesValidator connectionPropertiesValidator;
    @Autowired
    private PatchUtil patchUtil;
    @Autowired
    private EntityManager entityManager;

    @Override
    public Subsystem postSubsystem(final Subsystem subsystemRequest) {
        final Subsystem subsystem = buildSubsystemRequest(subsystemRequest);
        subsystemRequest.setConnectionProperties(
                cipherUtil.encryptOrDecryptProperties(subsystem.getConnectionProperties(), EncryptDecryptConnectionPropertiesUtil.ENCRYPT));
        return subsystemDao.saveSubsystem(subsystem);
    }

    @Override
    public List<Subsystem> getAllSubsystems(final String tenantName) {
        final List<Subsystem> subsystems = subsystemDao.getAllSubsystems();
        subsystems.forEach(subsystem ->
                subsystem.setConnectionProperties(
                    cipherUtil.encryptOrDecryptProperties(subsystem.getConnectionProperties(), EncryptDecryptConnectionPropertiesUtil.DECRYPT))
        );
        return subsystems;
    }

    @Override
    public Subsystem getSubsystemById(String subsystemId, String tenantName) {
        log.info("SubsystemsManagerServiceImpl.getSubsystemById() request received for subsystem id : {}", subsystemId);
        final Subsystem subsystem = subsystemValidator.isSubsystemIdValid(subsystemId);
        final List<ConnectionProperties> decryptedConnectionProperties = cipherUtil.encryptOrDecryptProperties(subsystem.getConnectionProperties(),
                EncryptDecryptConnectionPropertiesUtil.DECRYPT);
        subsystem.setConnectionProperties(decryptedConnectionProperties);
        return subsystem;
    }

    @Override
    public boolean deleteSubsystemById(final String subsystemId) {
        log.info("SubsystemsManagerServiceImpl.deleteSubsystemById() request received for subsystem id : {}", subsystemId);
        boolean isDeleted = false;
        SubsystemRequestValidator.idNotNullCheck(subsystemId);
        final Long ssId = Long.parseLong(subsystemId);
        final Subsystem subsystem = subsystemValidator.isSubsystemIdValidNoException(subsystemId);
        if (subsystem == null) {
            return false;
        }
        if (Boolean.TRUE.equals(deleteRequestValidator.isSubsystemInUse(subsystemId))) {
            final String subsystemName = subsystemDao.findSubsystemByIdWithException(ssId).getName();
            throw new SubsystemCannotBeDeletedException(subsystemName);
        } else {
            subsystemDao.deleteSubsystemById(ssId);
            isDeleted = true;
        }
        return isDeleted;
    }

    /**
     * updates patch on an existing subsystem and saves to database
     * @param subsystemId String
     * @param patchRequestFields Map
     * @return patched subsystem
     */
    public Subsystem patchSubsystem(String subsystemId, Map<String, Object> patchRequestFields) {
        log.info("Request received for patch subsystem Id:{}", subsystemId);
        subsystemRequestValidator.isValidPatchRequest(subsystemId, patchRequestFields);

        final Subsystem currentSubsystem = subsystemValidator.isSubsystemIdValid(subsystemId);
        validatePatchRequestFields(patchRequestFields, currentSubsystem);

        final Subsystem patchedSubsystem = updateSubsystem(subsystemId, patchRequestFields, currentSubsystem);
        validatePatchedSubsystem(patchedSubsystem);

        if (patchedSubsystem.getConnectionProperties() != null) {
            patchedSubsystem.setConnectionProperties(cipherUtil.encryptOrDecryptProperties(patchedSubsystem.getConnectionProperties(),
                    EncryptDecryptConnectionPropertiesUtil.ENCRYPT));
        } else {
            log.info("connection property is empty or null");
        }
        return subsystemDao.saveSubsystem(patchedSubsystem);
    }

    /**
     * clears ghost connection properties
     */
    @Transactional
    public void clearGhostConnnectionProperties() {
        final List<Property> listOfProperties = propertyRepository.findAll();
        deleteProperty(listOfProperties);
        final List<ConnectionProperties> listOfConnProp = connectionPropertiesRepository.findAll();
        deleteConnectionProperty(listOfConnProp);
    }

    private Subsystem buildSubsystemRequest(Subsystem subsystemRequest) {
        final SubsystemType subsystemTypeRequest = subsystemRequest.getSubsystemType();
        final Subsystem subsystem = subsystemRequest.toBuilder().build();

        if (!ObjectUtils.isEmpty(subsystemTypeRequest.getId())) {
            subsystem.setSubsystemType(subsystemTypeDao.findById(subsystemTypeRequest.getId()));
        } else {
            subsystem.setSubsystemType(subsystemTypeDao.findByType(subsystemTypeRequest.getType()));
        }

        subsystem.setSubsystemTypeId(subsystem.getSubsystemType().getId());

        if (!ObjectUtils.isEmpty(subsystemTypeRequest.getSubtype())) {
            if (!ObjectUtils.isEmpty(subsystemTypeRequest.getSubtype().getId())) {
                subsystem.getSubsystemType().setSubtype(subsystemSubtypeDao.findById(subsystemTypeRequest.getSubtype().getId()));
            } else {
                subsystem.getSubsystemType().setSubtype(subsystemSubtypeDao.findByName(subsystemTypeRequest.getSubtype().getName()));
            }
            subsystem.setSubsystemSubtypeId(subsystem.getSubsystemType().getSubtype().getId());
        }
        return subsystem;
    }

    private void validatePatchedSubsystem(final Subsystem subsystem) {
        subsystemRequestValidator.validateRequiredFieldsPresent(subsystem);
        connectionPropertiesValidator.validateConnectionProperties(subsystem.getConnectionProperties(), subsystem.getSubsystemType());
    }

    @SuppressWarnings("unchecked")
    private void validatePatchRequestFields(Map<String, Object> patchRequestFields, Subsystem subsystem) {
        if (patchRequestFields.containsKey(Constants.NAME) && !subsystem.getName().equals(patchRequestFields.get(Constants.NAME))
                && subsystemDao.existsByName(patchRequestFields.get(Constants.NAME).toString())) {
            throw new NameMustBeUniqueException(patchRequestFields.get(Constants.NAME).toString());
        }

        if (patchRequestFields.containsKey(Constants.ADAPTER_LINK_NAME) && (patchRequestFields.get(Constants.ADAPTER_LINK_NAME) == null
                || isCurrentAdapterLinkNotEmptyOrNull(subsystem.getAdapterLink()) && isPatchRequestAdapterLinkNotEqualCurrentAdapterLink(
                        patchRequestFields.get(Constants.ADAPTER_LINK_NAME).toString(), subsystem.getAdapterLink()))) {
            throw new AdapterErrorException("SSM-B-41");
        }

        if (patchRequestFields.containsKey(SUBSYSTEM_TYPE_ID)) {
            final Object subsystemTypeObject = patchRequestFields.get(SUBSYSTEM_TYPE_ID);
            final Long subsystemTypeId = Long.valueOf(subsystemTypeObject.toString());
            subsystemValidator.isSubsystemTypeIdValid(subsystemTypeId);
        }

        if (patchRequestFields.containsKey(API_KEY) && !subsystem.getApiKey().toString().equals(patchRequestFields.get(API_KEY))) {
            throw new ApiKeyErrorException(SSM_B_52, API_KEY);
        }

        if (patchRequestFields.containsKey(SUBSYSTEM_TYPE) && isPatchRequestSubsystemTypeNotEqualCurrentSubsystemType(
                (Map<String, Object>) patchRequestFields.get(SUBSYSTEM_TYPE), subsystem.getSubsystemType())) {
            throw new SubsystemTypeErrorException(SSM_B_52, SUBSYSTEM_TYPE);
        }

    }

    private Subsystem updateSubsystem(final String subsystemId, final Map<String, Object> updateRequestFields, final Subsystem subsystem) {
        subsystemRequestValidator.isValidUpdateRequest(subsystemId, updateRequestFields, subsystem);
        final Long ssId = Long.valueOf(subsystemId);
        Map<String, Object> subsystemMap = new ObjectMapper().convertValue(subsystem, new TypeReference<Map<String, Object>>() {
        });
        subsystemMap = patchUtil.applyPatch(subsystemMap, updateRequestFields);
        final Subsystem subsystemObj = new ObjectMapper().convertValue(subsystemMap, Subsystem.class);
        subsystemObj.setId(ssId);
        subsystemValidator.isSubsystemTypeIdValid(subsystemObj.getSubsystemType().getId());
        return subsystemObj;
    }

    private boolean isCurrentAdapterLinkNotEmptyOrNull(final String adapterLink) {
        return adapterLink != null && !adapterLink.isEmpty();
    }

    private boolean isPatchRequestAdapterLinkNotEqualCurrentAdapterLink(final String patchAl, final String currentAl) {
        return !patchAl.equals(currentAl);
    }

    @SuppressWarnings("unchecked")
    private boolean isPatchRequestSubsystemTypeNotEqualCurrentSubsystemType(Map<String, Object> patchSubsystemType, SubsystemType subsystemType) {

        if (patchSubsystemType == null) {
            return true;
        }
        if (patchSubsystemType.containsKey(ID) && !subsystemType.getId().toString().equals(patchSubsystemType.get(ID).toString())) {
            return true;
        }
        if (patchSubsystemType.containsKey(TYPE) && !subsystemType.getType().equals(patchSubsystemType.get(TYPE))) {
            return true;
        }
        if (patchSubsystemType.containsKey(SUBTYPE)) {
            if (!ObjectUtils.isEmpty(subsystemType.getSubtype())) {
                validateSubsystemSubtype((Map<String, Object>) patchSubsystemType.get(SUBTYPE), subsystemType.getSubtype());
            } else {
                throw new SubsystemSubtypeDoesNotSupportException(SSM_B_53, subsystemType.getType());
            }
        }

        if (!patchSubsystemType.containsKey(SUBTYPE) && !ObjectUtils.isEmpty(subsystemType.getSubtype())) {
            throw new MalformedContentException(SSM_B_54, subsystemType.getType());
        }

        return false;
    }

    private void validateSubsystemSubtype(Map<String, Object> patchSubtype, Subtype subtype) {
        if (ObjectUtils.isEmpty(patchSubtype) || isPatchRequestSubtypeNotEqualCurrentSubtype(patchSubtype, subtype)) {
            throw new SubsystemTypeErrorException(SSM_B_52, SUBTYPE);
        }
    }

    private boolean isPatchRequestSubtypeNotEqualCurrentSubtype(Map<String, Object> patchSubtype, Subtype subtype) {
        return (patchSubtype.containsKey(ID) && !subtype.getId().toString().equals(patchSubtype.get(ID).toString())
                || patchSubtype.containsKey(NAME) && !subtype.getName().equals(patchSubtype.get(NAME)));
    }

    private void deleteProperty(List<Property> listOfProperties) {
        for (Property prop : listOfProperties) {
            entityManager.refresh(prop);
            log.debug("check final clean : property = {}   prop.getConnectionProperties() = {}", prop.getId(), prop.getConnectionProperties());
            if (prop.getConnectionProperties() == null) {
                log.debug("remove prop with id = {}", prop.getId());
                propertyRepository.delete(prop);
            }
        }
    }

    private void deleteConnectionProperty(List<ConnectionProperties> listOfConnProp) {
        for (ConnectionProperties connProp : listOfConnProp) {
            entityManager.refresh(connProp);
            if ((connProp.getSubsystemId() == null) || (connProp.getSubsystem() == null)) {
                connectionPropertiesRepository.delete(connProp);
            }
        }
    }

}
