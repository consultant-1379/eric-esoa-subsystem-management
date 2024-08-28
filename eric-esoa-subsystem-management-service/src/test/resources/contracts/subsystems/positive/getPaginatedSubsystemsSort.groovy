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
                A client makes a request to get a subsystems list sorted by name
            then:
                the subsystems list sorted by name is returned
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v1/subsystems") {
            queryParameters {
                parameter 'offset': value(consumer(optional(anyInteger())), producer('0'))
                parameter 'limit': value(consumer(optional(anyPositiveInt())), producer('10'))
                parameter 'sortAttr': value(consumer('name'), producer('name'))
                parameter 'sortDir': value(consumer('asc'), producer('asc'))
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
        body(file("GetSubsystemsSortedResponse.json"))
        bodyMatchers {
            jsonPath('$.[0].name', byEquality())
            jsonPath('$.[1].name', byEquality())
            jsonPath('$.[2].name', byEquality())
            jsonPath('$.[3].name', byEquality())
            // avoid deep test on inner part of response
            jsonPath('$.[*].connectionProperties', byType())
        }
    }
}
