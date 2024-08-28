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
               A pre-existing subsystem-type
            when:
                A client makes request to create a subsystem-type
            then:
                the subsystem-type already exists
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v1/subsystem-types"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "type": "${value(consumer(anyNonEmptyString()), producer('duplicateSubsystemType'))}"
                }
            """
        )
    }
    response {
        status CONFLICT()
        headers {
            contentType(applicationJson())
        }
        body(
            """
                {
                    "errorCode": "SSM-K-16",
                    "userMessage": "${value(nonEmpty())}",
                    "errorData": [
                        "${value(anyNonEmptyString())}"
                    ]
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
