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
package com.ericsson.bos.so.subsystemsmanager.api.models.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class SubsystemTypeRequest.
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SubsystemTypeRequest {

    private String type;

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