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
package contracts.connectionProperties.negative

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description(
        """
            ```
            given:
                The Credential Manager service is unavailable
            when:
                A client makes a request to modify a specific connection properties in a specific subsystem
            then:
                the system responds with status 503 SERVICE_UNAVAILABLE
            ```
        """
        )
    request {
        method 'PUT'
        url "/subsystem-manager/v1/subsystems/${value(consumer('12'), producer('12'))}/connection-properties/${value(consumer(anyPositiveInt()), producer('2'))}"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "encryptedKeys":["password"],
                    "name": "${value(consumer(anyNonEmptyString()), producer('connectionNew'))}",
                    "username": "${value(consumer(anyNonEmptyString()), producer('newusername'))}",
                    "password": "${value(consumer(anyNonEmptyString()), producer('CloudAdmin456'))}",
                    "subsystemUsers":[{"id":5,"connectionPropsId":3},{"id":16,"connectionPropsId":3}],
                    "tenant": "${value(consumer(optional(anyNonEmptyString())), producer('newtenant'))}"
                }
             """
        )
        bodyMatchers {
            jsonPath('$.encryptedKeys', byRegex('[\\S\\s]+'))
            jsonPath('$.subsystemUsers[*].id', byRegex(number()))
            jsonPath('$.subsystemUsers[*].connectionPropsId', byRegex(number()))
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
