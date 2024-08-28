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
package com.ericsson.bos.so.subsystemsmanager.web.controller;

import com.ericsson.bos.so.subsystemsmanager.business.util.Constants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Interface BaseWebController.
 */
@RestController
public interface BaseWebController {

    /**
     * The Interface V1.
     */
    @RestController
    @RequestMapping(value = Constants.V1)
    interface V1{
    }

    /**
     * The Interface V2.
     */
    @RestController
    @RequestMapping(value = Constants.V2)
    interface V2{
    }

}
