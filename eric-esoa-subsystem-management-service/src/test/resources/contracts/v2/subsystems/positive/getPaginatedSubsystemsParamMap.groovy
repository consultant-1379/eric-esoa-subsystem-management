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
                A client makes a request to get a subsystems list filtered by param Map
            then:
                the subsystems filtered list is returned
            ```
        """
    )
    request {
        method 'GET'
        url("/subsystem-manager/v2/subsystems") {
            queryParameters {
                parameter 'name': value(consumer("Ericsson"), producer("ecm"))
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
        body(file("responseJson/GetSubsystemsResponse.json").asString())

    }
}
