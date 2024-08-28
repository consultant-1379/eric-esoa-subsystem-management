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
                A client makes request to create a subsystem-subtype under specific type with list of subtypes.
            then:
                the subsystem-subtype is created.
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
                    "name": "${value(consumer(anyNonEmptyString()), producer('subtype'))}",
                    "alias": "${value(consumer(anyNonEmptyString()), producer('sub'))}",
                }
            """
        )
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body(file("responseJson/PostSubsystemSubtypeResponse.json"))
    }
    priority 5
}
