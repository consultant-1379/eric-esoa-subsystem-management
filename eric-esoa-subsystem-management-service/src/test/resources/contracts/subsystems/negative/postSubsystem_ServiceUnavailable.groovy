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

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description(
        """
            ```
            given:
                The Credential Manager service is unavailable
            when:
                A client makes a request to create a subsystem
            then:
                the system responds with status 503 SERVICE_UNAVAILABLE
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v1/subsystems"

        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "subsystemTypeId": "${value(consumer(anyPositiveInt()), producer('1'))}",
                    "name": "Unavailable",
                    "url": "${value(consumer(anyUrl()), producer('https://subsystem-1/'))}",
                    ${file("ConnectionProperties.json")},
                    "vendor": "${value(consumer(anyNonEmptyString()), producer('subsystem-vendor'))}",
                    "adapterLink": "${value(consumer(anyNonEmptyString()), producer('eric-eo-ecm-adapter'))}"
                }
             """
        )
        bodyMatchers {
            // avoid deep test on inner part
            jsonPath('$.connectionProperties', byRegex('[\\S\\s]+'))
        }
    }
    response {
        status SERVICE_UNAVAILABLE()
        headers {
            contentType(applicationJson())
        }
        body(
            """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-I-11",
                   "errorData" : []
               }
            """
        )
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(0)
                maxOccurrence(0) } )
        }
    }
}
