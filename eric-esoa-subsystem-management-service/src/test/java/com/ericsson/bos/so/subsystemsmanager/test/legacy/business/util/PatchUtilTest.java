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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import com.ericsson.bos.so.subsystemsmanager.business.util.PatchUtil;

/**
 * The Class PatchUtilTest.
 */
public class PatchUtilTest {

    @InjectMocks
    private PatchUtil patchUtil;

    private final Map<String, Object> resource = new HashMap<String, Object>();

    private final Map<String, Object> patchRequestFields = new HashMap<String, Object>();

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Given valid resource and patch data when patch util is called then resource value is set.
     */
    @Test
    public void givenValidResourceAndPatchData_whenPatchUtilIsCalled_thenResourceValueIsSet() {
        setUpValidResourceAndPatchData();
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        assertEquals(Constants.THREE, res.get(Constants.SUBSYSTEM_ID));
        assertEquals(Constants.FOUR, res.get(Constants.SUBSYSTEM_TYPE_ID));
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList =
                (ArrayList<LinkedHashMap<String, Object>>) res.get(Constants.PROPERTIES);
        final String key = (String) propertiesHashMapArrayList.get(0).get(Constants.VALUE);
        assertEquals(Constants.PATCHED_USERNAME, key);
    }

    /**
     * Given valid resource and patch data when patch util is called then resource value is set and two new fields added and two encrypted.
     */
    @Test
    public void givenValidResourceAndPatchData_whenPatchUtilIsCalled_thenResourceValueIsSetAndTwoNewFieldsAddedAndTwoEncrypted() {
        setUpValidResourceAndPatchData_withTwoNewProperties();
        patchRequestFields.put(Constants.ENCRYPTED_KEYS, Arrays.asList(Constants.USERNAME, Constants.TOKEN));
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        assertEquals(Constants.THREE, res.get(Constants.SUBSYSTEM_ID));
        assertEquals(Constants.FOUR, res.get(Constants.SUBSYSTEM_TYPE_ID));
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList =
                (ArrayList<LinkedHashMap<String, Object>>) res.get(Constants.PROPERTIES);
        assertEquals(Constants.PATCHED_USERNAME, propertiesHashMapArrayList.get(0).get(Constants.VALUE));
        assertEquals(3, propertiesHashMapArrayList.size());
        assertTrue((Boolean) propertiesHashMapArrayList.get(0).get(Constants.ENCRYPTED));
        assertFalse((Boolean) propertiesHashMapArrayList.get(1).get(Constants.ENCRYPTED));
        assertTrue((Boolean) propertiesHashMapArrayList.get(2).get(Constants.ENCRYPTED));
    }

    /**
     * Given valid resource data when patch util is called with no properties only encrypt all in encrypted keys list then encrypt all props.
     */
    @Test
    public void givenValidResourceData_WhenPatchUtilIsCalledWithNoPropertiesOnlyEncryptAllInEncryptedKeysList_thenEncryptAllProps() {
        setUpValidResourceWithThreeProperties();
        patchRequestFields.put(Constants.ENCRYPTED_KEYS, Arrays.asList(Constants.ENCRYPT_ALL));
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList =
                (ArrayList<LinkedHashMap<String, Object>>) res.get(Constants.PROPERTIES);
        assertEquals(3, propertiesHashMapArrayList.size());
        assertTrue((Boolean) propertiesHashMapArrayList.get(0).get(Constants.ENCRYPTED));
        assertTrue((Boolean) propertiesHashMapArrayList.get(1).get(Constants.ENCRYPTED));
        assertTrue((Boolean) propertiesHashMapArrayList.get(2).get(Constants.ENCRYPTED));
    }

    /**
     * Given patch data with valid adapter link when patch util is called then resource value is updated.
     */
    @Test
    public void givenPatchDataWithValidAdapterLink_whenPatchUtilIsCalled_thenResourceValueIsUpdated() {
        setUpValidAdapterLinkAndPatchData();
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        assertEquals(Constants.THREE, res.get(Constants.SUBSYSTEM_ID));
        assertEquals(Constants.FOUR, res.get(Constants.SUBSYSTEM_TYPE_ID));
        assertEquals(Constants.ADAPTER_LINK_NAME, res.get(Constants.ADAPTER_LINK_NAME));
    }

