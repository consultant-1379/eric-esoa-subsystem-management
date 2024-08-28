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
package contracts.v2.subsystems.negative

import org.springframework.cloud.contract.spec.Contract
import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText('{"Content-Type":"application/json","Accept":"*/*","X-login":"so-user","X-password":"Ericsson@12345"}')

Contract.make {
    description(
            """
            ```
            when:
                A client makes a request to modify  subsystem
            then:
                the system responds with status 404 Not Found
            ```
        """
    )
    request {
        method 'PATCH'
        url "/subsystem-manager/v2/subsystems/${value(consumer('11'), producer('11'))}"
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
                        "alias": "${value(consumer(anyNonEmptyString()), producer('Authentication systems'))}",
                        "subtype": {
                            "id": "${value(consumer(anyInteger()), producer('1'))}",
                            "name": "${value(consumer(anyNonEmptyString()), producer('Oauth2ClientCredentials'))}",
                            "alias": "${value(consumer(anyNonEmptyString()), producer('Oauth2 Client Credentials'))}"
                        }
                    },
                    "url": "${value(consumer(anyUrl()), producer('http://eric-eo-ecm-stub'))}",
                    "connectionProperties": [{
                        "id": "3",
                        "username": "administratoreeddd",
                        "password": "TestPassw0rdddd",
                        "auth_url": "/auth/v1",
                        "auth_type": "Oauth2",
                        "client_id": "12131dd",
                        "client_secret": "13131",
                        "grant_type": "2112",
                        "auth_headers": "${value(object)}",
                        "test": "testing2",
                        "encryptedKeys":["password","client_secret"],
                        "subsystemUsers": []
                    }],
                    "vendor": "${value(consumer(anyNonEmptyString()), producer('ericsson'))}"
                }
             """
        )
    }
    response {
        status NOT_FOUND()
        headers {
            contentType(applicationJson())
        }
        body(
                """
                {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-J-13",
                   "errorData" : ["${value(nonEmpty())}"]
                }
            """
        )
    }
}
