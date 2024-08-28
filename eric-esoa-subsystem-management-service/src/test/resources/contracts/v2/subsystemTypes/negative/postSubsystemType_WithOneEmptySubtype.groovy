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
                A client makes request to create a subsystem-type with list of subtypes with one subtype with empty name.
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
                    "name": "SubType",
                    "alias": "Sub"
                    },{
                    "name": "",
                    "alias": ""
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
            "userMessage": "The Subsystem subtype 'name' field is mandatory.",
            "errorCode": "SSM-B-48",
            "errorData": ["name"]
        }''')
    }
    priority 5
}
