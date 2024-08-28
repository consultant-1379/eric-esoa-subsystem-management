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
package contracts.v2.subsystems.positive

import org.springframework.cloud.contract.spec.Contract
import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText('{"Content-Type":"application/json","Accept":"*/*","X-login":"so-user","X-password":"Ericsson@12345"}')

Contract.make {
    description(
            """
            ```
            when:
                A client makes a request to modify a specific subsystem
            then:
                the modified subsystem is returned
            ```
        """
    )
    request {
        method 'PATCH'
        url "/subsystem-manager/v2/subsystems/${value(consumer(anyPositiveInt()), producer('2'))}"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
                """
                {
                    "id": "${value(consumer(anyInteger()), producer('2'))}",
                    "apiKey": "${value(consumer(anyNonEmptyString()), producer('7da8bb76-07b5-497a-bfa0-8779b9f6387d'))}",
                    "name": "${value(consumer(anyNonEmptyString()), producer('auth'))}",
                    "subsystemType": {
                        "id": "${value(consumer(anyInteger()), producer('9'))}",
                        "type": "${value(consumer(anyNonEmptyString()), producer('AuthenticationSystems'))}",
                        "alias": "${value(consumer(anyNonEmptyString()), producer('AuthenticationSystems'))}",
                        "subtype": {
                            "name": "Oauth2ClientCredentials"
                        }
                    },
                    "url": "${value(consumer(anyUrl()), producer('http://eric-eo-ecm-stub'))}",
                    "connectionProperties": [{
                        "username": "administratoreeddd",
                        "password": "TestPassw0rdddd",
                        "auth_url": "/auth/v1",
                        "auth_type": "Oauth2",
                        "client_id": "12131dd",
                        "client_secret": "13131",
                        "grant_type": "2112",
                        "auth_headers": "${value(object)}",
                        "subsystemUsers": []
                    }],
                    "vendor": "${value(consumer(anyNonEmptyString()), producer('ericsson'))}"
                }
             """
        )
        bodyMatchers {
            // avoid deep test on inner part
            jsonPath('$.connectionProperties', byRegex('[\\S\\s]+'))
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("responseJson/PostSubsystemWithSubtypeResponse.json").asString().replaceAll("%id", fromRequest().path(3).serverValue.toString()))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.connectionProperties', byType())
            jsonPath('$.subsystemType', byType())
        }
    }
    priority 5
}
