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
package com.ericsson.bos.so.subsystemsmanager.test.contracts.v2

import com.ericsson.bos.so.subsystemsmanager.business.api.AdaptersLinksService
import com.ericsson.bos.so.subsystemsmanager.business.api.ConnectionPropsService
import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemUserService
import com.ericsson.bos.so.subsystemsmanager.business.api.SubsystemsService
import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemTypeServiceV2
import com.ericsson.bos.so.subsystemsmanager.business.api.v2.SubsystemsServiceV2
import com.ericsson.bos.so.subsystemsmanager.business.exception.controlleradvice.DaoControllerAdvice
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.v2.SubsystemFilterServiceV2
import com.ericsson.bos.so.subsystemsmanager.web.SubsystemManagerApplication
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.ConnectionPropertiesController
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.SubsystemController
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.SubsystemTypeController
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.SubsystemUserController
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.AdapterLinksControllerV2
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.ConnectionPropertiesControllerV2
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.SubsystemControllerV2
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.SubsystemTypeControllerV2
import com.ericsson.bos.so.subsystemsmanager.web.controller.v2.SubsystemUserControllerV2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@SpringBootTest(classes = SubsystemManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("contract-test")
@Unroll
@Import([SubsystemManagerServiceMock.class])
class ContractsBase extends Specification {

    // index values used for triggering errors in contract test
    final String NOT_FOUND_ID = '11'
    final String SERVICE_UNAVAILABLE_ID = '12'
    final String NAME_MUST_BE_UNIQUE_ID = '13'
    final String PARTIAL_DELETE_ID = '15'

    // values used for triggering errors in contract test
    final String TENANT_MAPPING_DOES_NOT_EXIST = 'wrongTenant'
    final String NAME_MUST_BE_UNIQUE = 'duplicate'
    final String SUBSYSTEM_TYPE_NOT_FOUND = 'wrongType'
    final String SUBSYSTEM_TYPE_API_PROBLEM = 'NFVO'
    final String SUBSYSTEM_TYPE_ALREADY_EXIST = 'duplicateSubsystemType'
    final String SUBSYSTEM_SUB_TYPE_ALREADY_EXIST = 'duplicateSubType'
    final String INVALID_FIELD_NAME = 'ID'

    final String BASE_DIR = "/contracts/v2"

    @Autowired
    AdapterLinksControllerV2 adapterLinksControllerV2

    @Autowired
    SubsystemUserControllerV2 subsystemUserControllerV2

    @Autowired
    ConnectionPropertiesControllerV2 connectionPropertiesControllerV2

    @Autowired
    ConnectionPropertiesController connectionPropertiesController

    @Autowired
    SubsystemController subsystemController

    @Autowired
    SubsystemTypeController subsystemTypeController

    @Autowired
    SubsystemTypeControllerV2 subsystemTypeControllerV2

    @Autowired
    SubsystemControllerV2 subsystemControllerV2

    @Autowired
    SubsystemUserController subsystemUserController

    @Autowired
    AdaptersLinksService adaptersLinksService

    @Autowired
    ConnectionPropsService connectionPropsService

    @Autowired
    SubsystemsService subsystemsService

    @Autowired
    SubsystemsServiceV2 subsystemsServiceV2

    @Autowired
    SubsystemFilterServiceV2 subsystemJsonFilterServiceV2

    @Autowired
    SubsystemTypeServiceV2 subsystemTypeServiceV2

    @Autowired
    SubsystemUserService subsystemUserService

    @Autowired
    DaoControllerAdvice daoControllerAdvice

    @EnableWebMvc
    @TestConfiguration
    static class SubsystemManagerServiceMock {

        // mock services
        def factory = new DetachedMockFactory()

        @Bean
        SubsystemsService subsystemsService() {
            factory.Mock(SubsystemsService)
        }

        @Bean
        SubsystemsServiceV2 subsystemsServiceV2() {
            factory.Mock(SubsystemsServiceV2)
        }

        @Bean
        SubsystemTypeServiceV2 subsystemTypeServiceV2() {
            factory.Mock(SubsystemTypeServiceV2)
        }

        @Bean
        SubsystemFilterServiceV2 subsystemJsonFilterServiceV2() {
            factory.Mock(SubsystemFilterServiceV2)
        }
    }

    def prepareMockResponseFromFile(final String fileName, final String... substitutions) {
        final File bodyFile = new File(getClass().getResource(fileName).toURI())
        try {
            return String.format(bodyFile.text, substitutions)
        } catch (IllegalArgumentException ignored) {
            return ""
        }
    }
}