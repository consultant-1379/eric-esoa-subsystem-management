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
package com.ericsson.bos.so.subsystemsmanager.web.controller.v1;

import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEMS;
import static com.ericsson.bos.so.subsystemsmanager.business.util.Constants.SUBSYSTEM_MANAGER;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService;
import com.ericsson.bos.so.subsystemsmanager.log.LoggerHandler;
import com.ericsson.bos.so.subsystemsmanager.api.adapter_links.SubsystemsApi;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class AdapterLinksController
 * Note: Controller implements ..api.adapter_links.SubsystemApi interface.
 */
@Slf4j
@RestController
@RequestMapping(AdapterLinksController.BASE_PATH)
public class AdapterLinksController implements SubsystemsApi {

    public static final String BASE_PATH = SUBSYSTEM_MANAGER + "/v1";

    public static final String ADAPTERLINK_PATH = SUBSYSTEMS + "/adapter-links";

    private static final String DEFAULT_TYPE_NFVO = "NFVO";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterLinksController.class);

    @Autowired
    private LoggerHandler loggerHandler;

    @Autowired
    private AdaptersLinksService adaptersLinksService;

    /**
     * Gets the adapters links.
     *
     * @param subsystemType the subsystem type
     * @return the adapters links
     */
    @Override
    public ResponseEntity<List<String>> getAdaptersLinks(
            @RequestParam(value = "type", required = false, defaultValue = DEFAULT_TYPE_NFVO) String subsystemType) {
        loggerHandler.logAudit(LOGGER, String.format("request received to fetch adapter links for subsystem-type: %s", subsystemType));
        return new ResponseEntity<>(adaptersLinksService.fetchAdapterLinksByType(subsystemType), HttpStatus.OK);
    }
}
