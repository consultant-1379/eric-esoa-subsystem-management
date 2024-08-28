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
package contracts.adapterLinks.positive

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to get the list of adapter links for a particular subsystem types
            then:
                the subsystem types list is returned
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v1/subsystems/adapter-links") {
            queryParameters {
                parameter 'type': value(consumer(optional(anyNonEmptyString())), producer('NFVO'))
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
        body(file("GetAdapterLinks.json"))
    }
    priority 5
}
