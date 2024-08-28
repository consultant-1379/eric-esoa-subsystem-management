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
 * The Class SubsystemUser.
 */
@Entity
@Table(name = "SubsystemUser")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "connectionPropsId" })
@Schema
@Data
public class SubsystemUser {

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "connectionPropsId", insertable = false, updatable = false)
    @Schema(description = "The connection property of the subsystem user")
    public ConnectionProperties connectionProperties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subsystemUserId")
    @Schema(accessMode = AccessMode.READ_ONLY, description = "The Id of the subsystem user")
    private Long id;

    @Column(name = "connectionPropsId")
    @Schema(accessMode = AccessMode.READ_ONLY, description = "The connection property Id of the subsystem user")
    private Long connectionPropsId;

    @Override
    public String toString() {
        return "SubsystemUser [id=" + id + "]";
    }

}
