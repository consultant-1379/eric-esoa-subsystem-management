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
package com.ericsson.bos.so.subsystemsmanager.test.contracts.util

import java.util.Map.Entry
import java.util.regex.PatternSyntaxException

class MockResponseFormatter {

    def static prepareMockResponseFromFile(final String fileName, final String... substitutions) {
        final File bodyFile = new File(getClass().getResource(fileName).toURI())
        try {
            return String.format(bodyFile.text, substitutions)
        } catch (IllegalArgumentException ignored) {
            return ""
        }
    }

    def static prepareMockResponseFromFile(final String fileName, final Map<String,String> substitutions) {
        String body = new File(getClass().getResource(fileName).toURI()).text
        try {
            substitutions.each { Entry e ->
                body = body.replaceAll(e.key,e.value)
            }
            return body;
        } catch (PatternSyntaxException ignored) {
            return ""
        }
    }

}
