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
package contracts.v2.subsystemUsers.positive

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description(
        """
            ```
            when:
                A client makes a request to create a connection properties user
            then:
                the user is created
            ```
        """
        )
    request {
        method 'POST'
        url "/subsystem-manager/v2/subsystems/${value(consumer(anyPositiveInt()), producer('10'))}/connection-properties/${value(consumer(anyPositiveInt()), producer('10'))}/subsystem-users"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body(file("PostSubsystemUserResponse.json").asString().replaceAll("%id", "${fromRequest().path(3).serverValue}").replaceAll("%pid", "${fromRequest().path(5).serverValue}"))
    }
    priority 5
}
