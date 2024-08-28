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
package com.ericsson.bos.so.subsystemsmanager.api.models.util;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type ConnectionPropertiesSerializer util
 */
@Component
public class ConnectionPropertiesSerializer extends StdSerializer<ConnectionProperties> {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPropertiesSerializer.class);

    /**
     * The type ConnectionPropertiesSerializer Constructor
     */
    public ConnectionPropertiesSerializer() {
        super(ConnectionProperties.class);
    }

    @Override
    public void serialize(ConnectionProperties connectionProperties, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("id", connectionProperties.getId());
        if (connectionProperties.getSubsystemId() != null) {
            jsonGenerator.writeObjectField("subsystemId", connectionProperties.getSubsystemId());
        }

        final List<String> encryptedKeys = new ArrayList<>();

        if (connectionProperties.getProperties() != null && !connectionProperties.getProperties().isEmpty()) {
            connectionProperties.getProperties().forEach(property -> {
                try {
                    jsonGenerator.writeObjectField(property.getKey(), property.getValue());
                    if (property.isEncrypted()) {
                        encryptedKeys.add(property.getKey());
                    }
                } catch (IOException exception) {
                    LOG.info("failed to add field:" + exception.getMessage(), exception);
                }
            });
        }

        jsonGenerator.writeObjectField("encryptedKeys", encryptedKeys);

        if (connectionProperties.getSubsystemUsers() == null) {
            jsonGenerator.writeObjectField("subsystemUsers", new ArrayList<>());
        } else {
            jsonGenerator.writeObjectField("subsystemUsers", connectionProperties.getSubsystemUsers());
        }

        jsonGenerator.writeEndObject();
    }
}
