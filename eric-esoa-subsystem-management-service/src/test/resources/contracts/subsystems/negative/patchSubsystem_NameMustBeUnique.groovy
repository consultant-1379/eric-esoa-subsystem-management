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
package contracts.subsystems.negative

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to modify a subsystem with a name already in use
            then:
                the system responds with status 409 CONFLICT
            ```
        """
        )
    request {
        method 'PATCH'
        url "/subsystem-manager/v1/subsystems/${value(consumer('13'), producer('13'))}"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "name": "duplicate",
                    "url": "${value(consumer(optional(anyUrl())), producer('https://test.newurl/'))}",
                    "vendor": "${value(consumer(optional(anyNonEmptyString())), producer('new-vendor'))}",
                    ${file("ConnectionProperties.json")},
                    "adapterLink": "${value(consumer(optional(anyNonEmptyString())), producer('new-adapter'))}",
                    "subsystemTypeId": "${value(consumer(optional(anyPositiveInt())), producer('1'))}"
                }
            """
        )
        bodyMatchers {
            // avoid deep test on inner part
            jsonPath('$.connectionProperties', byRegex('[\\S\\s]+'))
        }
    }
    response {
        status CONFLICT()
        headers {
            contentType(applicationJson())
        }
        body(
            """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-K-07",
                   "errorData" : ["${value(nonEmpty())}"]
               }
            """
        )
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(1)
                maxOccurrence(1) } )
        }
    }
}
