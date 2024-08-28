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
                A client makes a request to create a new subsystem
            then:
                the subsystem is created
            ```
        """
    )
    request {
        method 'POST'
        url "/subsystem-manager/v2/subsystems"

        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
                """
                {
                    "name": "auth",
                    "subsystemType": {
                        "type": "AuthenticationSystems",
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
                        "encryptedKeys":["password","client_secret"],
                        "subsystemUsers": [{"id":5,"connectionPropsId":3},{"id":16,"connectionPropsId":3}]
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
        status CREATED()
        body(file("responseJson/PostSubsystemWithSubtypeResponse.json"))
    }
    priority 5
}