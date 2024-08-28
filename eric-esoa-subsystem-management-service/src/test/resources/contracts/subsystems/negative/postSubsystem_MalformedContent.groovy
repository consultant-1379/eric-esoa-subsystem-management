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
            when:
                A client makes a request to create a subsystem omitting mandatory parameters
            then:
                the system responds with status 400 BAD_REQUEST
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
                    "name": "${value(consumer(anyNonEmptyString()), producer('subsystem-1'))}",
                    "url": "${value(consumer(anyUrl()), producer('https://subsystem-1/'))}",
                    "connectionProperties": [],
                    "vendor": "${value(consumer(anyNonEmptyString()), producer('subsystem-vendor'))}",
                    "adapterLink": "${value(consumer(anyNonEmptyString()), producer('eric-eo-ecm-adapter'))}"
                }
            """
        )
    }
    response {
        status BAD_REQUEST()
        headers {
            contentType(applicationJson())
        }
        body(
            """
                {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-B-25",
                   "errorData" : [""]
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
