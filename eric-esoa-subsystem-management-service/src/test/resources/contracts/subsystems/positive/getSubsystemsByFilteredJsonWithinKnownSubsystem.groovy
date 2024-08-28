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
                A client makes a request to get a subsystem selected fields
            then:
                the selected fields of subsystems are returned
            ```
        """
        )
    request {
        method 'GET'
        url ("/subsystem-manager/v1/subsystems/${value(consumer(anyPositiveInt()), producer('10'))}") {
            queryParameters {
                parameter 'select': value(consumer(anyNonEmptyString()), producer('id,name,subsystemType'))
                parameter 'tenantName': value(consumer(optional(anyNonEmptyString())), producer('tenant1'))
            }
        }
        headers {
            accept(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("GetSubsystemsByFilteredJsonWithinKnownSubsystemResponse.json").asString().replaceAll("%id", fromRequest().path(3).serverValue.toString()))
    }
    priority 5
}
