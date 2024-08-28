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
package com.ericsson.bos.so.subsystemsmanager.test.integration.subsystem.v2;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService;
import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static com.ericsson.bos.so.subsystemsmanager.web.controller.v2.AdapterLinksControllerV2.ADAPTERLINK_PATH;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class AdapterLinkControllerV2Test.
 */
public class AdapterLinkControllerV2Test extends BaseIntegrationTest {

    @SpyBean
    private AdaptersLinksService adapterLinksService;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN subsystem type NFVO WHEN get request on adapter links THEN NFVO adapter links returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_subsystemTypeNFVO_WHEN_getRequestOnAdapterLinks_THEN_NFVOAdapterlinksReturned() throws Exception {
        // given
        final List<String> nfvoAdapterLinks = Arrays.asList("nfvo-adapter-link-1", "nfvo-adapter-link-2");
        doReturn(nfvoAdapterLinks).when(adapterLinksService).fetchAdapterLinksByType(PredefinedSubsystemType.NFVO.getType());

        //when
        response = performMvcGETRequest(PredefinedSubsystemType.NFVO.getType());

        //then
        response.andExpect(jsonPath("$.[0]").value(nfvoAdapterLinks.get(0)))
                .andExpect(jsonPath("$.[1]").value(nfvoAdapterLinks.get(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk());
    }

    /**
     * GIVEN no subsystem type param provided WHEN get request on adapter links THEN default NFVO adapter links returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_noSubsystemTypeParamProvided_WHEN_getRequestOnAdapterLinks_THEN_defaultNFVOAdapterlinksReturned() throws Exception {
        //given
        final List<String> nfvoAdapterLinks = Arrays.asList("nfvo-adapter-link-1", "nfvo-adapter-link-2");
        doReturn(nfvoAdapterLinks).when(adapterLinksService).fetchAdapterLinksByType(PredefinedSubsystemType.NFVO.getType());

        //when
        final String getSubsystemsUri = pathProperties.getV2().getSubsystemManagement().getBasePath() + ADAPTERLINK_PATH;
        response = mockMvc.perform(MockMvcRequestBuilders.get(getSubsystemsUri).contentType(MediaType.APPLICATION_JSON));

        //then
        response.andExpect(jsonPath("$.[0]").value(nfvoAdapterLinks.get(0)))
                .andExpect(jsonPath("$.[1]").value(nfvoAdapterLinks.get(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk());
    }

    /**
     * GIVEN invalid subsystem type WHEN get request on adapter links THEN subsystem type does not exist.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_invalidSubsystemType_WHEN_getRequestOnAdapterLinks_THEN_subsystemTypeDoesNotExist() throws Exception {
        //given
        final String invalidType = "invalid_subsystem_type";

        //when
        response = performMvcGETRequest(invalidType);

        //then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-J-17");
        checkResponseContainsErrorData(invalidType);
    }

    /**
     * GIVEN wrong access account WHEN get request on adapter links THEN K8's api exception.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_wrongAccessAccount_WHEN_getRequestOnAdapterLinks_THEN_K8sApiException() throws Exception {
        //given
        final String invalidType = PredefinedSubsystemType.NFVO.getType();
        //when
        response = performMvcGETRequest(invalidType);

        //then
        response.andExpect(status().isInternalServerError());
        checkResponseContainsInternalErrorCode("SSM-F-05");
    }

    private ResultActions performMvcGETRequest(final String subsystemType) throws Exception {
        final String getSubsystemsUri = pathProperties.getV2().getSubsystemManagement().getBasePath() + ADAPTERLINK_PATH + "?type=" + subsystemType;
        return mockMvc.perform(MockMvcRequestBuilders.get(getSubsystemsUri).contentType(MediaType.APPLICATION_JSON));
    }

}
