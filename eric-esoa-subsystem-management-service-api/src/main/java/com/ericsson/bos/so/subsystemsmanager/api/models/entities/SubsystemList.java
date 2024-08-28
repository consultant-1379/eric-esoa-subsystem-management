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

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class SubsystemList.
 */
public class SubsystemList {

    @JsonProperty("total")
    private int total;
    @JsonProperty("items")
    private List<Subsystem> items = Collections.emptyList();

    @JsonProperty("total")
    public int getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(int total) {
        this.total = total;
    }

    @JsonProperty("items")
    public List<Subsystem> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<Subsystem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SubsystemList [total= ").append(total).append(", items= ").append(items).append("]");
        return sb.toString();
    }

}
