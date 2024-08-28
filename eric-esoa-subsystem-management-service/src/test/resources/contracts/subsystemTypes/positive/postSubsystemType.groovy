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
package contracts.subsystemTypes.positive

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            when:
                A client makes request to create a subsystem-type
            then:
                the subsystem-type is created
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
                    "type": "${value(consumer(anyNonEmptyString()), producer('SCP'))}"
                }
            """
        )
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body(file("PostSubsystemTypeResponse.json"))
    }
    priority 5
}
