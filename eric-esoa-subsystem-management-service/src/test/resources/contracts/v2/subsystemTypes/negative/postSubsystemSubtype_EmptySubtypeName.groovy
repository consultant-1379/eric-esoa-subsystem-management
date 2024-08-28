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
            given:
                An empty subsystem sub-type request
            when:
                A client makes request to create a sub-type in a particular subsystem with Empty Sub-type Name
            then:
                Return Bad Request response
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v2/subsystem-types/${value(consumer(anyDouble()), producer(1L))}" +
                "/subtype"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "name": "${value(consumer(anyNonEmptyString()), producer(''))}",
                    "alias": "emptyName"
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
                    "userMessage": "The Subsystem subtype 'name' field is mandatory.",
                    "errorCode": "SSM-B-48",
                    "errorData": ["name"]
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
