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
package contracts.subsystemTypes.negative

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            given:
                The Credential Manager service is unavailable
            when:
                A client makes a request to get the list of all subsystem types
            then:
                the system responds with status 503 SERVICE_UNAVAILABLE
            ```
        """
        )
    request {
        method 'GET'
        url "/subsystem-manager/v1/subsystem-types"
        headers {
            accept(applicationJson())
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
    priority 5
}
