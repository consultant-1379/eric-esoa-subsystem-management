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
package com.ericsson.bos.so.subsystemsmanager.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.bos.so.subsystemsmanager.config.properties.PathProperties;

/**
 * The class ServiceManagementService
 */
@Service
public class ServiceManagementService {

    private static final String SRM_URI = "/srm";
    private static final String CM_VERSION = "/v1.0";
    private static final String CREDENTIALS_MANAGER_URI = "/credentialsmanager";
    private static final String SELF_SERVICE_URI = "/selfservice";

    @Autowired
    private PathProperties pathProperties;

    public String getCredentialsManagerSelfServiceUrl() {
        return pathProperties.getEsoSecurityHost() + CREDENTIALS_MANAGER_URI + CM_VERSION + SELF_SERVICE_URI;
    }

    public String getCredentialsManagerSRMUrl() {
        return pathProperties.getEsoSecurityHost() + CREDENTIALS_MANAGER_URI + CM_VERSION + SRM_URI;
    }
}
