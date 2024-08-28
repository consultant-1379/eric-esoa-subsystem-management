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

import static com.ericsson.bos.so.subsystemsmanager.test.legacy.business.util.ConnectionPropertiesValidatorTest.validConnectionProperty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemDao;
import com.ericsson.bos.so.subsystemsmanager.business.dao.api.SubsystemTypeDao;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.ConnectionPropertiesRepository;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemUserRepository;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Class SubsystemValidatorTest.
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class SubsystemValidatorTest {

    private static final SubsystemType nfvoSubsystemType;

    private static final SubsystemType domainManagerSubsystemType;

    private static final SubsystemType domainOrchestratorSubsystemType;

    @Mock
    private SubsystemDao subsystemDao;

    @Mock
    private SubsystemTypeDao subsystemTypeDao;

    @Mock
    private SubsystemUserRepository subsystemUserRepository;

    @Mock
    private ConnectionPropertiesValidator connectionPropertiesValidator;

    @Mock
    private ConnectionPropertiesRepository connectionPropertiesRepository;

    @InjectMocks
    private SubsystemValidator subsystemValidator;

    static {
        nfvoSubsystemType = SubsystemType.builder()
                .id(1L)
                .type(PredefinedSubsystemType.NFVO.toString())
                .build();
        domainManagerSubsystemType = SubsystemType.builder()
                .id(2L)
                .type(PredefinedSubsystemType.DOMAIN_MANAGER.toString())
                .build();
        domainOrchestratorSubsystemType =  SubsystemType.builder()
                .id(8L)
                .type(PredefinedSubsystemType.DOMAIN_ORCHESTRATOR.toString())
                .build();
    }

    /**
     * Valid subsystem.
     *
     * @param subsystemName the subsystem name
     * @return the subsystem
     */
    public static Subsystem validSubsystem(final String subsystemName) {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setId(47l);
        connectionProperties.setProperties(OldPropertyFactory.createDefaultTestProperties());
        connectionProperties.setSubsystemUsers(usersSet);
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(subsystemName);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemType(nfvoSubsystemType);
        subsystemOnboardingRequest.setSubsystemTypeId(nfvoSubsystemType.getId());
        subsystemOnboardingRequest.setId(99l);
        subsystemOnboardingRequest.setAdapterLink("test-adapter-link");
        subsystemOnboardingRequest.setVendor("Ericsson");
        return subsystemOnboardingRequest;
    }

    /**
     * Valid subsystem user.
     *
     * @return the subsystem user
     */
    private static SubsystemUser validSubsystemUser() {
        final SubsystemUser subsystemUser = new SubsystemUser();
        subsystemUser.setId(2l);
        return subsystemUser;
    }

    /**
     * Valid connection properties.
     *
     * @return the connection properties
     */
    private static ConnectionProperties validConnectionProperties(){
        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setId(1l);
        return connectionProperties;
    }

    /**
     * Test if subsystem id valid.
     */
    @Test
    public void testIsSubsystemIdValid() {
        final Subsystem returnValue = validSubsystem(Constants.$_validSubsystemName);
        when(subsystemDao.findSubsystemByIdWithException(Mockito.any(long.class))).thenReturn(returnValue);

        final Subsystem mySubsystem = subsystemValidator.isSubsystemIdValid(String.valueOf(100L));

        assertEquals(validSubsystem(Constants.$_validSubsystemName).getName(), mySubsystem.getName());
        assertEquals(validSubsystem(Constants.$_validSubsystemName).getHealthCheckTime(), mySubsystem.getHealthCheckTime());
        assertEquals(validSubsystem(Constants.$_validSubsystemName).getOperationalState(), mySubsystem.getOperationalState());
    }

    /**
     * Test if subsystem type id valid.
     */
    @Test
    public void testIsSubsystemTypeIdValid() {
        when(subsystemTypeDao.findById(Mockito.any(long.class))).thenReturn(nfvoSubsystemType);

        final SubsystemType mySubsystemType = subsystemValidator.isSubsystemTypeIdValid(100L);

        assertEquals(nfvoSubsystemType.getId(), mySubsystemType.getId());
        assertEquals(nfvoSubsystemType.getType(), mySubsystemType.getType());
    }

    /**
     * Test if subsystem user valid.
     */
    @Test
    public void testIsSubsystemUserValid() {
        final Optional<SubsystemUser> returnValue = Optional.of(validSubsystemUser());
        when(subsystemUserRepository.findById(Mockito.anyLong())).thenReturn(returnValue);

        final SubsystemUser mySubsystemUser = subsystemValidator.isSubsystemUserIdValid("" + 100L);

        assertEquals(validSubsystemUser().getId(), mySubsystemUser.getId());
    }

    /**
     * Checks if subsystem user is valid when all parameters are correct.
     */
    @Test
    public void isValidSubsystemUser_whenAllParametersAreOk() {
        final String subsystemId = validSubsystem(Constants.$_validSubsystemName).getId().toString();
        final String connPropsId = validConnectionProperty().getId().toString();
        final String subsystemUserId = validSubsystemUser().getId().toString();
        connectionPropertiesValidator.isValidSubsystemUser(connPropsId, subsystemId, subsystemUserId);
        Assertions.assertThatCode(() -> connectionPropertiesValidator.isValidSubsystemUser(connPropsId, subsystemId, subsystemUserId))
                .doesNotThrowAnyException();
    }

    /**
     * Checks if valid subsystem when subsystem exists.
     */
    @Test
    public void isValidSubsystem_whenSubsystemExists() {
        final Subsystem subsystem = validSubsystem(Constants.$_validSubsystemName);

        Assertions.assertThatCode(() -> subsystemValidator.isValidSubsystem(subsystem)).doesNotThrowAnyException();
    }

    /**
     * Checks if valid subsystem when subsystem is null.
     */
    @Test(expected = MalformedContentException.class)
    public void isValidSubsystem_whenSubsystemIsNull() {
        final Subsystem subsystem = null;
        subsystemValidator.isValidSubsystem(subsystem);
    }

    /**
     * Test to validate subsystem adapter link.
     */
    @Test
    public void testValidateSubsystemAdapterLink() {
        final String adapterLink = validSubsystem(Constants.$_validSubsystemName).getAdapterLink();
        final String vendor = validSubsystem(Constants.$_validSubsystemName).getVendor();

        Assertions.assertThatCode(() -> subsystemValidator.validateSubsystemAdapterLink(adapterLink, nfvoSubsystemType, vendor))
                .doesNotThrowAnyException();
        Assertions.assertThatCode(() -> subsystemValidator.validateSubsystemAdapterLink(adapterLink, domainOrchestratorSubsystemType, vendor))
                .doesNotThrowAnyException();
    }


    /**
     * Test to validate subsystem adapter link throw exception when adapter link is not given and is domain manager.
     */
    @Test(expected = MalformedContentException.class)
    public void testValidateSubsystemAdapterLink_throwExceptionWhenAdapterLinkIsNotGivenAndIsDomainManager() {
        final String vendor = validSubsystem(Constants.$_validSubsystemName).getVendor();
        subsystemValidator.validateSubsystemAdapterLink(null, domainManagerSubsystemType, vendor);
    }

    /**
     * Test to validate subsystem adapter link throw exception when adapter link is not given.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testValidateSubsystemAdapterLink_throwExceptionWhenAdapterLinkIsNotGiven() {
        final String vendor = validSubsystem(Constants.$_validSubsystemName).getVendor();
        subsystemValidator.validateSubsystemAdapterLink(null, nfvoSubsystemType, vendor);
    }

    /**
     * Test validate subsystem adapter link throw exception when adapter link is empty.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test(expected = MalformedContentException.class)
    public void testValidateSubsystemAdapterLink_throwExceptionWhenAdapterLinkIsEmpty() {
        final String adapterLink = "";
        final String vendor = validSubsystem(Constants.$_validSubsystemName).getVendor();

        subsystemValidator.validateSubsystemAdapterLink(adapterLink, nfvoSubsystemType, vendor);
    }

}
