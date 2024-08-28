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
package contracts.subsystemUsers.negative

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description(
        """
            ```
            given:
                The Credential Manager service is unavailable
            when:
                A client makes a request to delete a user
            then:
                the system responds with status 503 SERVICE_UNAVAILABLE
            ```
        """
        )
    request {
        method 'DELETE'
        url "/subsystem-manager/v1/subsystems/${value(consumer('12'), producer('12'))}/connection-properties/${value(consumer(anyPositiveInt()), producer('4'))}/subsystem-users/${value(consumer(anyPositiveInt()), producer('6'))}"

        headers {
            accept(applicationJson())
        }
    }
    response {
        status SERVICE_UNAVAILABLE()
        headers {
            contentType(applicationJson())
        }
        body(
            """
               {
                   "userMessage":"${value(nonEmpty())}",
                   "errorCode":"SSM-I-11",
                   "errorData" : []
               }
            """
        )
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(0)
                maxOccurrence(0) } )
        }
    }
}
