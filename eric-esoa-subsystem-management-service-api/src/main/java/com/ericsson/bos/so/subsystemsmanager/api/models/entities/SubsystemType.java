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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class SubsystemType.
 */
@Entity
@Table(name = "SubsystemType", uniqueConstraints = { @UniqueConstraint(columnNames = "type", name = "uniqueSubsystemTypeConstraint") })
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubsystemType {

    @Id
    @Column(name = "subsystemTypeId")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsystem_type_id_seq")
    @SequenceGenerator(name = "subsystem_type_id_seq", initialValue = 20, allocationSize = 1)
    private Long id;

    private String type;

    private String alias;

    @Transient
    private SubsystemTypeCategory category;

    @Transient
    @JsonProperty
    private Subtype subtype;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "SubsystemTypeSubtype", joinColumns = @JoinColumn(name = "subsystemTypeId"),
        inverseJoinColumns = @JoinColumn(name = "subsystemSubtypeId"))
    private List<Subtype> subtypes;

    /**
     * Resolve category.
     */
    public void resolveCategory() {
        category = PredefinedSubsystemType.resolveCategory(type);
    }

    @Override
    public String toString() {
        return "SubsystemType [id=" + id + ", type=" + type + ", alias=" + alias + ", category=" + category + ", subsystemSubTypes=" + subtypes
                + ", subsystemSubType=" + subtype + "]";
    }
}
