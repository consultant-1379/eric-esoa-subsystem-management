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
package com.ericsson.bos.so.subsystemsmanager.log;

import static com.ericsson.bos.so.subsystemsmanager.log.Constants.AUDIT_LOG;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.CATEGORY_KEY;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.CATEGORY_PRIVACY;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.FACILITY_KEY;
import static com.ericsson.bos.so.subsystemsmanager.log.Constants.SUBJECT_KEY;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.shared.spring.security.utils.AuthenticationUtils;

/**
 * User LoggerHandler.
 */
@Component
public class LoggerHandler {
    /**
     * LogAudit
     * @param logger - LOGGER instance
     * @param msg - the message to print
     */
    public void logAudit(final Logger logger, final String msg) {
        MDC.put(FACILITY_KEY, AUDIT_LOG);
        final Optional<String> username = AuthenticationUtils.getUserName();
        username.ifPresent(user -> MDC.put(SUBJECT_KEY, user));
        logger.info("{}", msg);
        MDC.remove(FACILITY_KEY);
        MDC.remove(SUBJECT_KEY);
    }

    /**
     * logPrivacy
     * @param logger - LOGGER instance
     * @param msg - the message to print
     */
    public void logPrivacy(final Logger logger, final String msg) {
        MDC.put(CATEGORY_KEY, CATEGORY_PRIVACY);
        logger.info("{}", msg);
        MDC.remove(CATEGORY_KEY);
    }

    /**
     * Return an input string tagged for privacy logging
     *
     * @param data - the string to be tagged
     * @param tag -  the tag to add
     * @return data string tagged
     */
    public String getPrivacyTaggedData(final String data, final String tag) {
        return "[" + tag + "]" + data + "[/" + tag + "]";
    }
}
