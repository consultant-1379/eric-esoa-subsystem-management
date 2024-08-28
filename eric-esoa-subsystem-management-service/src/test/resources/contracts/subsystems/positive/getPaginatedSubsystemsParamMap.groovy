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
                A client makes a request to get a subsystems list filtered by param Map
            then:
                the subsystems filtered list is returned
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v1/subsystems") {
            queryParameters {
                parameter 'vendor' : value(consumer("Ericsson"), producer("Ericsson"))
            }
        }
        headers {
            accept(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("GetSubsystemsParamMapResponse.json").asString())
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.[*].connectionProperties', byType())
        }
    }
}
