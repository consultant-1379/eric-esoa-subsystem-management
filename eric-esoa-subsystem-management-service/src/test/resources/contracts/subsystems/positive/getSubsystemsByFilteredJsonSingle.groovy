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
                A client makes a request for a single field of subsystems
            then:
                the list of field values is returned
            ```
        """
        )
    request {
        method 'GET'
            url ("/subsystem-manager/v1/subsystems") {
                queryParameters {
                    parameter 'select': value(consumer(anyNonEmptyString()), producer('name'))
                    parameter 'tenantName': value(consumer(optional(anyNonEmptyString())))
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
        body(file("GetSubsystemsByFilteredJsonSingleResponse.json"))
    }
    priority 5
}
