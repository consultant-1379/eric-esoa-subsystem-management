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
package com.ericsson.bos.so.subsystemsmanager.config.features;

import org.springframework.stereotype.Component;

/**
 * The Class FeatureRouter.
 */
@Component
public class FeatureRouter {

    public static final String SOL005_FEATURE = "sol005";

    private final FeatureProperties featureProperties;

    /**
     *
     * @param featureProperties the featureProperties
     */
    public FeatureRouter(FeatureProperties featureProperties) {
        this.featureProperties = featureProperties;
    }

    /**
     *
     * @param featureName the featureName
     * @return Boolean
     */
    public Boolean featureIsEnabled(String featureName) {
        return featureProperties.getFeatures().get(featureName);
    }

    /**
     * Sets the feature
     * @param featureName the featureName
     * @param isEnabled the isEnabled
     */
    public void setFeature(String featureName, Boolean isEnabled) {
        featureProperties.getFeatures().put(featureName, isEnabled);
    }

}