    /**
     * Given valid resource and patch data when patch util is called then resource value is set and encrypt all.
     */
    @Test
    public void givenValidResourceAndPatchData_whenPatchUtilIsCalled_thenResourceValueIsSetAndEncryptAll() {
        setUpValidResourceAndPatchData_withTwoNewProperties();
        patchRequestFields.put(Constants.ENCRYPTED_KEYS, Arrays.asList(Constants.ENCRYPT_ALL));
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        assertEquals(Constants.THREE, res.get(Constants.SUBSYSTEM_ID));
        assertEquals(Constants.FOUR, res.get(Constants.SUBSYSTEM_TYPE_ID));
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList =
                (ArrayList<LinkedHashMap<String, Object>>) res.get(Constants.PROPERTIES);
        assertEquals(Constants.PATCHED_USERNAME, propertiesHashMapArrayList.get(0).get(Constants.VALUE));
        assertEquals(3, propertiesHashMapArrayList.size());
        assertTrue((Boolean) propertiesHashMapArrayList.get(0).get(Constants.ENCRYPTED));
        assertTrue((Boolean) propertiesHashMapArrayList.get(1).get(Constants.ENCRYPTED));
        assertTrue((Boolean) propertiesHashMapArrayList.get(2).get(Constants.ENCRYPTED));
    }

    /**
     * Given valid resource and empty patch data when patch util is called then resource keep origin value.
     */
    @Test
    public void givenValidResourceAndEmptyPatchData_whenPatchUtilIsCalled_thenResourceKeepOriginValue() {
        setUpValidResourceAndEmptyPatchData();
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        assertEquals(Constants.ONE, res.get(Constants.SUBSYSTEM_ID));
        assertEquals(Constants.TWO, res.get(Constants.SUBSYSTEM_TYPE_ID));
    }

    /**
     * Given unmatched resource and patch data when patch util is called then resource keep origin value.
     */
    @Test
    public void givenUnmatchedResourceAndPatchData_whenPatchUtilIsCalled_thenResourceKeepOriginValue() {
        setUpUnmatchedResourceAndPatchData();
        final Map<String, Object> res = patchUtil.applyPatch(resource, patchRequestFields);
        assertEquals(Constants.ONE, res.get(Constants.SUBSYSTEM_ID));
        assertEquals(Constants.TWO, res.get(Constants.SUBSYSTEM_TYPE_ID));
        assertEquals(null, res.get(Constants.ONE));
        assertEquals(null, res.get(Constants.NAME));
    }

    /**
     * Test new property gets added.
     */
    @Test
    public void testNewPropertyGetsAdded() {
        setUpValidResourceAndPatchData();
        patchRequestFields.put(Constants.NEW_PROPERTY, Constants.NEW_PROPERTY1);
        final Map<String, Object> result = patchUtil.applyPatch(resource, patchRequestFields);
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList =
                (ArrayList<LinkedHashMap<String, Object>>) result.get(Constants.PROPERTIES);
        assertEquals(2, propertiesHashMapArrayList.size());
    }

    /**
     * Sets up the valid resource and patch data.
     */
    private void setUpValidResourceAndPatchData() {
        resource.put(Constants.SUBSYSTEM_ID, Constants.ONE);
        resource.put(Constants.SUBSYSTEM_TYPE_ID, Constants.TWO);
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList = new ArrayList<>();
        propertiesHashMapArrayList.add(new LinkedHashMap<>());
        propertiesHashMapArrayList.get(0).put(Constants.KEY, Constants.USERNAME);
        propertiesHashMapArrayList.get(0).put(Constants.VALUE, Constants.OLD_USERNAME);
        propertiesHashMapArrayList.get(0).put(Constants.ENCRYPTED, false);
        resource.put(Constants.PROPERTIES, propertiesHashMapArrayList);
        patchRequestFields.put(Constants.SUBSYSTEM_ID, Constants.THREE);
        patchRequestFields.put(Constants.SUBSYSTEM_TYPE_ID, Constants.FOUR);
        patchRequestFields.put(Constants.USERNAME, Constants.PATCHED_USERNAME);
    }

