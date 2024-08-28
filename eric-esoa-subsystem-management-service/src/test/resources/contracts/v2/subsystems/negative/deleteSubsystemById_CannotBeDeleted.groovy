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
                A client makes a request to delete a specific subsystem used by active service
            then:
                the system responds with status 404 NOT FOUND
            ```
        """
    )
    request {
        method 'DELETE'
        url "/subsystem-manager/v2/subsystems/${value(consumer('13'), producer('13'))}"
        headers {
            accept(applicationJson())
        }
    }
    response {
        status NOT_FOUND()
        headers {
            contentType(applicationJson())
        }
        body(
                """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-J-13",
                   "errorData" : ["${value(nonEmpty())}"]
               }
            """
        )
    }
}
