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
package contracts.v2.subsystemTypes.positive

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            when:
                A client makes request to create a subsystem-type with list of subtypes.
            then:
                the subsystem-type is created.
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
                    "name": "subtype1",
                    "alias": "subtype1"
                    },{
                    "name": "subtype2",
                    "alias": "subtype2"
                    },{
                    "name": "subtype3",
                    "alias": "subtype3"
                    }]'''))}
                }
            """
        )
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body(file("responseJson/PostSubsystemTypeWithListOfSubtypesResponse.json"))
    }
    priority 5
}
