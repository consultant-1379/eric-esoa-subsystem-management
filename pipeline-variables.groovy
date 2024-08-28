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
env.JAVA_VERSION="17"
//env.SKIP_CHECKSTYLES="true"
//env.SKIP_VULNERABILITY_ANALYSIS="true"
//env.SKIP_FOSSA="true"
//env.SKIP_SONAR="true"
//env.EXCLUDE_PATH="<exclude-path-arguments>"
//env.MAVEN_RELEASE_FLAGS="<extra flags>"
env.VALUES_FILE_FOR_HELM_CHART_DESIGN_RULE_CHECK="dr-values.yaml"
env.SKIPPED_HELM_CHART_DESIGN_RULES=
        "-DhelmDesignRule.config.DR-D1120-061=skip " +
        "-DhelmDesignRule.config.DR-D1123-125=skip " +
        "-DhelmDesignRule.config.DR-D1121-060=skip " +
        "-DhelmDesignRule.config.DR-D1123-128=skip " +
        "-DhelmDesignRule.config.DR-D1123-127=skip " +
        "-DhelmDesignRule.config.DR-D1123-133=skip " +
        "-DhelmDesignRule.config.DR-D1124-021=skip " +
        "-DhelmDesignRule.config.DR-D1126-005=skip " +
        "-DhelmDesignRule.config.DR-D470217-001=skip " +
        "-DhelmDesignRule.config.DR-D1121-011=skip "
env.SKIP_LICENSE="false"