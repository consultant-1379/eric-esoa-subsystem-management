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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemTypeCategory;

import org.junit.Test;


/**
 * The Class GetSubsystemTypesTest.
 */
public class GetSubsystemTypesTest extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN no category and custom subsystem type present WHEN get subsystem types THEN all records returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_noCategory_and_customSubsystemTypePresent_WHEN_getSubsystemTypes_THEN_allRecordsReturned() throws Exception {
        // given
        final String newSubsystemType = Constants.NEW_SUBSYSTEM_TYPE;
        subsystemTypeFactory.persistSubsystemType(newSubsystemType);

        // when
        response = getSubsystemTypes();

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(new ArrayList<>(PredefinedSubsystemType.toSet()));
        checkResponseBodyContains(newSubsystemType);
    }

    /**
     * WHEN get subsystem types and empty category THEN all records returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_getSubsystemTypes_and_emptyCategory_THEN_allRecordsReturned() throws Exception {
        // when
        response = getSubsystemTypes("");

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(new ArrayList<>(PredefinedSubsystemType.toSet()));
    }

    /**
     * WHEN get subsystem types and primary category THEN primary subsystem types returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_getSubsystemTypes_and_primaryCategory_THEN_primarySubsystemTypesReturned() throws Exception {
        // when
        response = getSubsystemTypes(SubsystemTypeCategory.PRIMARY.getCategory());

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(new ArrayList<>(PredefinedSubsystemType.getPrimaryTypes()));
    }

    /**
     * GIVEN custom subsystem type present WHEN get subsystem types and custom category THEN custom subsystem types returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_customSubsystemTypePresent_WHEN_getSubsystemTypes_and_customCategory_THEN_customSubsystemTypesReturned() throws Exception {
        // given
        final String newSubsystemType = Constants.NEW_SUBSYSTEM_TYPE;
        subsystemTypeFactory.persistSubsystemType(newSubsystemType);

        // when
        response = getSubsystemTypes(SubsystemTypeCategory.CUSTOM.getCategory().toLowerCase());

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(new ArrayList<>(PredefinedSubsystemType.getNonPrimaryTypes()));
        checkResponseBodyContains(newSubsystemType);
    }

    /**
     * WHEN get subsystem types and invalid category THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_getSubsystemTypes_and_invalidCategory_THEN_conflictResponse() throws Exception {
        // when
        final String invalidCategory = "invalid";
        response = getSubsystemTypes(invalidCategory);

        // then
        response.andExpect(status().isBadRequest());
    }

    private ResultActions getSubsystemTypes() throws Exception {
        return mockMvc.perform(get(pathProperties.getSubsystemTypes().getBasePath()));
    }

    private ResultActions getSubsystemTypes(String category) throws Exception {
        return mockMvc.perform(get(pathProperties.getSubsystemTypes().getBasePath() + "?category=" + category));
    }

}
