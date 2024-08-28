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
                A client makes a request to get a filtered list of subsystems
            then:
                the system responds with status SERVICE UNAVAILABLE
            ```
        """
    )
    request {
        method 'GET'
        url("/subsystem-manager/v2/subsystems") {
            queryParameters {
                parameter 'offset': value(consumer(optional(anyInteger())), producer('1'))
                parameter 'limit': value(consumer(optional(anyPositiveInt())), producer('1'))
                parameter 'sortAttr': value(consumer(optional(anyNonEmptyString())), producer('id'))
                parameter 'sortDir': value(consumer(optional(anyNonEmptyString())), producer('asc'))
                parameter 'filters': "%3D%7B%22name%22%3A%22auth4%22%7D"
            }
        }
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
                   "errorCode":"SSM-I-11"
                }
            """
        )
    }
}
