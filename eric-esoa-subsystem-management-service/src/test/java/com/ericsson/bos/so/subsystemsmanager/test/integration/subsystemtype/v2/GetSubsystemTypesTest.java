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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;

/**
 * The Class GetSubsystemTypesTest.
 */
public class GetSubsystemTypesTest extends BaseIntegrationTest {

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem type present WHEN get subsystem types THEN all records returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemTypePresent_WHEN_getSubsystemTypes_THEN_allRecordsReturned() throws Exception {
        // given
        persistSubsystemType();
        final String subsystemType = Constants.NEW_SUBSYSTEM_TYPE;

        // when
        response = getSubsystemTypes();

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(new ArrayList<>(PredefinedSubsystemType.toSet()));
        checkResponseBodyContains(subsystemType);
    }

    /**
     * GIVEN subsystem type present WHEN get subsystem types by type THEN all records returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemTypePresent_WHEN_getSubsystemTypesByType_THEN_allRecordsReturned() throws Exception {
        // given
        persistSubsystemType();
        final String subsystemType = Constants.NEW_SUBSYSTEM_TYPE;
        // when
        response = getSubsystemTypeByType(subsystemType);

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(subsystemType);
    }

    /**
     * WHEN get subsystem types and empty type THEN all records returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_getSubsystemTypes_and_emptyType_THEN_allRecordsReturned() throws Exception {
        // when
        response = getSubsystemTypeByType("");

        // then
        response.andExpect(status().isOk());
        checkResponseBodyContains(new ArrayList<>(PredefinedSubsystemType.toSet()));
    }

    /**
     * WHEN get subsystem types and invalid type THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void WHEN_getSubsystemTypes_and_invalidType_THEN_conflictResponse() throws Exception {
        // when
        final String invalidType = "invalid";
        response = getSubsystemTypeByType(invalidType);

        // then
        response.andExpect(status().isNotFound());
    }

    private ResultActions getSubsystemTypes() throws Exception {
        return mockMvc.perform(get(pathProperties.getV2().getSubsystemTypes().getBasePath()));
    }

    private ResultActions getSubsystemTypeByType(String type) throws Exception {
        return mockMvc.perform(get(pathProperties.getV2().getSubsystemTypes().getBasePath() + "?type=" + type));
    }

    private SubsystemType persistSubsystemType() {
        final Subtype subtpe = new Subtype();
        subtpe.setName("new-subsystem-subtype");
        subtpe.setAlias("new-subsystem-subtype");
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        subtypes.add(subtpe);
        final SubsystemType subsystemType = subsystemTypeFactory.persistSubsystemType(Constants.NEW_SUBSYSTEM_TYPE, Constants.NEW_SUBSYSTEM_TYPE,
                subtypes);
        return subsystemType;
    }

}
