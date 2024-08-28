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
package contracts.connectionProperties.positive

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to add a connection properties definition to a specific subsystem
            then:
                the connection properties is added
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v1/subsystems/${value(consumer(anyPositiveInt()), producer('10'))}/connection-properties"

        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "encryptedKeys":["password"],
                    "name": "${value(consumer(anyNonEmptyString()), producer('connection2'))}",
                    "username": "${value(consumer(anyNonEmptyString()), producer('ecmadmin'))}",
                    "password": "${value(consumer(anyNonEmptyString()), producer('CloudAdmin123'))}",
                    "subsystemUsers": [{"id":5,"connectionPropsId":3},{"id":16,"connectionPropsId":3}],
                    "${value(consumer(optional(anyNonEmptyString())), producer('tenant'))}": "${value(consumer(optional(anyNonEmptyString())), producer('master'))}"
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
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body(file("PostConnectionPropsResponse.json").asString().replaceAll("%id", fromRequest().path(3).serverValue.toString()))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.encryptedKeys', byType())
            jsonPath('$.subsystemUsers', byType())
        }
    }
    priority 5
}
