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
                    "name": "${value(consumer(anyNonEmptyString()), producer('sol005'))}",
                    "subsystemType": {
                        "type": "${value(consumer(anyNonEmptyString()), producer('NFVO'))}",
                    },
                    "url": "${value(consumer(anyUrl()), producer('http://eric-eo-ecmSol005-stub'))}",
                    "vendor": "ericsson",
                    "adapterLink": "eric-eo-ecmsol005-adapter",
                    "connectionProperties": [{
                        "name": "connection1",
                        "tenant": "ECM",
                        "username": "ecmadmin",
                        "password": "CloudAdmin123",
                        "encryptedKeys":[]
                    }],
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
        headers {
            contentType(applicationJson())
        }
        body(file("responseJson/SubsystemResponse.json"))
    }
    priority 5
}
