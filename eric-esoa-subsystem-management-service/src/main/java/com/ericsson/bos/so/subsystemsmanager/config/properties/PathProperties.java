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
package com.ericsson.bos.so.subsystemsmanager.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class PathProperties.
 */
@ConfigurationProperties("paths")
@Getter
@Setter
public class PathProperties {

    private SubsystemManagement subsystemManagement = new SubsystemManagement();

    private SubsystemTypes subsystemTypes = new SubsystemTypes();

    private EsoSecurity esoSecurity = new EsoSecurity();

    private V2 v2 = new V2();

    /**
     * Gets the base path.
     *
     * @return the base path
     */
    @Getter

    /**
     * Sets the base path.
     *
     * @param basePath the new base path
     */
    @Setter
    public static class SubsystemManagement {

        private String basePath;

    }

    /**
     * Gets the base path.
     *
     * @return the base path
     */
    @Getter

    /**
     * Sets the base path.
     *
     * @param basePath the new base path
     */
    @Setter
    public static class SubsystemTypes {

        private String basePath;

    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    @Getter

    /**
     * Sets the host.
     *
     * @param host the new host
     */
    @Setter
    public static class EsoSecurity {

        /** The host. */
        private String host;

    }

    /**
     * Gets the eso security host.
     *
     * @return the eso security host
     */
    public String getEsoSecurityHost() {
        return esoSecurity.getHost();
    }

    /**
     * Gets the subsystem types.
     *
     * @return the subsystem types
     */
    @Getter

    /**
     * Sets the subsystem types.
     *
     * @param subsystemTypes the new subsystem types
     */
    @Setter
    public static class V2 {

        private SubsystemManagement subsystemManagement = new SubsystemManagement();

        private SubsystemTypes subsystemTypes = new SubsystemTypes();

        /**
         * Gets the base path.
         *
         * @return the base path
         */
        @Getter

        /**
         * Sets the base path.
         *
         *param basePath the new base path
         */
        @Setter
        public static class SubsystemManagement {

            /** The base path. */
            private String basePath;

        }

        /**
         * Gets the base path.
         *
         * @return the base path
         */
        @Getter

        /**
         * Sets the base path.
         *
         * @param basePath the new base path
         */
        @Setter
        public static class SubsystemTypes {

            /** The base path. */
            private String basePath;

        }
    }

}