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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemSubtypeAlreadyExistsException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemTypeAlreadyExistsException;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemTypeRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.BaseIntegrationTest;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.ericsson.bos.so.subsystemsmanager.api.models.request.v2.SubsystemTypeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class PostSubsystemTypeTest.
 */
public class PostSubsystemTypeTest extends BaseIntegrationTest {

    private static final String SSM_J_17 = "SSM-J-17";
    private static final String SSM_B_49 = "SSM-B-49";
    private static final String SSM_B_48 = "SSM-B-48";
    @Autowired
    private SubsystemTypeRepository subsystemTypeRepository;

    @MockBean
    private KmsServiceImpl kmsService;

    /**
     * GIVEN unique type WHEN create subsystem type THEN created response and record persisted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_uniqueType_WHEN_createSubsystemType_THEN_createdResponse_and_recordPersisted() throws Exception {
        // given
        final SubsystemTypeRequest subsystemTypeRequest = getSubsystemTypeRequest();

        // when
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isCreated());

        assertThat(subsystemTypeRepository.findByType(subsystemTypeRequest.getType())).isPresent();
    }

    /**
     * GIVEN unique type WHEN create subsystem type without subtypes THEN created response and record persisted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_uniqueType_WHEN_createSubsystemTypeWithoutSubtypes_THEN_createdResponse_and_recordPersisted() throws Exception {
        // given
        final SubsystemTypeRequest subsystemTypeRequest = getSubsystemTypeRequestWithoutSubtypes();

        // when
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isCreated());

        assertThat(subsystemTypeRepository.findByType(subsystemTypeRequest.getType())).isPresent();
    }

    /**
     * GIVEN unique name WHEN create subsystem subtype under specific subsystem type THEN created response and record persisted.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_uniqueName_WHEN_createSSTUnderSpecSubsystemType_THEN_createdResp_and_recordPer() throws Exception { // given
        // given
        final SubsystemType persistSubsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long subsystemTypeId = persistSubsystemType.getId();
        final Subtype subtype = new Subtype();
        subtype.setName(Constants.VODAFONE);
        subtype.setAlias(Constants.VODAFONE);
        // when
        response = postSubsystemSubtype(subsystemTypeId, subtype);
        // then
        response.andExpect(status().isCreated());

        assertTrue(subsystemTypeRepository.findById(subsystemTypeId).get().getSubtypes().size() > 1);
    }

    /**
     * Test response is method not allowed.
     *
     * @throws Exception the exception
     */
    @Test
    public void testResponseIsMethodNotAllowed() throws Exception {
        final SubsystemType persistSubsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long subsystemTypeId = persistSubsystemType.getId();
        final Subtype subtype = new Subtype();
        subtype.setName(Constants.VODAFONE);
        subtype.setAlias(Constants.VODAFONE);
        // when
        final String postSubsystemTypeUrl = pathProperties.getV2().getSubsystemTypes().getBasePath() + Constants.$_SLASH + subsystemTypeId
                + Constants.$_SLASH + Constants.SUBTYPE;
        final ObjectMapper objectMapper = new ObjectMapper();
        final String subtypeJson = objectMapper.writeValueAsString(subtype);
        response = mockMvc.perform(patch(postSubsystemTypeUrl).content(subtypeJson).contentType(MediaType.APPLICATION_JSON));
        // then
        response.andExpect(status().isMethodNotAllowed());
        checkResponseContainsInternalErrorCode("SSM-B-29");
        checkResponseContainsUserMessage("Bad request.");
    }

    /**
     * Test response is not found.
     *
     * @throws Exception the exception
     */
    @Test
    public void testResponseIsNotFound() throws Exception {
        final SubsystemType persistSubsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long subsystemTypeId = persistSubsystemType.getId();
        final Subtype subtype = new Subtype();
        subtype.setName(Constants.VODAFONE);
        subtype.setAlias(Constants.VODAFONE);
        // when
        final String postSubsystemTypeUrl = pathProperties.getV2().getSubsystemTypes().getBasePath() + Constants.$_SLASH + subsystemTypeId
                + Constants.$_SLASH + Constants.SUBTYPE;
        final ObjectMapper objectMapper = new ObjectMapper();
        final String subtypeJson = objectMapper.writeValueAsString(subtype);
        response = mockMvc.perform(post(postSubsystemTypeUrl+"asd").content(subtypeJson).contentType(MediaType.APPLICATION_JSON));
        // then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode("SSM-B-21");
        checkResponseContainsUserMessage("Endpoint not found.");
    }

