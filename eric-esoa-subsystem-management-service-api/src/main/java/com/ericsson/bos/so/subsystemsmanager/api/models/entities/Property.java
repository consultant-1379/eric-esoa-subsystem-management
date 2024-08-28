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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Data;

/**
 * The Class Property.
 */
@Entity
@Table(name = "properties")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "connectionPropsId" })
@Schema
@Data
public class Property {

    public static final String PW = "password";
    public static final String HIDDEN_FIELD_VALUE = "********";

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "connectionPropsId", insertable = false, updatable = false)
    @Schema(description = "The Connection property of the property")
    public ConnectionProperties connectionProperties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "propertyId")
    @Schema(accessMode = AccessMode.READ_ONLY, description = "The id of the connection property")
    private Long id;

    @Column(name = "key")
    @Schema(accessMode = AccessMode.READ_ONLY, description = "The key of the connection property")
    private String key;

    @Column(name = "value")
    @Schema(accessMode = AccessMode.READ_ONLY, description = "The value of the connection property")
    private String value;

    @Column(name = "isEncrypted")
    @Schema(accessMode = AccessMode.READ_ONLY, description = "Is the connection property encrypted")
    private boolean encrypted;

    @Override
    public String toString() {
        String displayValue = value;
        if (encrypted || key.equalsIgnoreCase(PW)) {
            displayValue = HIDDEN_FIELD_VALUE;
        }
        return "Property{" + "id=" + id + ", key='" + key + '\'' + ", value='" + displayValue + '\'' + ", encrypted=" + encrypted + '}';
    }

}
