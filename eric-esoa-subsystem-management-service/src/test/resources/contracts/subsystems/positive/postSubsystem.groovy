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

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to create a new subsystem
            then:
                the subsystem is created
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v1/subsystems"

        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body(
             """
                {
                    "subsystemTypeId": "${value(consumer(anyPositiveInt()), producer('5'))}",
                    "name": "${value(consumer(anyNonEmptyString()), producer('subsystem-1'))}",
                    "url": "${value(consumer(anyUrl()), producer('https://subsystem-1/'))}",
                    "connectionProperties": [{
                        "encryptedKeys":["password"],
                        "name": "connection1",
                        "username": "ecmadmin",
                        "password": "CloudAdmin123",
                        "subsystemUsers": [{"id":5,"connectionPropsId":3},{"id":16,"connectionPropsId":3}]
                    }],
                    "vendor": "${value(consumer(anyNonEmptyString()), producer('subsystem-vendor'))}",
                    "adapterLink": "${value(consumer(anyNonEmptyString()), producer('eric-eo-ecm-adapter'))}"
                }
             """
        )
        bodyMatchers {
            // avoid deep test on inner part
            jsonPath('$.connectionProperties', byRegex('[\\S\\s]+'))
        }
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body(file("PostSubsystemResponse.json"))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.connectionProperties', byType())
            jsonPath('$.subsystemType', byType())
        }
    }
    priority 5
}
