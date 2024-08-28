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
package com.ericsson.bos.so.subsystemsmanager.business.subsystem;


import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import lombok.ToString;

/**
 * Adapter for NotificationService
 * There should be just some minimal information sent to NotificationService
 *
 * @see Subsystem
 */
@ToString
public class SubsystemNotificationServiceAdapter {

    private final transient Subsystem subsystem;

    /**
     * Instantiates a new subsystem notification service adapter.
     *
     * @param subsystem the subsystem
     */
    public SubsystemNotificationServiceAdapter(Subsystem subsystem) {
        this.subsystem = subsystem;
    }

    public Long getId() {
        return subsystem.getId();
    }

    public String getName() {
        return subsystem.getName();
    }
}
