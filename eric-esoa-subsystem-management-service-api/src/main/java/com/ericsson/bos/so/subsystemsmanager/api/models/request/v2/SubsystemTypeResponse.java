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
package com.ericsson.bos.so.subsystemsmanager.api.models.request.v2;

import java.util.List;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subtype;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemTypeResponse.
 */
@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SubsystemTypeResponse {

    private Long id;
    private String type;
    private String alias;
    private List<Subtype> subtypes;


    /**
     * To json string.
     *
     * @return the jsonString
     * @throws JsonProcessingException the json processing exception
     */
    public String toJsonString() throws JsonProcessingException {
        final String jsonString = new ObjectMapper().writeValueAsString(this);
        log.debug(this.getClass().getSimpleName() + " JSON: " + jsonString);

        return jsonString;
    }

}