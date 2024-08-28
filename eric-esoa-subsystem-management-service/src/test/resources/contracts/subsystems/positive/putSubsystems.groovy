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
                A client makes a request to modify a specific subsystem
            then:
                the modified subsystem is returned
            ```
        """
        )
    request {
        method 'PUT'
        url "/subsystem-manager/v1/subsystems/${value(consumer(anyPositiveInt()), producer('10'))}"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
            """
                {
                    "name": "${value(consumer(anyNonEmptyString()), producer('newname'))}",
                    "url": "${value(consumer(anyUrl()), producer('https://test.newurl/'))}",
                    "vendor": "${value(consumer(anyNonEmptyString()), producer('new-vendor'))}",
                    "connectionProperties": [{
                        "encryptedKeys":["password"],
                        "name": "connection1",
                        "username": "ecmadmin",
                        "password": "CloudAdmin123",
                        "subsystemUsers": [{"id":5,"connectionPropsId":3},{"id":16,"connectionPropsId":3}]
                    }],
                    "subsystemTypeId": "${value(consumer(anyPositiveInt()), producer('5'))}",
                    "adapterLink": "${value(consumer(anyNonEmptyString()), producer('new-adapter'))}",
                }
            """
            )
       bodyMatchers {
           // avoid deep test on inner part
           jsonPath('$.connectionProperties', byRegex('[\\S\\s]+'))
       }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("PutSubsystemResponse.json").asString().replaceAll("%id", fromRequest().path(3).serverValue.toString()))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.connectionProperties', byType())
            jsonPath('$.subsystemType', byType())
        }
    }
    priority 5
}