    /**
     * Test response is bad request if malformed request.
     *
     * @throws Exception the exception
     */
    @Test
    public void testResponseIsBadRequestIfMalformedRequest() throws Exception {
        final SubsystemType persistSubsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long subsystemTypeId = persistSubsystemType.getId();
        final Subtype subtype = new Subtype();
        subtype.setName(Constants.VODAFONE);
        subtype.setAlias(Constants.VODAFONE);
        // when
        final String postSubsystemTypeUrl = pathProperties.getV2().getSubsystemTypes().getBasePath() + Constants.$_SLASH + subsystemTypeId
                + Constants.$_SLASH + Constants.SUBTYPE;
        final ObjectMapper objectMapper = new ObjectMapper();
        final String subtypeJson = objectMapper.writeValueAsString(subtype);
        response = mockMvc.perform(post(postSubsystemTypeUrl).content("asd"+subtypeJson).contentType(MediaType.APPLICATION_JSON));
        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode("SSM-B-29");
        checkResponseContainsUserMessage("Bad request.");
    }

    /**
     * GIVEN no type WHEN create subsystem type THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_noType_WHEN_createSubsystemType_THEN_badRequestResponse() throws Exception {
        // when
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest();
        subsystemTypeRequest.setType(null);
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isBadRequest());

    }

    /**
     * GIVEN predefined type WHEN create subsystem type THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_predefinedType_WHEN_createSubsystemType_THEN_conflictResponse() throws Exception {
        // given
        final String newSubsystemName = PredefinedSubsystemType.DOMAIN_MANAGER.getType();
        final List<Subtype> subtypes = new ArrayList<Subtype>();

        // when
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest(newSubsystemName, PredefinedSubsystemType.DOMAIN_MANAGER.name(),
                subtypes);
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
        final String newSubsystemType = Constants.OPERATOR;
        persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final SubsystemTypeRequest subsystemTypeRequest = new SubsystemTypeRequest();
        subsystemTypeRequest.setType(newSubsystemType);
        // when

        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isConflict());
        checkResponseContainsInternalErrorCode(SubsystemTypeAlreadyExistsException.INTERNAL_ERROR_CODE);
    }

    /**
     * GIVEN create subsystem type WHEN create subsystem type without subtype name THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_createSubsystemType_WHEN_createSubsystemTypeWithoutSubtypeName_THEN_BadRequestResponse() throws Exception {
        // given
        final SubsystemTypeRequest subsystemTypeRequest = getSubsystemTypeRequestWithoutSubtypeName();
        // when

        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode(SSM_B_48);
    }

    /**
     * GIVEN create subsystem type WHEN create subsystem type with same subtype name THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_createSubsystemType_WHEN_createSubsystemTypeWithSameSubtypeName_THEN_BadRequestResponse() throws Exception {
        // given
        final SubsystemTypeRequest subsystemTypeRequest = getSubsystemTypeRequestWithSameSubtypeName();
        // when
        response = postSubsystemType(subsystemTypeRequest);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode(SSM_B_49);
    }

    /**
     * GIVEN existing subsystem subtype WHEN create same subsystem subtype THEN conflict response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemSubtype_WHEN_createSameSubsystemSubtype_THEN_conflictResponse() throws Exception {
        // given
        final SubsystemType persistSubsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long subsystemTypeId = persistSubsystemType.getId();
        final Subtype subtype = new Subtype();
        subtype.setName(Constants.AIRTEL);
        subtype.setAlias(Constants.AIRTEL);
        // when
        response = postSubsystemSubtype(subsystemTypeId, subtype);

        // then
        response.andExpect(status().isConflict());
        checkResponseContainsInternalErrorCode(SubsystemSubtypeAlreadyExistsException.INTERNAL_ERROR_CODE);
    }

    /**
     * GIVEN existing subsystem subtype WHEN create subsystem subtype without subtype name THEN bad request response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemSubtype_WHEN_createSubsystemSubtypeWithoutSubtypeName_THEN_BadRequestResponse() throws Exception {
        // given
        final SubsystemType persistSubsystemType = persistSubsystemType(Constants.OPERATOR, Constants.OPERATOR, Constants.AIRTEL, Constants.AIRTEL);
        final long subsystemTypeId = persistSubsystemType.getId();
        final Subtype subtype = new Subtype();
        subtype.setAlias(Constants.AIRTEL);
        // when
        response = postSubsystemSubtype(subsystemTypeId, subtype);

        // then
        response.andExpect(status().isBadRequest());
        checkResponseContainsInternalErrorCode(SSM_B_48);
    }

    /**
     * GIVEN existing subsystem subtype WHEN create same subsystem subtype with wrong subsystem id THEN not foundt response.
     *
     * @throws Exception the exception
     */
    @Test
    public void GIVEN_existingSubsystemSubtype_WHEN_createSameSubsystemSubtypeWithWrongSubsystemId_THEN_NotFoundtResponse() throws Exception {
        // given
        final long subsystemTypeId = 936L;
        final Subtype subtype = new Subtype();
        subtype.setName(Constants.AIRTEL);
        subtype.setAlias(Constants.AIRTEL);
        // when
        response = postSubsystemSubtype(subsystemTypeId, subtype);

        // then
        response.andExpect(status().isNotFound());
        checkResponseContainsInternalErrorCode(SSM_J_17);
    }

