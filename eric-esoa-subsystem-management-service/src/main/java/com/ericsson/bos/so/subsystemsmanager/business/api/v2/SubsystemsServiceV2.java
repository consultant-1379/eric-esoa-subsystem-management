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
package com.ericsson.bos.so.subsystemsmanager.business.api.v2;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemsService;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemList;

/**
 * The interface SubsystemsServiceV2
 */
@Component
public interface SubsystemsServiceV2 extends SubsystemsService {

    /**
     * Gets paginated,filtered and sorted list of subsystems
     * @param offset Integer
     * @param limit Integer
     * @param sortAttr String
     * @param sortDir String
     * @param filter String
     * @param tenantName String
     * @return list of subsystems
     */
    SubsystemList getAllSubsystemsPagination(Integer offset, Integer limit, String sortAttr, String sortDir,
                                             String filter, String tenantName);

    /**
     * Gets list of subsystems based on tenant name and search parameters
     * @param tenantName String
     * @param search Map
     * @return details of subsystem
     */
    List<Subsystem> fetchSubsystemByQuery(String tenantName, Map<String, Object> search);

    /**
     * Patches an update to existing subsystem
     * @param subsystemId String
     * @param updatedSubsystem Map
     * @return response of updated subsystem
     */
    Subsystem patchSubsystem(String subsystemId, Map<String, Object> updatedSubsystem);

}
