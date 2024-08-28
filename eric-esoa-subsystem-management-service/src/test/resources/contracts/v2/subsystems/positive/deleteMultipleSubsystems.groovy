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
package contracts.v2.subsystems.positive

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
            """
            ```
            when:
                A client makes a request to delete subsystems
            then:
                the subsystems are deleted
            ```
        """
    )
    request {
        method 'DELETE'
        url "/subsystem-manager/v2/subsystems"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
                """
                 "${value(consumer(regex('\\[(([1-9][0-9]*){1},?)+\\]')), producer(["10", "12", "13"]))}"
            """
        )
    }
    response {
        status NO_CONTENT()
    }
}
