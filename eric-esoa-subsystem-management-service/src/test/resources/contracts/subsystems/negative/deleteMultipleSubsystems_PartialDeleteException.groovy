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
                A client makes a request to delete some subsystems with one or more nonexistent
            then:
                the system responds with status 500 INTERNAL_SERVER_ERROR
            ```
        """
        )
    request {
        method 'DELETE'
        url "/subsystem-manager/v1/subsystems"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                ["15"]
            """
        )
    }
    response {
        status INTERNAL_SERVER_ERROR()
        headers {
            contentType(applicationJson())
        }
        body(
            """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-E-15",
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
    priority 5
}