    /**
     * Sets up the valid resource with three properties.
     */
    private void setUpValidResourceWithThreeProperties() {
        setUpValidResourceAndEmptyPatchData();
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList = new ArrayList<>();
        propertiesHashMapArrayList.add(new LinkedHashMap<>());
        propertiesHashMapArrayList.add(new LinkedHashMap<>());
        propertiesHashMapArrayList.add(new LinkedHashMap<>());
        propertiesHashMapArrayList.get(0).put(Constants.KEY, Constants.USERNAME);
        propertiesHashMapArrayList.get(0).put(Constants.VALUE, Constants.OLD_USERNAME);
        propertiesHashMapArrayList.get(0).put(Constants.ENCRYPTED, false);
        propertiesHashMapArrayList.get(1).put(Constants.KEY, Constants.NAME);
        propertiesHashMapArrayList.get(1).put(Constants.VALUE, Constants.NAME);
        propertiesHashMapArrayList.get(1).put(Constants.ENCRYPTED, false);
        propertiesHashMapArrayList.get(2).put(Constants.KEY, Constants.PW);
        propertiesHashMapArrayList.get(2).put(Constants.VALUE, Constants.PW);
        propertiesHashMapArrayList.get(2).put(Constants.ENCRYPTED, false);
        resource.put(Constants.PROPERTIES, propertiesHashMapArrayList);
    }

    /**
     * Sets up the valid resource and patch data with two new properties.
     */
    private void setUpValidResourceAndPatchData_withTwoNewProperties() {
        resource.put(Constants.SUBSYSTEM_ID, Constants.ONE);
        resource.put(Constants.SUBSYSTEM_TYPE_ID, Constants.TWO);
        final ArrayList<LinkedHashMap<String, Object>> propertiesHashMapArrayList = new ArrayList<>();
        propertiesHashMapArrayList.add(new LinkedHashMap<>());
        propertiesHashMapArrayList.get(0).put(Constants.KEY, Constants.USERNAME);
        propertiesHashMapArrayList.get(0).put(Constants.VALUE, Constants.OLD_USERNAME);
        propertiesHashMapArrayList.get(0).put(Constants.ENCRYPTED, false);
        resource.put(Constants.PROPERTIES, propertiesHashMapArrayList);
        patchRequestFields.put(Constants.SUBSYSTEM_ID, Constants.THREE);
        patchRequestFields.put(Constants.SUBSYSTEM_TYPE_ID, Constants.FOUR);
        patchRequestFields.put(Constants.USERNAME, Constants.PATCHED_USERNAME);
        patchRequestFields.put(Constants.NEW_KEY, Constants.NEW_VALUE);
        patchRequestFields.put(Constants.TOKEN, Constants.TOKEN);
    }

    /**
     * Sets up the valid adapter link and patch data.
     */
    private void setUpValidAdapterLinkAndPatchData() {
        resource.put(Constants.SUBSYSTEM_ID, Constants.ONE);
        resource.put(Constants.SUBSYSTEM_TYPE_ID, Constants.TWO);
        patchRequestFields.put(Constants.SUBSYSTEM_ID, Constants.THREE);
        patchRequestFields.put(Constants.SUBSYSTEM_TYPE_ID, Constants.FOUR);
        patchRequestFields.put(Constants.USERNAME, Constants.PATCHED_USERNAME);
        patchRequestFields.put(Constants.ADAPTER_LINK_NAME, Constants.ADAPTER_LINK_NAME);
    }

    /**
     * Sets up the valid resource and empty patch data.
     */
    private void setUpValidResourceAndEmptyPatchData() {
        resource.put(Constants.SUBSYSTEM_ID, Constants.ONE);
        resource.put(Constants.SUBSYSTEM_TYPE_ID, Constants.TWO);
    }

    /**
     * Sets up the unmatched resource and patch data.
     */
    private void setUpUnmatchedResourceAndPatchData() {
        resource.put(Constants.SUBSYSTEM_ID, Constants.ONE);
        resource.put(Constants.SUBSYSTEM_TYPE_ID, Constants.TWO);
        patchRequestFields.put(Constants.ID, Constants.THREE);
        patchRequestFields.put(Constants.NAME, Constants.NAME);
    }
}
