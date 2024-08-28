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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Subtype.
 */
@Entity
@Table(name = "subsystemSubtype", uniqueConstraints = { @UniqueConstraint(columnNames = "name", name = "uniqueSubsystemSubTypeNameConstraint") })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subtype {

    @Id
    @Column(name = "subsystemSubtypeId")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsystem_subtype_id_seq")
    @SequenceGenerator(name = "subsystem_subtype_id_seq", sequenceName = "subsystem_subtype_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    @Schema(description = "The subsystem subtype name")
    private String name;

    @Column(name = "alias")
    @Schema(description = "The subsystem subtype alias name")
    private String alias;

    @Override
    public String toString() {
        return "SubsystemSubType [id=" + id + ", name=" + name + ", alias=" + alias + "]";
    }
}
