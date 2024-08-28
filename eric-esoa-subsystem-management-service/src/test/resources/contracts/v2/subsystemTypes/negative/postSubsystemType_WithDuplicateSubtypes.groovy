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
                A client makes request to create a subsystem-type with list of subtypes with duplicated subtype names.
            then:
                Return Malformed Service response.
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v2/subsystem-types"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "type": "${value(consumer(anyNonEmptyString()), producer('new-subsystemType'))}",
                    "alias": "${value(consumer(anyNonEmptyString()), producer('new'))}",
                    "subtypes": ${value(consumer(any()),producer('''[{
                    "name": "duplicateSubType",
                    "alias": "duplicateSubType1"
                    },{
                    "name": "duplicateSubType",
                    "alias": "duplicateSubType2"
                    }]'''))}
                }
            """
        )
    }
    response {
        status BAD_REQUEST()
        headers {
            contentType(applicationJson())
        }
        body('''{
            "userMessage": "The Subsystem subtype name 'duplicateSubType' must be unique.",
            "errorCode": "SSM-B-49",
            "errorData": ["duplicateSubType"]
        }''')
    }
    priority 5
}
