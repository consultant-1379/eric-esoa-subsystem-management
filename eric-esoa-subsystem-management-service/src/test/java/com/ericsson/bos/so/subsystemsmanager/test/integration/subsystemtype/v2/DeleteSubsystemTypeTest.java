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
package com.ericsson.bos.so.subsystemsmanager.test.integration.subsystemtype.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeDoesNotExistException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class DeleteSubsystemTypeTest.
 */
@Slf4j
public class DeleteSubsystemTypeTest extends BaseIntegrationTest {

    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem type present WHEN delete subsystem type THEN no content response and record deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_SubsystemTypePresent_WHEN_deleteSubsystemType_THEN_noContentResponse_and_recordDeleted() throws Exception {
        // given
        final SubsystemType subsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, "Idea", "Idea");

        // when
        response = deleteSubsystemType(subsystemType.getId());
        log.info("response:{}", response);
        // then
        response.andExpect(status().isNoContent());

        assertThat(subsystemTypeRepository.findById(subsystemType.getId())).isNotPresent();
    }

    /**
     * GIVEN subsystem subtype present WHEN delete subsystem subtype THEN no content response and record deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_SubsystemSubtypePresent_WHEN_deleteSubsystemSubtype_THEN_noContentResponse_and_recordDeleted() throws Exception {
        final SubsystemType subsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        // when
        response = deleteSubsystemSubtype(subsystemType.getId(), subsystemType.getSubtypes().get(0).getId());
        // then
        response.andExpect(status().isNoContent());
        assertThat(subsystemTypeRepository.findById(subsystemType.getId()).get().getSubtypes()).isEmpty();
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
     * GIVEN subsystem subtype empty list WHEN delete subsystem subtype THEN not found response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemSubtypeEmptyList_WHEN_deleteSubsystemSubtype_THEN_notFoundResponse() throws Exception {
        final SubsystemType subsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR);
        log.info("subsystemType:{}",subsystemType);
        final long subsystemSubtypeId = 936L;
        // when
        response = deleteSubsystemSubtype(subsystemType.getId(), subsystemSubtypeId);
        // then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-J-47");
    }

    /**
     * GIVEN subsystem subtype not present WHEN delete subsystem subtype THEN not found response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemSubtypeNotPresent_WHEN_deleteSubsystemSubtype_THEN_notFoundResponse() throws Exception {
        final SubsystemType subsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long nonexistentId = 936L;
        // when
        response = deleteSubsystemSubtype(subsystemType.getId(), nonexistentId);
        // then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-J-47");
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

        subsystemTypeRepository.findById(predefinedSubsystemTypeId)
                .orElseThrow(() -> new SubsystemTypeDoesNotExistException(predefinedSubsystemTypeId));

        // when
        response = deleteSubsystemType(predefinedSubsystemTypeId);

        // then
        response.andExpect(status().isForbidden());
        checkResponseContainsInternalErrorCode("SSM-L-10");
    }

    private ResultActions deleteSubsystemType(long id) throws Exception {
        final String deleteSubsystemTypeUri = pathProperties.getV2().getSubsystemTypes().getBasePath() +
                Constants.$_SLASH + id;
        return mockMvc.perform(MockMvcRequestBuilders.delete(deleteSubsystemTypeUri));
    }

    private ResultActions deleteSubsystemSubtype(long id, long subtypeId) throws Exception {
        final String deleteSubsystemSubtypeUri = pathProperties.getV2().getSubsystemTypes().getBasePath() +
                Constants.$_SLASH + id + Constants.$_SLASH + Constants.SUBTYPE + Constants.$_SLASH + subtypeId;
        return mockMvc.perform(MockMvcRequestBuilders.delete(deleteSubsystemSubtypeUri));
    }

    private SubsystemType persistSubsystemType(String subsystemType, String subsystemTypeAlias,
            String subtypeName, String subtypeNameAlias) {
        final Subtype subtpe = new Subtype();
        subtpe.setName(subtypeName);
        subtpe.setAlias(subtypeNameAlias);
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        subtypes.add(subtpe);
        final SubsystemType persistedSubsystemType = subsystemTypeFactory.persistSubsystemType(subsystemType, subsystemTypeAlias, subtypes);
        return persistedSubsystemType;
    }

    private SubsystemType persistSubsystemType(String subsystemType, String subsystemTypeAlias) {
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        final SubsystemType persistedSubsystemType = subsystemTypeFactory.persistSubsystemType(subsystemType, subsystemTypeAlias, subtypes);
        return persistedSubsystemType;
    }

}
