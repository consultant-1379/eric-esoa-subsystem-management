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
package com.ericsson.bos.so.subsystemsmanager.business.fieldfilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.exception.FailedJSONParameterException;
import com.ericsson.bos.so.subsystemsmanager.business.exception.FailedJSONProcessingException;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * The class SubsystemFilterUtil
 */
@Component
public class SubsystemFilterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SubsystemFilterUtil.class);

    /**
     * Extracts fields from subsystem details
     * @param fields String
     * @param subsystemFilterEntity SubsystemFilterEntity
     * @return filtered fields of subystem
     */
    public Subsystem extractFieldsFromSubsystem(final String fields, final SubsystemFilterEntity subsystemFilterEntity) {
        LOG.info("extractSubnetFields(): received request to extract fields from subsystem");
        return filterFields(fields, subsystemFilterEntity);
    }

    /**
     * Extracts fields from list of subsystems
     * @param fields String
     * @param subsystemsResults List
     * @return list of subsystems with filtered fields
     */
    public List<Subsystem> extractFieldsFromSubsystemList(final String fields, final List<SubsystemFilterEntity> subsystemsResults) {
        LOG.debug("extractFieldsFromSubnetList(): received request to extract fields from subsystem list, fields: {}, subsystemList: {} ", fields,
                subsystemsResults);
        final List<Subsystem> responseEntities = new ArrayList<>();
        for (SubsystemFilterEntity subnet : subsystemsResults) {
            final Subsystem filteredSubnet = filterFields(fields, subnet);
            responseEntities.add(filteredSubnet);
        }
        return responseEntities;
    }

    /**
     * Extracts fields from connection properties
     * @param fields String
     * @param subsystemsResults List
     * @return list of subsystems with filtered fields
     */
    public List<Object> extractFieldsFromConnectionProperties(final String fields, final List<SubsystemFilterEntity> subsystemsResults) {
        LOG.debug("extractFieldsFromConnectionProperties(): received request to extract fields ConnectionProperties, fields: {}, subsystemList: {} ",
                fields, subsystemsResults);
        final List<Object> responseEntities = new ArrayList<>();
        for (SubsystemFilterEntity subnet : subsystemsResults) {
            final Subsystem filteredSubnet = filterFields(fields, subnet);
            responseEntities.add(filteredSubnet);
        }
        return responseEntities;
    }

    /**
     * Extracts single field from the list of subsystems
     * @param field String
     * @param subsystemList List
     * @return list of subsystems with filtered fields
     */
    public List<Object> extractSingleFieldFromSubsystemList(final String field, final List<Subsystem> subsystemList) {
        LOG.info("extractSingleFieldFromSubsystemList() received request for subsystemList: {}", subsystemList);
        try {
            final List<Object> filteredValues = new ArrayList<>();
            final String json = new ObjectMapper().writeValueAsString(subsystemList);
            final JSONArray ary = new JSONArray(json);
            for (int i = 0; i < ary.length(); i++) {
                final JSONObject obj = ary.getJSONObject(i);
                filteredValues.add(obj.get(field));
            }
            return filteredValues;
        } catch (JSONException | JsonProcessingException ex) {
            LOG.error("extractSingleFieldFromSubsystemList() JSONParameterException occur: ", ex);
            throw new FailedJSONParameterException(field);
        }
    }

    private Subsystem filterFields(final String fields, final SubsystemFilterEntity subsystem) {
        LOG.info("filterFields() : received request");
        try {
            final Set<String> filterProperties = buildFilterProperties(new StringTokenizer(fields, ","));
            final FilterProvider filters = new SimpleFilterProvider().addFilter("subsystemFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept(filterProperties));
            final String json = new ObjectMapper().writer(filters).writeValueAsString(subsystem);
            return new ObjectMapper().readValue(json, Subsystem.class);
        } catch (JsonProcessingException ex) {
            LOG.error("filterFields() JSONProcessingException occur: ", ex);
            throw new FailedJSONProcessingException(subsystem.getName());
        }
    }

    private Set<String> buildFilterProperties(final StringTokenizer st) {
        final Set<String> filterProperties = new HashSet<>();
        while (st.hasMoreTokens()) {
            filterProperties.add(st.nextToken());
        }
        return filterProperties;
    }
}
