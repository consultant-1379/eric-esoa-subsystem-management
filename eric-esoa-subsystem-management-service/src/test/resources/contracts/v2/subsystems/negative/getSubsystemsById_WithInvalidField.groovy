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
package contracts.v2.subsystems.negative

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
            """
            ```
            when:
                A client makes a request to get a specific subsystem in a selected field
            then:
                the subsystem is returned
            ```
        """
    )
    request {
        method 'GET'
        url("/subsystem-manager/v2/subsystems") {
            queryParameters {
                parameter 'id': 12
                parameter 'select': "ID"
                parameter 'tenantName': "master"
            }
        }
        headers {
            accept(applicationJson())
        }
    }
    response {
        status BAD_REQUEST()
        headers {
            contentType(applicationJson())
        }
        body(
                """
                {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-C-43",
                   "errorData" : ["${value(nonEmpty())}"]
                }
            """
        )
    }
    priority 5
}
