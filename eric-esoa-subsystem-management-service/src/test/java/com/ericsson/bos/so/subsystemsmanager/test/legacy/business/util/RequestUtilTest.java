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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * The Class RequestUtilTest.
 */
// TODO SM-47052 [Subsystem Management] Re-write ConnectionProperties serialization test (or get rid of it)
public class RequestUtilTest {

    @InjectMocks
    private SubsystemRequestValidator subsystemRequestValidator = new SubsystemRequestValidator();

    private Subsystem onboardRequest;

    private SubsystemList subsystemList;

    private SubsystemType subsystemType;

    private final List<ConnectionProperties> connectionPropList = new ArrayList<>();

    private final List<Subsystem> onboardRequestList = new ArrayList<>();

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setUpConnectionProperties(Constants.NAME, Constants.VENDOR_NAME, Constants.DOMAIN_MANAGER);
    }

    /**
     * Test name not empty.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testNameNotEmpty() {
        setUpOnbordRequest(Constants.TEST_NFVO, Constants.VENDOR_NAME, Constants.NFVO);
        onboardRequest.setName(" ");
        subsystemRequestValidator.validatePostRequest(onboardRequest);
    }

    /**
     * Test vendor not empty.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testVendorNotEmpty() {
        setUpOnbordRequest(Constants.TEST_NFVO, Constants.VENDOR_NAME, Constants.NFVO);
        onboardRequest.setVendor(" ");
        subsystemRequestValidator.validatePostRequest(onboardRequest);
    }

    /**
     * Test url not empty.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testUrlNotEmpty() {
        setUpOnbordRequest(Constants.TEST_NFVO, Constants.VENDOR_NAME, Constants.NFVO);
        onboardRequest.setUrl(" ");
        subsystemRequestValidator.validatePostRequest(onboardRequest);
    }

    /**
     * Test id not null check.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testIdNotNullCheck() {
        final Long id = null;
        SubsystemRequestValidator.idNotNullCheck(id);
    }

    /**
     * Test id not null check string null.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testIdNotNullCheckStringNull() {
        final String id = null;
        SubsystemRequestValidator.idNotNullCheck(id);
    }

    /**
     * Test id not null check string empty.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testIdNotNullCheckStringEmpty() {
        final String id = "";
        SubsystemRequestValidator.idNotNullCheck(id);
    }

    /**
     * Test map not null check null.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testMapNotNullCheckNull() {
        final Map<String, Object> responseMap = null;
        SubsystemRequestValidator.mapNotNullCheck(responseMap);
    }

    /**
     * Test map not null check empty.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testMapNotNullCheckEmpty() {
        final Map<String, Object> responseMap = new HashMap<String, Object>();
        SubsystemRequestValidator.mapNotNullCheck(responseMap);
    }

    /**
     * Test is valid patch request all null.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testIsValidPatchRequestAllNull() {
        final Map<String, Object> responseMap = null;
        final String id = null;
        subsystemRequestValidator.isValidPatchRequest(id, responseMap);
    }

    /**
     * Test validate onboarding request is null.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testValidateOnboardingRequestIsNull() {
        final Subsystem subsystem = new Subsystem();
        subsystemRequestValidator.validatePostRequest(subsystem);
    }

    /**
     * Test is no connection property object in onboard request.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testIsNoConnectionPropertyObjectInOnboardRequest() {
        setUpOnbordRequest(Constants.TEST_NFVO, Constants.VENDOR_NAME, Constants.NFVO);
        onboardRequest.setConnectionProperties(null);
        subsystemRequestValidator.validatePostRequest(onboardRequest);
    }

    /**
     * Test is valid patch request not null.
     *
     * @throws MalformedContentException
     *             the malformed content exception
     */
    @Test
    public void testIsValidPatchRequestNotNull() {
        final String subsystemId = "1";
        final Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("Key", subsystemId);
        subsystemRequestValidator.isValidPatchRequest(subsystemId, responseMap);

        Assertions.assertThatCode(() -> subsystemRequestValidator.isValidPatchRequest(subsystemId, responseMap))
                .doesNotThrowAnyException();
    }

    /**
     * Sets up the onboard request.
     *
     * @param subsystemName
     *            the subsystem name
     * @param vendor
     *            the vendor
     * @param subsystemTypeValue
     *            the subsystem type value
     */
    private void setUpOnbordRequest(String subsystemName, String vendor, String subsystemTypeValue) {
        setUpSubsystemType(subsystemTypeValue);
        onboardRequest = new Subsystem();
        onboardRequest.setId(Long.valueOf(1));
        onboardRequest.setName(subsystemName);
        onboardRequest.setOperationalState(OperationalState.REACHABLE);
        onboardRequest.setUrl("url");
        onboardRequest.setVendor(vendor);
        onboardRequest.setHealthCheckTime("health-check");
        onboardRequest.setSubsystemTypeId(Long.valueOf(1));
        onboardRequest.setSubsystemType(subsystemType);
        onboardRequest.setAdapterLink("test-adapter-link");
        onboardRequestList.add(onboardRequest);
    }

    /**
     * Sets up subsystem list.
     */
    private void setUpSubsystemList() {
        subsystemList = new SubsystemList();
        subsystemList.setItems(onboardRequestList);
    }

    /**
     * Sets up subsystem type.
     *
     * @param subsystemTypeValue
     *            THENew up subsystem type
     */
    private void setUpSubsystemType(String subsystemTypeValue) {
        setUpSubsystemList();
        subsystemType = new SubsystemType();

        if (subsystemTypeValue.equalsIgnoreCase(Constants.DOMAIN_MANAGER)) {
            subsystemType.setId(Long.valueOf(1));
        } else if (subsystemTypeValue.equalsIgnoreCase(Constants.NFVO)) {
            subsystemType.setId(Long.valueOf(2));
        }

        subsystemType.setType(subsystemTypeValue);
    }

    /**
     * Sets up connection properties.
     *
     * @param subsystemName
     *            the subsystem name
     * @param vendor
     *            the vendor
     * @param subsystemTypeValue
     *            the subsystem type value
     */
    private void setUpConnectionProperties(String subsystemName, String vendor, String subsystemTypeValue) {
        setUpOnbordRequest(subsystemName, vendor, subsystemTypeValue);
        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setId(Long.valueOf(1));
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystem(onboardRequest);
        connectionProperties.setSubsystemUsers(null);
        connectionProperties.setSubsystemId(Long.valueOf(1));
        connectionPropList.add(connectionProperties);
        onboardRequest.setConnectionProperties(connectionPropList);
    }
}
