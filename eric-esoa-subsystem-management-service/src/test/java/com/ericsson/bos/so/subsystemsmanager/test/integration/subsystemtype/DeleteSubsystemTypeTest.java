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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;

import org.junit.Test;


/**
 * The Class DeleteSubsystemTypeTest.
 */
public class DeleteSubsystemTypeTest extends BaseIntegrationTest {

    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN custom subsystem type present WHEN delete subsystem type THEN no content response and record deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_customSubsystemTypePresent_WHEN_deleteSubsystemType_THEN_noContentResponse_and_recordDeleted() throws Exception {
        // given
        final SubsystemType subsystemType = subsystemTypeFactory.persistSubsystemType("bb-8");

        // when
        response = deleteSubsystemType(subsystemType.getId());

        // then
        response.andExpect(status().isNoContent());

        assertThat(subsystemTypeRepository.findById(subsystemType.getId())).isNotPresent();
    }

    /**
     * GIVEN subsystem type not present WHEN delete subsystem type THEN not found response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemTypeNotPresent_WHEN_deleteSubsystemType_THEN_notFoundResponse() throws Exception {
        // given
        final long nonexistentId = 936L;

        // when
        response = deleteSubsystemType(nonexistentId);

        // then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-J-17");
    }

    /**
     * WHEN delete predefined subsystem type THEN forbidden response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_deletePredefinedSubsystemType_THEN_forbiddenResponse() throws Exception {
        // given
        final long predefinedSubsystemTypeId = 1;

        final SubsystemType subsystemType = subsystemTypeRepository.findById(predefinedSubsystemTypeId)
                .orElseThrow(() -> new SubsystemTypeDoesNotExistException(predefinedSubsystemTypeId));

        // when
        response = deleteSubsystemType(predefinedSubsystemTypeId);

        // then
        response.andExpect(status().isForbidden());
        checkResponseContainsInternalErrorCode("SSM-L-10");
    }

    private ResultActions deleteSubsystemType(long id) throws Exception {
        final String deleteSubsystemTypeUri = pathProperties.getSubsystemTypes().getBasePath() + "/" + id;
        return mockMvc.perform(MockMvcRequestBuilders.delete(deleteSubsystemTypeUri));
    }

}
