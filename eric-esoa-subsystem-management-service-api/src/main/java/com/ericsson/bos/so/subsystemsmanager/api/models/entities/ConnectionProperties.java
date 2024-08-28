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
package com.ericsson.bos.so.subsystemsmanager.api.models.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.ericsson.bos.so.subsystemsmanager.api.models.util.ConnectionPropertiesSerializer;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class ConnectionProperties.
 */
@Entity
@Table(name = "connection_properties")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "subsystemId", "properties", "subsystemUsers" })
@JsonSerialize(using = ConnectionPropertiesSerializer.class)
@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionProperties {

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "subsystemId", insertable = false, updatable = false)
    @Schema(description = "The subsystem of the connection property")
    public Subsystem subsystem;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "connectionPropsId", referencedColumnName = "connectionPropsId")
    @Schema(hidden = true, description = "The List of subsystem users of the connection property")
    public List<SubsystemUser> subsystemUsers;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "connectionPropsId")
    @Schema(hidden = true, accessMode = AccessMode.READ_ONLY, description = "The id of the connection property")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "connectionPropsId", referencedColumnName = "connectionPropsId")
    @Schema(name = "string", example = "newToken", description = "The list of properties of the connection property")
    private List<Property> properties;

    @Transient
    @Schema(name = "encryptedKeys", description = "The selected property to be encrypted")
    private List<String> encryptedKeys;

    @Schema(hidden = true, accessMode = AccessMode.READ_ONLY, description = "The Subsystem Id of the connection property")
    private Long subsystemId;

    @Override
    public String toString() {
        return "ConnectionProperties{" + "id=" + id + ", properties=" + properties + '}';
    }

    /**
     * Sets the property.
     *
     * @param jsonKey the json key
     * @param jsonValue the json value
     */
    @JsonAnySetter
    public void setProperty(String jsonKey, String jsonValue) {
        final String PW_PROPERTY = "password";

        if (properties == null) {
            properties = new ArrayList<>();
        }
        final Property property = new Property();
        property.setKey(jsonKey);
        property.setValue(jsonValue);
        property.setEncrypted(PW_PROPERTY.equals(jsonKey));
        properties.add(property);
    }

}
