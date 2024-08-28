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
            given:
                A SubsystemType with a list of Subtypes.
            when:
                A client makes a request to delete the SubsystemType with valid SubsystemType Id.
            then:
                the SubsystemType along with its associated subtypes will be deleted.
            ```
        """
    )

    request {
        method 'DELETE'
        url "/subsystem-manager/v2/subsystem-types/${value(consumer(anyDouble()), producer(1L))}"
        headers {
            accept(applicationJson())
            contentType(applicationJson())
        }
    }

    response {
        status NO_CONTENT()
    }
}
