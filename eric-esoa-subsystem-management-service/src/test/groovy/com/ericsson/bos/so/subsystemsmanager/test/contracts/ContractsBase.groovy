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
package com.ericsson.bos.so.subsystemsmanager.test.contracts

import com.ericsson.bos.so.subsystemsmanager.business.*
import com.ericsson.bos.so.subsystemsmanager.business.api.*
import com.ericsson.bos.so.subsystemsmanager.business.exception.controlleradvice.DaoControllerAdvice
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterService
import com.ericsson.bos.so.subsystemsmanager.business.fieldfilter.SubsystemFilterServiceImpl
import com.ericsson.bos.so.subsystemsmanager.web.SubsystemManagerApplication
import com.ericsson.bos.so.subsystemsmanager.web.controller.v1.*
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

    final String BASE_DIR = "/contracts"

    @Autowired
    AdapterLinksController adapterLinksController

    @Autowired
    ConnectionPropertiesController connectionPropertiesController

    @Autowired
    SubsystemController subsystemController

    @Autowired
    SubsystemTypeController subsystemTypeController

    @Autowired
    SubsystemUserController subsystemUserController

    @Autowired
    AdaptersLinksService adaptersLinksService

    @Autowired
    ConnectionPropsService connectionPropsService

    @Autowired
    SubsystemsService subsystemsService

    @Autowired
    SubsystemFilterService subsystemJsonFilterService

    @Autowired
    SubsystemTypeService subsystemTypeService

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
        AdapterLinksServiceImpl adaptersLinksService() {
            factory.Mock(AdapterLinksServiceImpl)
        }
        @Bean
        ConnectionPropsServiceImpl connectionPropsService() {
            factory.Mock(ConnectionPropsServiceImpl)
        }
        @Bean
        SubsystemsServiceImpl subsystemsService() {
            factory.Mock(SubsystemsServiceImpl)
        }
        @Bean
        SubsystemFilterServiceImpl subsystemJsonFilterService() {
            factory.Mock(SubsystemFilterServiceImpl)
        }
        @Bean
        SubsystemTypeServiceImpl subsystemTypeService() {
            factory.Mock(SubsystemTypeServiceImpl)
        }
        @Bean
        SubsystemUserServiceImpl subsystemUserService() {
            factory.Mock(SubsystemUserServiceImpl)
        }

    }

}

