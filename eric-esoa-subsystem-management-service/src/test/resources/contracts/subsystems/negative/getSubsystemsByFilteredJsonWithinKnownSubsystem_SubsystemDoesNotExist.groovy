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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to get the selected fields of a nonexistent subsystem
            then:
                the system responds with status 404 NOT_FOUND
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v1/subsystems/${value(consumer('11'), producer('11'))}") {
            queryParameters {
                parameter 'select': value(consumer(anyNonEmptyString()), producer('id,name,operationalState'))
                parameter 'tenantName': value(consumer(optional(anyNonEmptyString())))
            }
        }
        headers {
            accept(applicationJson())
        }
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
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(1)
                maxOccurrence(1) } )
        }
    }
}
