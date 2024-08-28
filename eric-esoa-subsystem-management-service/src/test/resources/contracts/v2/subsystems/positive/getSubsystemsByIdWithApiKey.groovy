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
                A client makes a request to get a specific subsystem in a specific tenant
            then:
                the subsystem is returned
            ```
        """
    )
    request {
        method 'GET'
        url("/subsystem-manager/v2/subsystems/${value(consumer(anyPositiveInt()), producer('708'))}") {
            queryParameters {
                parameter 'apiKey': value(consumer(optional(anyNonEmptyString())), producer('0500c886-2b71-4d7d-be89-16fe7944cc67'))
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
        body(file("responseJson/SubsystemResponse.json").asString().replaceAll("%id", fromRequest().path(3).serverValue.toString()).replaceAll("%te", fromRequest().query('tenantName').serverValue.toString()))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.connectionProperties', byType())
        }
    }
    priority 5
}
