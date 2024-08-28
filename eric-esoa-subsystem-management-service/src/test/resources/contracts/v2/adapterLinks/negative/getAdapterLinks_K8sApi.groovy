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
package contracts.v2.adapterLinks.negative

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            given:
                Service unreachable/unavailable

            when:
                a client makes a request to get the list of all subsystem types

            then:
                the system responds with status 500 INTERNAL_SERVER_ERROR
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v2/subsystems/adapter-links") {
            queryParameters {
                parameter 'type': 'NFVO'
            }
        }
        headers {
            accept(applicationJson())
        }
    }
    response {
        // K8sClientInitializationException is managed by DaoControllerAdvice.handleAllOtherExceptions which report INTERNAL_DATABASE_ERROR
        status INTERNAL_SERVER_ERROR()
        headers {
            contentType(applicationJson())
        }
        body(
            """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-F-39",
                   "errorData" : ["${value(nonEmpty())}"]
               }
            """
        )
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(2)
                maxOccurrence(2) } )
        }
    }
}
