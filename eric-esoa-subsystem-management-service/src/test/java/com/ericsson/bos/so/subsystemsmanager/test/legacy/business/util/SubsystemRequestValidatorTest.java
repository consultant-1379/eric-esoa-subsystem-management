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
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.business.util.SubsystemValidatorTest.validSubsystem;
import static org.mockito.ArgumentMatchers.any;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.business.exception.MalformedContentException;
import com.ericsson.bos.so.subsystemsmanager.business.validation.ConnectionPropertiesValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemRequestValidator;
import com.ericsson.bos.so.subsystemsmanager.business.validation.SubsystemValidator;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Class SubsystemRequestValidatorTest.
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class SubsystemRequestValidatorTest {

    @Mock
    private SubsystemValidator subsystemValidator;

    @Mock
    private ConnectionPropertiesValidator connectionPropertiesValidator;

    @InjectMocks
    private SubsystemRequestValidator subsystemRequestValidator;

    /**
     * Checks valid connection properties for post request when all parameters are ok.
     */
    @Test
    public void isValidConnPropsPostRequest_whenAllParametersAreOk(){
        final ConnectionProperties connProps = validConnectionProperty();
        final Subsystem subsystem= validSubsystem(Constants.$_validSubsystemName);
        final String subsystemId = subsystem.getId().toString();
        Mockito.when(subsystemValidator.isSubsystemIdValid(any())).thenReturn(subsystem);

        subsystemRequestValidator.isValidConnPropsPostRequest(connProps, subsystemId);

        Assertions.assertThatCode(() -> SubsystemRequestValidator.idNotNullCheck(connProps.getSubsystemId()))
                .doesNotThrowAnyException();
        Assertions.assertThatCode(() -> SubsystemRequestValidator.idNotNullCheck(subsystemId)).doesNotThrowAnyException();
        Assertions.assertThatCode(() -> subsystemValidator.isSubsystemIdValid(subsystemId))
                .doesNotThrowAnyException();
    }

    /**
     * Checks valid connection properties for post request when connection propserties are null.
     */
    @Test(expected = MalformedContentException.class)
    public void isValidConnPropsPostRequest_whenConnPropsAreNull() {
        final ConnectionProperties connProps = null;
        final String subsystemId = validSubsystem(Constants.$_validSubsystemName).getId().toString();
        subsystemRequestValidator.isValidConnPropsPostRequest(connProps, subsystemId);
    }

    /**
     * Checks if valid update request.
     *
     * @throws MalformedContentException the malformed content exception
     */
    @Test
    public void isValidUpdateRequest() {
        final String subsystemId = validSubsystem(Constants.$_validSubsystemName).getId().toString();
        final Subsystem subsystem = validSubsystem(Constants.$_validSubsystemName);

        final Map<String, Object> patchRequestFields = new HashMap<String, Object>();
        patchRequestFields.put("key", Integer.valueOf(22));

        subsystemRequestValidator.isValidUpdateRequest(subsystemId, patchRequestFields, subsystem);

        Assertions.assertThatCode(() -> subsystemRequestValidator.isValidPatchRequest(subsystemId, patchRequestFields))
                .doesNotThrowAnyException();
        Assertions.assertThatCode(() -> subsystemValidator.isValidSubsystem(subsystem)).doesNotThrowAnyException();
    }

}
