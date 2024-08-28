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
package com.ericsson.bos.so.subsystemsmanager.test.integration.subsystemtype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeAlreadyExistsException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.SubsystemTypeRequest;

import org.junit.Test;


/**
 * The Class PostSubsystemTypeTest.
 */
public class PostSubsystemTypeTest extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /** The subsystem type repository. */
    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    /**
     * GIVEN unique name WHEN create subsystem type THEN created response and record persisted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_uniqueName_WHEN_createSubsystemType_THEN_createdResponse_and_recordPersisted() throws Exception {
        // given
        final String newSubsystemType = "new-subsystem-type";
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest(newSubsystemType);

        // when
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isCreated());

        assertThat(subsystemTypeRepository.findByType(newSubsystemType)).isPresent();
    }

    /**
     * GIVEN predefined name WHEN create subsystem type THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_predefinedName_WHEN_createSubsystemType_THEN_conflictResponse() throws Exception {
        // given
        final String newSubsystemName = PredefinedSubsystemType.DOMAIN_MANAGER.getType();

        // when
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest(newSubsystemName);
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isConflict());
        checkResponseContainsInternalErrorCode(SubsystemTypeAlreadyExistsException.INTERNAL_ERROR_CODE);
    }

    /**
     * GIVEN existing subsystem type WHEN create same subsystem type THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemType_WHEN_createSameSubsystemType_THEN_conflictResponse() throws Exception {
        // given
        final String newSubsystemType = "new-subsystem-type";
        subsystemTypeFactory.persistSubsystemType(newSubsystemType);

        // when
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest(newSubsystemType);
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isConflict());
        checkResponseContainsInternalErrorCode(SubsystemTypeAlreadyExistsException.INTERNAL_ERROR_CODE);
    }

    /**
     * GIVEN no name WHEN create subsystem type THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_noName_WHEN_createSubsystemType_THEN_badRequestResponse() throws Exception {
        // when
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest(null);
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isBadRequest());
        //checkResponseContainsInternalErrorCode(MalformedContentException.INTERNAL_ERROR_CODE);

    }

    private ResultActions postSubsystemType(final SubsystemTypeRequest subsystemTypeRequest) throws Exception {
        final String postSubsystemTypeUrl = pathProperties.getSubsystemTypes().getBasePath();
        return mockMvc.perform(post(postSubsystemTypeUrl)
                .content(subsystemTypeRequest.toJsonString())
                .contentType(MediaType.APPLICATION_JSON));
    }

}
