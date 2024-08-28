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
                A client makes a request to get the connection properties of a subsystem
            then:
                the connection properties list is returned
            ```
        """
        )
    request {
        method 'GET'
        url "/subsystem-manager/v1/subsystems/${value(consumer(anyPositiveInt()), producer('10'))}/connection-properties"
        headers {
            accept(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("GetConnectionPropsResponse.json").asString().replaceAll("%id", fromRequest().path(3).serverValue.toString()))
        bodyMatchers {
            // avoid deep test on inner part of response
            jsonPath('$.[*].encryptedKeys', byType())
            jsonPath('$.[*].subsystemUsers', byType())
            jsonPath('$.[*].scriptingVMs', byEquality())
            jsonPath('$.[*].sftpPort', byEquality())
        }
    }
    priority 5
}
