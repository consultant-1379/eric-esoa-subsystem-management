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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;


/**
 * The Class CucumberIntegrationTests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features"}, plugin = {"pretty", "html:target/reports/cucumber/html",
    "json:target/cucumber.json", "usage:target/usage.jsonx", "junit:target/junit.xml"})
public class CucumberIntegrationTests {

}