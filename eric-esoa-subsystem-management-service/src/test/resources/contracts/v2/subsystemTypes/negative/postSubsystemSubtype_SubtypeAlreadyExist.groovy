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
               A pre-existing subsystem sub-type
            when:
                A client makes request to create a subsystem sub-type
            then:
                the sub-type already exists
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v2/subsystem-types/${value(consumer(anyNonBlankString()),producer('1'))}/subtype"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
                """
                {
                    "name": "${value(consumer(anyNonEmptyString()), producer('duplicateSubsystemType'))}",
                    "alias": "${value(consumer(anyNonEmptyString()), producer('subtype'))}",
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
                    "errorCode": "SSM-K-46",
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
