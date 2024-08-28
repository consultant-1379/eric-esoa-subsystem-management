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
package com.ericsson.bos.so.subsystemsmanager.business.api;

import com.ericsson.bos.so.subsystemsmanager.business.util.ResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

/**
 * The class GenericSerializer
 */
@Service
@Configurable
public class GenericSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(GenericSerializer.class);

    @Autowired
    private ResponseUtil responseUtil;

    /**
     * Serializes passed object to expected json string
     * @param object Object
     * @return list of adapter links
     */
    public String serializeObjectToExpectedJson(Object object){
        String objectAsJsonString = "";
        try {
            objectAsJsonString = responseUtil.convertResponse(object);
        } catch (JsonProcessingException exception) {
            LOG.info("Failed to serialize object to expected JSON: " + exception.getMessage(), exception);
        }
        return objectAsJsonString;
    }

}
