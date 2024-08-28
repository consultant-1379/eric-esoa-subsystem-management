====
    COPYRIGHT Ericsson 2023-2024



    The copyright to the computer program(s) herein is the property of

    Ericsson Inc. The programs may be used and/or copied only with written

    permission from Ericsson Inc. or in accordance with the terms and

    conditions stipulated in the agreement/contract under which the

    program(s) have been supplied.
====

#######################################################
##  DEFAULT PROJECT FOR A SPRING BOOT BASED MICRO SERVICE  ##
#######################################################

PROJECT
---------------

The project contains a very simple, Spring Boot based, microservice example. The code is
unit tested using Spock and integration tested using Cucumber. The project is broken down
into a number of modules, each with its own concern, as outlined in the next section.

RECOMENDED PROJECT STRUCTUREs
---------------------------------------------------------------

    - WEB Module
        Contains the REST endpoint. This modules concern is handling the HTTP requests, delegating the 
        processing of the requests to the business module and generating the HTTP responses. The Unit Tests 
        are written using spock and run in a Spring Boot web context.
    
    - BUSINESS Module
        Contains the business logic. This modules concern is encoding the real-world business rules that determine 
        how data can be created, displayed, stored, and changed. It prescribes how business objects interact with one another, 
        and enforces the routes and the methods by which business objects are accessed and updated. The Unit Tests are written
         using spock and run in a Spring Boot context.
     
    - DOMAIN Module
        Contains the REST resources. Any REST resource exposed by the web module should be defined here.
        This module is shared between the WEB & BUSINESS modules.
    
    - DOCKER Module
        Builds the docker image.
    
    - INTEGRATION TEST Module
        Contains the integration tests, written using Cucumber. The integrations tests are ran against a container based on the 
        image build in the DOCKER module.
        

HOW TO BUILD
--------------------------

    Build all code, execute the unit tests, build docker image and run integration tests against a docker container:
    > mvn clean install
    
    To skip unit & integration tests:
    > mvn clean install -Dmaven.test.skip=true
    
    To skip docker image build:
    > mvn clean install  Ddocker.skip=true

