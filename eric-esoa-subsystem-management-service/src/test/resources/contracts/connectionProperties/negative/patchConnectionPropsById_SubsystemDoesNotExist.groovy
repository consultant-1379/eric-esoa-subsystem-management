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
            when:
                A client makes a request to patch a specific connection properties of a nonexistent subsystem
            then:
                the system responds with status 404 NOT_FOUND
            ```
        """
        )
    request {
        method 'PATCH'
        url "/subsystem-manager/v1/subsystems/${value(consumer('11'), producer('11'))}/connection-properties/${value(consumer(anyPositiveInt()), producer('2'))}"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "encryptedKeys": ["password"],
                    "name": "${value(consumer(optional(anyNonEmptyString())), producer('connectionNew'))}",
                    "username": "${value(consumer(optional(anyNonEmptyString())), producer('newusername'))}",
                    "password": "${value(consumer(optional(anyNonEmptyString())), producer('CloudAdmin456'))}",
                    "subsystemUsers": [{"id":5,"connectionPropsId":3},{"id":16,"connectionPropsId":3}],
                    "${value(consumer(optional(optional(anyNonEmptyString()))), producer('tenant'))}": "${value(consumer(optional(anyNonEmptyString())), producer('newtenant'))}"
                }
            """
        )
       bodyMatchers {
           jsonPath('$.encryptedKeys[*]', byRegex('[\\S\\s]+'))
           jsonPath('$.subsystemUsers[*].id', byRegex(number()))
           jsonPath('$.subsystemUsers[*].connectionPropsId', byRegex(number()))
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
        bodyMatchers {
            jsonPath('$.errorData', byType{
                minOccurrence(1)
                maxOccurrence(1) } )
        }
    }
}
