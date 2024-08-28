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
package contracts.v2.subsystemTypes.negative

import org.springframework.cloud.contract.spec.Contract

import java.util.function.Consumer;

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to get a specific subsystem type
            then:
                the system responds with status 404 NOT_FOUND
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v2/subsystem-types") {
            queryParameters {
                parameter 'type': value(consumer('nfvo'))
            }
        }

        headers {
            accept(applicationJson())
        }
    }
    response {
        status NOT_FOUND()
        headers {
            contentType(applicationJson())
        }
        body("""{
            "userMessage": "'nfvo' Connected System type was not found.",
            "errorCode": "SSM-J-17",
            "errorData": [
                "nfvo"
            ]
        }""")
    }
}
