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
package com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemTypeValidator;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.ConnectionPropertiesFactory;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.ExpectedResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_NAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_PASSWORD;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.DEFAULT_USERNAME;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.NAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.PW_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory.USERNAME_PROPERTY;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.DEFAULT_ADAPTER_LINK;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.PRIMARY_SUBSYSTEM;
import static com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.SubsystemFactory.SECONDARY_SUBSYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class SubsystemTypeValidationIntegrationTest.
 */
public class SubsystemTypeValidationIntegrationTest extends BaseIntegrationTest {

    public static final String ADAPTER_LINK_1 = "adapter-link-1";
    public static final String ADAPTER_LINK_2 = "adapter-link-2";
    public static final String ADAPTER_LINK_NOT_EXIST =
        "The adapter link 'invaliAdapterLink' does not exist/is not functional. "
        + "Enter a valid adapter link to proceed.";
    public static final String INVALID_ADAPTER_LINK = "invaliAdapterLink";

    @Autowired
    private SubsystemRepository subsystemRepository;

    @SpyBean
    private AdaptersLinksService adapterLinksService;

    @MockBean
    private KmsServiceImpl kmsService;

    private static List<ConnectionProperties> buildConnectionPropertiesWithNoTenant() {
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD,
                NAME_PROPERTY, DEFAULT_NAME));

        return Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));
    }

    /**
     * WHEN get all subsystem types THEN predefined subsystem types present.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_getAllSubsystemTypes_THEN_predefinedSubsystemTypesPresent() throws Exception {
        // when
        response = mockMvc.perform(get(ExpectedResponse.getServiceUrl() + "/subsystem-types")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        final String responseJsonString = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final List<SubsystemType> subsystemTypes = new ObjectMapper().readValue(responseJsonString, new TypeReference<List<SubsystemType>>() {

        });

        final Set<String> subsystemTypeNames = subsystemTypes.stream()
                .map(SubsystemType::getType)
                .collect(Collectors.toSet());
        assertThat(subsystemTypeNames).containsAll(PredefinedSubsystemType.toSet());
    }

    // -- NFVO Subsystem tests -- //

    /**
     * WHEN create nfvo subsystem with no tenant THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createNfvoSubsystemWithNoTenant_THEN_subsystemPersisted() throws Exception {
        //given
        final List<String> adapterLinks = Arrays.asList(ADAPTER_LINK_1, DEFAULT_ADAPTER_LINK, ADAPTER_LINK_2);
        doReturn(adapterLinks).when(adapterLinksService).fetchAdapterLinksByType(PredefinedSubsystemType.NFVO.getType());

        // when
        final List<ConnectionProperties> connectionProperties = buildConnectionPropertiesWithNoTenant();
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType(), connectionProperties);

        response = postRequest(subsystem);

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(PRIMARY_SUBSYSTEM))
                .andExpect(jsonPath("$.subsystemType.type").value(PredefinedSubsystemType.NFVO.getType()));

        assertThat(subsystemRepository.existsByName(PRIMARY_SUBSYSTEM)).isTrue();
    }

    /**
     * WHEN create nfvo subsystem with ericsson vendor and no tenant THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createNfvoSubsystemWithEricssonVendorAndNoTenant_THEN_badRequestResponse() throws Exception {
        // when
        final List<ConnectionProperties> connectionProperties = buildConnectionPropertiesWithNoTenant();
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType(), connectionProperties);
        subsystem.setVendor(SubsystemTypeValidator.ERICSSON_VENDOR);

        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-A-27");
    }

    /**
     * WHEN create nfvo subsystem with no name property THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createNfvoSubsystemWithNoNameProperty_THEN_badRequestResponse() throws Exception {
        // when
        final List<Property> properties = PropertyFactory.buildProperties(ImmutableMap.of(USERNAME_PROPERTY, DEFAULT_USERNAME,
                PW_PROPERTY, DEFAULT_PASSWORD));
        final List<ConnectionProperties> connectionProperties =
                Collections.singletonList(ConnectionPropertiesFactory.buildConnectionProperties(properties));

        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType(), connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-A-27");
    }

    /**
     * GIVEN existing nfvo subsystem WHEN create another nfvo subsystem THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingNfvoSubsystem_WHEN_createAnotherNfvoSubsystem_THEN_conflictResponse() throws Exception {
        // given
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType());

        final List<String> adapterLinks = Arrays.asList(ADAPTER_LINK_1, DEFAULT_ADAPTER_LINK, ADAPTER_LINK_2);
        doReturn(adapterLinks).when(adapterLinksService).fetchAdapterLinksByType(PredefinedSubsystemType.NFVO.getType());

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(SECONDARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType());
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-H-14");
        checkResponseContainsErrorData(PredefinedSubsystemType.NFVO.getType(), "1", PredefinedSubsystemType.NFVO.getType());

        assertThat(subsystemRepository.existsByName(SECONDARY_SUBSYSTEM)).isFalse();
    }

    /**
     * WHEN create nfvo subsystem with no adapter link specified THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createNfvoSubsystemWithNoAdapterLinkSpecified_THEN_badRequestResponse() throws Exception {
        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType());
        subsystem.setAdapterLink("");
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-B-28");
        checkResponseContainsErrorData("subsystem-1");
    }

    /**
     * WHEN create nfvo subsystem with invalid adapter link THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createNfvoSubsystemWithInvalidAdapterLink_THEN_badRequestResponse() throws Exception {
        //given
        final List<String> adapterLinks = Arrays.asList(ADAPTER_LINK_1, DEFAULT_ADAPTER_LINK, ADAPTER_LINK_2);
        doReturn(adapterLinks).when(adapterLinksService).fetchAdapterLinksByType(PredefinedSubsystemType.NFVO.getType());

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.NFVO.getType());
        subsystem.setAdapterLink(INVALID_ADAPTER_LINK);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-J-42");
        checkResponseContainsErrorData(INVALID_ADAPTER_LINK);
        checkResponseBodyContains(ADAPTER_LINK_NOT_EXIST);
    }

    // -- Domain Manager Subsystem tests -- //

    /**
     * GIVEN existing domain manager subsystem WHEN create another domain manager subsystem THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingDomainManagerSubsystem_WHEN_createAnotherDomainManagerSubsystem_THEN_subsystemPersisted() throws Exception {
        // given
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(SECONDARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).value(SECONDARY_SUBSYSTEM))
                .andExpect(jsonPath(Constants.SUBSYSTEM_TYPE).value(PredefinedSubsystemType.DOMAIN_MANAGER.getType()));

        assertThat(subsystemRepository.existsByName(SECONDARY_SUBSYSTEM)).isTrue();
    }

    /**
     * WHEN create domain manager subsystem with adapter link and vendor ericsson THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    //#1: {Domain Manager, Vendor=Ericsson, adapterLink != null && !adapterLink.trim().isEmpty()} => OK
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createDomainManagerSubsystemWithAdapterLinkAndVendorEricsson_THEN_subsystemPersisted() throws Exception {
        // given

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        subsystem.setAdapterLink(DEFAULT_ADAPTER_LINK);
        subsystem.setVendor(SubsystemTypeValidator.ERICSSON_VENDOR);
        response = postRequest(subsystem);

        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThat(subsystemRepository.existsByName(subsystem.getName())).isTrue();
    }

    /**
     * WHEN create domain manager subsystem without adapter link and vendor ericsson THEN bad request response.
     *
     * @throws Exception the exception
     */
    //#2: { Domain Manager, Vendor=Ericsson, (adapterLink == null || adapterLink.trim().isEmpty()} => Error, adapterLink is mandatory for…
    @Test
    public void WHEN_createDomainManagerSubsystemWithoutAdapterLinkAndVendorEricsson_THEN_badRequestResponse() throws Exception {
        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        subsystem.setAdapterLink(null);
        subsystem.setVendor(SubsystemTypeValidator.ERICSSON_VENDOR);
        response = postRequest(subsystem);

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-B-28");

        assertThat(subsystemRepository.existsByName(subsystem.getName())).isFalse();
    }

    /**
     * WHEN create domain manager subsystem with adapter link and vendor not ericsson THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    //#3: { Domain Manager, Vendor != Ericsson, (adapterLink |= null || !adapterLink.trim().isEmpty()} => OK
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createDomainManagerSubsystemWithAdapterLinkAndVendorNotEricsson_THEN_subsystemPersisted() throws Exception {
        // given

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        subsystem.setAdapterLink(DEFAULT_ADAPTER_LINK);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThat(subsystemRepository.existsByName(subsystem.getName())).isTrue();
    }

    /**
     * WHEN create domain manager subsystem without adapter link and vendor not ericsson THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    //#4: { Domain Manager, Vendor !=Ericsson, (adapterLink == null || adapterLink.trim().isEmpty()} => OK
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createDomainManagerSubsystemWithoutAdapterLinkAndVendorNotEricsson_THEN_subsystemPersisted() throws Exception {
        // given

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType());
        subsystem.setAdapterLink(null);
        response = postRequest(subsystem);

        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThat(subsystemRepository.existsByName(subsystem.getName())).isTrue();
    }

    /**
     * WHEN create domain manager subsystem with two connection properties specified THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_createDomainManagerSubsystemWithTwoConnectionPropertiesSpecified_THEN_badRequestResponse() throws Exception {
        // when
        final List<ConnectionProperties> connectionProperties = ConnectionPropertiesFactory.buildConnectionProperties(2);
        final Subsystem subsystem =
                subsystemFactory.buildSubsystem(SECONDARY_SUBSYSTEM, PredefinedSubsystemType.DOMAIN_MANAGER.getType(), connectionProperties);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-B-37");
        checkResponseContainsErrorData("subsystem-2");
    }

    // -- Physical Device Subsystem tests -- //

    /**
     * WHEN create physical device subsystem with no vendor THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createPhysicalDeviceSubsystemWithNoVendor_THEN_subsystemPersisted() throws Exception {
        // given

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.PHYSICAL_DEVICE.getType());
        subsystem.setVendor(null);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).value(PRIMARY_SUBSYSTEM))
                .andExpect(jsonPath(Constants.SUBSYSTEM_TYPE).value(PredefinedSubsystemType.PHYSICAL_DEVICE.getType()));

        assertThat(subsystemRepository.existsByName(PRIMARY_SUBSYSTEM)).isTrue();
    }

    // -- CM Gateway Subsystem tests -- //

    /**
     * WHEN create cm gateway subsystem with no vendor THEN subsystem persisted.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void WHEN_createCmGatewaySubsystemWithNoVendor_THEN_subsystemPersisted() throws Exception {
        // given

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.CM_GATEWAY.getType());
        subsystem.setVendor(null);
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).value(PRIMARY_SUBSYSTEM))
                .andExpect(jsonPath(Constants.SUBSYSTEM_TYPE).value(PredefinedSubsystemType.CM_GATEWAY.getType()));

        assertThat(subsystemRepository.existsByName(PRIMARY_SUBSYSTEM)).isTrue();
    }

    // -- InventorySystem Subsystem tests -- //

    /**
     * Test inventory system subsystem unique.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInventorySystemSubsystemUnique() throws Exception {
        // given
        subsystemFactory.persistSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.INVENTORY_SYSTEM.getType());

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(SECONDARY_SUBSYSTEM, PredefinedSubsystemType.INVENTORY_SYSTEM.getType());
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        checkResponseContainsInternalErrorCode("SSM-H-14");
        checkResponseContainsErrorData(PredefinedSubsystemType.INVENTORY_SYSTEM.getType(), "1", PredefinedSubsystemType.INVENTORY_SYSTEM.getType());

        assertThat(subsystemRepository.existsByName(SECONDARY_SUBSYSTEM)).isFalse();
    }

    /**
     * Test create inventory system subsystem.
     *
     * @throws Exception the exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateInventorySystemSubsystem() throws Exception {
        // given

        // when
        final Subsystem subsystem = subsystemFactory.buildSubsystem(PRIMARY_SUBSYSTEM, PredefinedSubsystemType.INVENTORY_SYSTEM.getType());
        response = postRequest(subsystem);

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath(Constants.$_SUBSYSTEM_NAME).value(PRIMARY_SUBSYSTEM))
                .andExpect(jsonPath(Constants.SUBSYSTEM_TYPE).value(PredefinedSubsystemType.INVENTORY_SYSTEM.getType()));

        assertThat(subsystemRepository.existsByName(PRIMARY_SUBSYSTEM)).isTrue();
    }

    private ResultActions postRequest(final Subsystem subsystem) throws Exception {
        return mockMvc.perform(post(ExpectedResponse.getServiceUrl() + "/subsystems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(subsystem)));
    }
}
