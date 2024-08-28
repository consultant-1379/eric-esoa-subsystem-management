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
            when:
                A client makes a request to delete a SubsystemType by giving an invalid SubsystemTypeId
            then:
                the system responds with status 400 BAD_REQUEST
            ```
        """
    )

    request {
        method 'DELETE'
        url "/subsystem-manager/v1/subsystem-types/${value(consumer(anyDouble()), producer(-12L))}"
        headers {
            accept(applicationJson())
            contentType(applicationJson())
        }
    }

    response {
        headers {
        status BAD_REQUEST()
            contentType(applicationJson())
        }
        body(
            """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-B-25",
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
