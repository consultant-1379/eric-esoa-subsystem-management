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
package contracts.v2.subsystemTypes.negative

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to delete a Subsystem Subtype with a nonexistent SubsystemType Id
            then:
                the system responds with status 404 NOT_FOUND
            ```
        """
    )

    request {
        method 'DELETE'
        url "/subsystem-manager/v2/subsystem-types/${value(consumer(anyDouble()), producer(11L))}" +
                "/subtype/${value(consumer(anyDouble()), producer(1L))}"
        headers {
            contentType(applicationJson())
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
                   "userMessage": "'11' Connected System type was not found.",
                   "errorCode": "SSM-J-17",
                   "errorData": ["11"]
               }""")
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(1)
                maxOccurrence(1) } )
        }
    }
}