    private ResultActions postSubsystemType(final SubsystemTypeRequest subsystemTypeRequest) throws Exception {
        final String postSubsystemTypeUrl = pathProperties.getV2().getSubsystemTypes().getBasePath();
        return mockMvc.perform(post(postSubsystemTypeUrl).content(subsystemTypeRequest.toJsonString()).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions postSubsystemSubtype(final long subsystemTypeId, final Subtype subtype) throws Exception {
        final String postSubsystemTypeUrl = pathProperties.getV2().getSubsystemTypes().getBasePath() + Constants.$_SLASH + subsystemTypeId
                + Constants.$_SLASH + Constants.SUBTYPE;
        final ObjectMapper objectMapper = new ObjectMapper();
        final String subtypeJson = objectMapper.writeValueAsString(subtype);
        return mockMvc.perform(post(postSubsystemTypeUrl).content(subtypeJson).contentType(MediaType.APPLICATION_JSON));
    }

    private SubsystemTypeRequest getSubsystemTypeRequest() {
        final String newSubsystemType = Constants.OPERATOR;
        final String alias = Constants.OPERATOR;
        final Subtype subtpe = new Subtype();
        subtpe.setName(Constants.IDEA);
        subtpe.setAlias(Constants.IDEA);
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        subtypes.add(subtpe);
        return new SubsystemTypeRequest(newSubsystemType, alias, subtypes);
    }

    private SubsystemTypeRequest getSubsystemTypeRequestWithoutSubtypes() {
        final String newSubsystemType = Constants.OPERATOR;
        final String alias = Constants.OPERATOR;
        return new SubsystemTypeRequest(newSubsystemType, alias, null);
    }

    private SubsystemTypeRequest getSubsystemTypeRequestWithoutSubtypeName() {
        final String newSubsystemType = Constants.OPERATOR;
        final String alias = Constants.OPERATOR;
        final Subtype subtpe = new Subtype();
        subtpe.setAlias(Constants.IDEA);
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        subtypes.add(subtpe);
        return new SubsystemTypeRequest(newSubsystemType, alias, subtypes);
    }

    private SubsystemTypeRequest getSubsystemTypeRequestWithSameSubtypeName() {
        final String newSubsystemType = Constants.OPERATOR;
        final String alias = Constants.OPERATOR;
        final Subtype subtpe = new Subtype();
        subtpe.setName(Constants.IDEA);
        subtpe.setAlias(Constants.IDEA);

        final Subtype subtpe1 = new Subtype();
        subtpe1.setName(Constants.IDEA);
        subtpe1.setAlias(Constants.IDEA);
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        subtypes.add(subtpe);
        subtypes.add(subtpe);
        return new SubsystemTypeRequest(newSubsystemType, alias, subtypes);
    }

    private SubsystemType persistSubsystemType(String subsystemType, String subsystemTypeAlias, String subtypeName, String subtypeNameAlias) {
        final Subtype subtpe = new Subtype();
        subtpe.setName(subtypeName);
        subtpe.setAlias(subtypeNameAlias);
        final List<Subtype> subtypes = new ArrayList<Subtype>();
        subtypes.add(subtpe);
        final SubsystemType persistedSubsystemType = subsystemTypeFactory
                .persistSubsystemType(subsystemType, subsystemTypeAlias, subtypes);
        return persistedSubsystemType;
    }

}
