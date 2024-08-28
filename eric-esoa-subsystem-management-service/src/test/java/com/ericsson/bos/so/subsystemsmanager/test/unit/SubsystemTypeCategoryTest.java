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
package com.ericsson.bos.so.subsystemsmanager.test.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.PredefinedSubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemTypeCategory;

import org.junit.Test;

/**
 * The Class SubsystemTypeCategoryTest.
 */
public class SubsystemTypeCategoryTest {

    /**
     * GIVEN nfvo subsystem type WHEN category resolved THEN primary category returned.
     */
    @Test
    public void GIVEN_nfvoSubsystemType_WHEN_categoryResolved_THEN_primaryCategoryReturned() {
        assertThat(PredefinedSubsystemType.resolveCategory(PredefinedSubsystemType.NFVO.getType()))
                .isEqualTo(SubsystemTypeCategory.PRIMARY);
    }

    /**
     * GIVEN physical device subsystem type WHEN category resolved THEN custom category returned.
     */
    @Test
    public void GIVEN_physicalDeviceSubsystemType_WHEN_categoryResolved_THEN_customCategoryReturned() {
        assertThat(PredefinedSubsystemType.resolveCategory(PredefinedSubsystemType.PHYSICAL_DEVICE.getType()))
                .isEqualTo(SubsystemTypeCategory.CUSTOM);
    }

}
