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
package com.ericsson.bos.so.subsystemsmanager.business.util;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.util.ConnectionPropertiesSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ResponseUtil.
 */
@Component
public class ResponseUtil {

    @Autowired
    ConnectionPropertiesSerializer connectionPropertiesSerializer;

    private ObjectMapper getConnectionPropertiesObjectMapper(StdSerializer<ConnectionProperties> connectionPropertiesSerializer){
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule("ConnectionPropertiesSerializer");
        module.addSerializer(ConnectionProperties.class, connectionPropertiesSerializer);
        return objectMapper.registerModule(module);
    }

    /**
     * Convert response.
     *
     * @param object the object
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    public String convertResponse(Object object) throws JsonProcessingException {
        return getConnectionPropertiesObjectMapper(connectionPropertiesSerializer).writeValueAsString(object);
    }
}
