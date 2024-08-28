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
                A client makes a request to get a filtered list of subsystems
            then:
                the subsystems filtered list is returned
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v1/subsystems") {
            queryParameters {
                parameter 'offset': value(consumer(optional(anyInteger())), producer(''))
                parameter 'limit': value(consumer(optional(anyPositiveInt())), producer(''))
                parameter 'sortAttr': value(consumer(optional(anyNonEmptyString())), producer(''))
                parameter 'sortDir': value(consumer(optional(anyNonEmptyString())), producer(''))
                parameter 'filters': value(consumer("[\"operationalState\":\"UNREACHABLE\"]"), producer("[\"operationalState\":\"UNREACHABLE\"]"))
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
        body(file("GetSubsystemsFilteredResponse.json"))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.[*].connectionProperties', byType())
        }
    }
}
