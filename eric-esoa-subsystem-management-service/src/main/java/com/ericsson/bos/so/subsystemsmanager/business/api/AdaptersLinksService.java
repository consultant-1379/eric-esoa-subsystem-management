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

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * The interface AdaptersLinksService
 */
@Component
public interface AdaptersLinksService {

    /**
     * Get adapter links by K8's pod labels based on subsystem type
     * @param subsystemType String
     * @return list of adapter links
     */
    List<String> fetchAdapterLinksByType(String subsystemType);

}
