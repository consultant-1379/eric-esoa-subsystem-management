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
                A client makes a request to get the list of all subsystems
            then:
                the subsystems list is returned
            ```
        """
        )
    request {
        method 'GET'
        url "/subsystem-manager/v1/subsystems"
        headers {
            accept(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("GetSubsystemsResponse.json"))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.[*].connectionProperties', byType())
        }
    }
    priority 10001
}
