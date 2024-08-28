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
package com.ericsson.bos.so.subsystemsmanager.business.util;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.ENCRYPTED;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.ENCRYPT_ALL;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class PatchUtil.
 */
@Slf4j
@Component
public class PatchUtil {

    private static final List<String> LIST_OF_CONN_PROP_PARAM = Arrays.asList("username", "password", "tenantName", "encryptedKeys", "subsystemUsers",
            "name", "subsystemId", "Id");
    private List<LinkedHashMap<String, Object>> newPropertyList;
    private HashSet<String> encryptedKeys;
    private ArrayList<LinkedHashMap<String, Object>> propertyList;

    /**
     * Apply patch.
     *
     * @param resource the resource
     * @param patchRequestFields the patch request fields
     * @return the map
     */
    public Map<String, Object> applyPatch(final Map<String, Object> resource, final Map<String, Object> patchRequestFields) {
        encryptedKeys = getPatchedEncryptedKeys(patchRequestFields);
        newPropertyList = new ArrayList<>();
        propertyList = new ArrayList<>();
        for (Entry<String, Object> patchField : patchRequestFields.entrySet()) {
            if (Constants.PW.equalsIgnoreCase(patchField.getKey()) || Constants.CLIENT_SECRET.equalsIgnoreCase(patchField.getKey())) {
                encryptedKeys.add(patchField.getKey());
            }
            handleSubsystemField(resource, patchField);
        }
        /*
         * Add the AdapterLink to the resource, only if it is present in the patch fields and it isn't already present in the resource fields.
         */
        if (resource.get(Constants.ADAPTER_LINK_NAME) == null && patchRequestFields.get(Constants.ADAPTER_LINK_NAME) != null) {
            resource.put(Constants.ADAPTER_LINK_NAME, patchRequestFields.get(Constants.ADAPTER_LINK_NAME));
        }

        if (newPropertyList != null) {
            propertyList.addAll(newPropertyList);
        }
        patchEncryptedKeys();
        final List<String> encryptedKeysList = new ArrayList<>(encryptedKeys);
        resource.put(Constants.PROPERTIES, propertyList);
        resource.put(Constants.ENCRYPTED_KEYS, encryptedKeysList);
        return resource;
    }

    /**
     * Apply patch for connection properties.
     *
     * @param resource the resource
     * @param patchRequestFields the patch request fields
     * @return the map
     */
    public Map<String, Object> applyPatchForConn(final Map<String, Object> resource, final Map<String, Object> patchRequestFields) {
        encryptedKeys = getPatchedEncryptedKeys(patchRequestFields);
        newPropertyList = new ArrayList<>();
        propertyList = new ArrayList<>();
        for (Entry<String, Object> patchField : patchRequestFields.entrySet()) {
            if (patchField.getKey().equalsIgnoreCase(Constants.PW)) {
                encryptedKeys.add(Constants.PW);
            }
            if (!(resource.containsKey(patchField.getKey())) && (LIST_OF_CONN_PROP_PARAM.contains(patchField.getKey()))) {
                resource.put(patchField.getKey(), patchField.getValue());
            }
            if (!(LIST_OF_CONN_PROP_PARAM.contains(patchField.getKey()))) {
                log.warn("the parameter {} in the connection property PATCH request is wrong and it won't be added", patchField.getKey());
            }
            for (final Entry<String, Object> subsystemField : resource.entrySet()) {

                if (subsystemField.getKey().equals(patchField.getKey())) {
                    subsystemField.setValue(patchField.getValue());
                    break;
                }
            }
        }
        patchEncryptedKeys();
        final List<String> encryptedKeysList = new ArrayList<>(encryptedKeys);
        resource.put(Constants.ENCRYPTED_KEYS, encryptedKeysList);
        return resource;
    }

    private void patchPropertiesObjects(Entry<String, Object> subsystemField, Entry<String, Object> patchField) {
        boolean isPropertyPresent = false;
        propertyList = (ArrayList<LinkedHashMap<String, Object>>) subsystemField.getValue();

        for (LinkedHashMap<String, Object> propertyObject : propertyList) {
            final String propertyKey = (String) propertyObject.get(Constants.KEY);

            if (propertyKey.equalsIgnoreCase(patchField.getKey())) {
                propertyObject.put(VALUE, patchField.getValue());
                isPropertyPresent = true;
            }
        }

        if (!isPropertyPresent && !Constants.ENCRYPTED_KEYS.equalsIgnoreCase(patchField.getKey())) {
            if (newPropertyList == null) {
                newPropertyList = new ArrayList<>();
            }
            newPropertyList.add(createNewProperty(patchField));
        }
    }

    private LinkedHashMap<String, Object> createNewProperty(Entry<String, Object> patchField) {
        final LinkedHashMap<String, Object> property = new LinkedHashMap<>();
        property.put(Constants.KEY, patchField.getKey());
        property.put(VALUE, patchField.getValue());
        property.put(ENCRYPTED, false);
        return property;
    }

    private void patchEncryptedKeys() {
        if (propertyList != null) {
            for (LinkedHashMap<String, Object> property : propertyList) {
                if (encryptedKeys != null && encryptedKeys.contains(ENCRYPT_ALL)) {
                    property.put(ENCRYPTED, true);
                } else if (encryptedKeys != null) {
                    final String propertyKey = (String) property.get(Constants.KEY);
                    if (encryptedKeys.contains(propertyKey)) {
                        property.put(ENCRYPTED, true);
                    }
                }
            }
        }
    }

    private HashSet<String> getPatchedEncryptedKeys(Map<String, Object> patchRequestFields) {
        if (patchRequestFields.get(Constants.ENCRYPTED_KEYS) != null) {
            return new HashSet<>((List<String>) patchRequestFields.get(Constants.ENCRYPTED_KEYS));
        }
        return new HashSet<>();
    }

    private void handleSubsystemField(final Map<String, Object> resource, Entry<String, Object> patchField) {
        for (final Entry<String, Object> subsystemField : resource.entrySet()) {
            if ((subsystemField.getKey().equals(patchField.getKey())) && ("connectionProperties".equals(patchField.getKey()))) {
                patchWR(patchField);
            }
            if (subsystemField.getKey().equals(patchField.getKey())) {
                subsystemField.setValue(patchField.getValue());
                break;
            }

            if (Constants.PROPERTIES.equalsIgnoreCase(subsystemField.getKey())) {
                patchPropertiesObjects(subsystemField, patchField);
            }
        }
    }

    private void patchWR(Entry<String, Object> patchField) {
        int i = 0;
        final List<Object> prop = (List<Object>) patchField.getValue();
        for (Object property : prop) {
            final LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) property;
            final Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                if ("subsystemUsers".equals(key) && "".equals(map.get(key))) {
                    map.replace(key, new ArrayList<Object>());
                    ((List<Object>) patchField.getValue()).set(i, map);
                }
            }
            i++;
        }
    }
}
