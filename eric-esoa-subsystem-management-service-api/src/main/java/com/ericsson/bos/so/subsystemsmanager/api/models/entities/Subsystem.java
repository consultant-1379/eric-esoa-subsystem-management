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

import java.util.List;
import java.util.UUID;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Subsystem.
 */
@Entity
@Table(name = "Subsystem", uniqueConstraints = { @UniqueConstraint(columnNames = "name", name = "uniqueSubsystemNameConstraint") })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "id", "apiKey", "name", "subsystemType", "healthCheckTime", "url", "operationalState", "connectionProperties", "vendor" })
@Schema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Subsystem {

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "subsystemTypeId", insertable = false, updatable = false)
    @Schema(description = "The subsystem Type")
    public SubsystemType subsystemType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subsystemId")
    @Schema(hidden = true, accessMode = AccessMode.READ_ONLY, description = "The Id of the subsystem")
    private Long id;

    @Column(name = "apiKey", nullable = false, updatable = false)
    @Schema(accessMode = AccessMode.READ_ONLY, description = "api-key of the subsystem")
    private UUID apiKey;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "subsystemId", referencedColumnName = "subsystemId")
    @Schema(description = "The list of connection properties of the subsystem")
    private List<ConnectionProperties> connectionProperties;

    @Column(name = "name")
    @Schema(description = "The subsystem name")
    private String name;

    @Column(name = "healthCheckTime")
    @Schema(hidden = true, description = "The health check time of the subsystem")
    private String healthCheckTime;

    @Column(name = "url")
    @Schema(description = "The url of the subsystem")
    private String url;

    @Column(name = "operationalState")
    @Schema(hidden = true, description = "The operational state of the subsystem")
    private OperationalState operationalState;

    @Column(name = "subsystemTypeId")
    @Schema(example = "1", description = "The Id of the subsystem type")
    private Long subsystemTypeId;

    @Column(name = "subsystemSubtypeId")
    @Schema(example = "1", description = "The Id of the subsystem sub type")
    private Long subsystemSubtypeId;

    @Column(name = "vendor")
    @Schema(description = "Vendor's name")
    private String vendor;

    @Column(name = "adapterLink")
    @Schema(description = "Adapter link type")
    private String adapterLink;

    @JsonProperty
    public SubsystemType getSubsystemType() {
        return subsystemType;
    }

    @PrePersist
    private void prePersistFunction() {
        apiKey = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "Subsystem [id=" + id + ", apiKey=" + apiKey + ", connectionProperties=" + connectionProperties + ", subsystemType=" + subsystemType
                + ", name=" + name + ", healthCheckTime=" + healthCheckTime + ", url=" + url + ", operationalState=" + operationalState
                + ", subsystemTypeId=" + subsystemTypeId + ", vendor=" + vendor + ", adapterLink=" + adapterLink + ", subsystemSubtypeId="
                + subsystemSubtypeId + "]";
    }

}
