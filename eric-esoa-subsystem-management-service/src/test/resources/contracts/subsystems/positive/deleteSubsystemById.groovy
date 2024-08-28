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
package contracts.subsystems.positive

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to delete a subsystem
            then:
                the subsystem is deleted
            ```
        """
        )
    request {
        method 'DELETE'
        url "/subsystem-manager/v1/subsystems/${value(consumer(anyPositiveInt()), producer('10'))}"
        headers {
            accept(applicationJson())
        }
    }
    response {
        status NO_CONTENT()
    }
    priority 5
}
