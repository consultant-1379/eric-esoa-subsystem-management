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
package com.ericsson.bos.so.subsystemsmanager.business.util;

import java.io.IOException;
import java.util.Properties;

import com.ericsson.bos.so.subsystemsmanager.business.exception.SubsystemsManagerException;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The class WarningMessageProperties
 */
@Slf4j
public class WarningMessageProperties {


    @Setter(AccessLevel.PRIVATE)
    private static Properties warningProperties;

    private WarningMessageProperties() {
        throw new IllegalStateException("WarningMessageProperties class");
    }
    static {
        loadProperties();
    }

    /**
     * Load properties.
     */
    public static void loadProperties() {

        final Properties warningProperties = new Properties();
        try {
            warningProperties.load(WarningMessageProperties.class.getResourceAsStream("/warning.properties"));
        } catch (final IOException exception) {
            log.error("Error while loading /warning.properties file: " + exception);
            throw (SubsystemsManagerException) exception.getCause();
        } catch (final NullPointerException exception) {
            log.error("File /warning.properties not found", exception);
            throw (SubsystemsManagerException) exception.getCause();
        }
        setWarningProperties(warningProperties);
    }

    /**
     * Retrieve warning message.
     *
     * @param warningCode the warning code
     * @return the string
     */
    public static String retrieveWarningMessage(final String warningCode) {
        return warningProperties.getProperty(warningCode);
    }

}
