Feature: Subsystem feature

  Scenario: Welcome message returned when service contacted
    When I use the service
    Then the response will be "Welcome to Subsystem Management Service"

  #Scenario: Subsystem successfully created when valid request sent
    #Given a valid subsystem onboarding request
    #When I onboard the subsystem
    #Then a 201 statusCode is returned

  #Scenario: Subsystem is successfully created with 100 ConnectionProperties each with 10 properties
    #Given a valid subsystem with 100 ConnectionProperties
    #When I onboard the subsystem
     #Then a 201 statusCode is returned

  Scenario: Subsystem not created when onboarding request contains no properties within connection properties
    Given a subsystem onboarding request with no properties inside Connectionproperties
    When I onboard the invalid subsystem
    Then a 400 statusCode is returned

  #Scenario: Subsystem not created when onboarding request contains an encrypted key not in propertyList
    #Given a subsystem onboarding request with an encrypted key not in properties
    #When I onboard the invalid subsystem
    #Then a 400 statusCode is returned

  Scenario: Subsystem not created when onboarding request contains duplicated keys in ConnectionProperties
    Given a subsystem onboarding request with duplicated keys under one connectionProperty
    When I onboard the invalid subsystem
    Then a 400 statusCode is returned

  Scenario: Subsystem not created when onboarding request contains key containing white space in ConnectionProperties
    Given a subsystem onboarding request with a key containing space in properties
    When I onboard the invalid subsystem
    Then a 400 statusCode is returned

  Scenario: Subsystem not created when onboarding request contains key with 256 chars in ConnectionProperties
    Given a subsystem onboarding request with a key containing 256 chars in properties
    When I onboard the invalid subsystem
    Then a 400 statusCode is returned

  Scenario: Subsystem not created when onboarding request contains duplicate subsystem name
    Given a duplicate subsystem onboarding request
    When I onboard the subsystem with a duplicate subsystem name
    Then a 409 statusCode is returned
    And the expected exception is returned

  Scenario: Subsystem not created when onboarding request contains non existing subsystem type
    Given a subsystem onboarding request with an invalid subsystemType
    When I onboard the subsystem with an invalid subsystemType id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Subsystem not created when request contains a onboarding request with partial data
    Given a subsystem onboarding request with a partial onboarding request
    When I onboard the invalid subsystem request with partial info
    Then a 400 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response when a get request is sent with an invalid Subsystem Id
    When I make a get request specifying a subsystem with an invalid id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when delete request contains an invalid subsystem Id
    When I delete a subsystem that does not exists
    Then a 404 statusCode is returned
    And the expected exception is returned

  #Scenario: Subsystem successfully updated when valid patch request sent
    #When I make a patch request with a valid Subsystem id
    #Then a 200 statusCode is returned

  Scenario: Expected response returned when patch request contains an invalid subsystem Id
    When I make a patch request with an invalid Subsystem id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when patch request contains an invalid subsystemType Id
    When I make a patch request with a invalid SubsystemType id
    Then a 404 statusCode is returned
    And the expected exception is returned

  #Scenario: Subsystem successfully updated when valid put request sent
    #When I make a put request with a valid Subsystem id
    #Then a 200 statusCode is returned

  Scenario: Expected response returned when put request contains an invalid subsystem Id
    When I make a put request with a invalid Subsystem id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when put request contains an invalid subsystemType Id
    When I make a put request with a invalid SubsystemType id
    Then a 404 statusCode is returned
    And the expected exception is returned

  #Scenario: Paginated with ConnectionProperties filtered subsystems returned when service contacted
    #When I request a list of paginated subsystems with filter on ConnectionProperties
    #Then the paginated and filtered subsystems will be returned

  #Scenario: Subsystem not created when onboarding second subsystem of type NFVO
    #Given a valid subsystem onboarding request
    #When I onboard the subsystem of type nfvo
    #Then I try to onboard a second subsystem of type nfvo
    #Then a 409 statusCode is returned
    #And the expected exception is returned

  #Scenario: System will onboard multiple DomainManagers if they are from different vendors
    #Given a valid subsystem onboarding request
    #When I onboard the subsystem
    #Then I onboard a second subsystem with same type but different vendor
    #Then a 201 statusCode is returned

  #Scenario: Subsystem should be created when onboarding second subsystem of type DomainManager from same vendor
    #Given a valid subsystem onboarding request
    #When I onboard the subsystem
    #Then I onboard a second subsystem with type Domain Manager and same vendor
    #Then a 201 statusCode is returned

  Scenario: Expected response is returned when onboarding subsystem of type NFVO with no adapter link
    Given an nfvo subsystem onboarding request without adapter link
    When I onboard the nfvo type subsystem
    Then a 400 statusCode is returned

  Scenario: Expected response is returned when onboarding subsystem of type NFVO with empty adapter link
    Given an nfvo subsystem onboarding request with empty adapter link
    When I onboard the nfvo type subsystem
    Then a 400 statusCode is returned

  Scenario: Expected response is returned when onboarding subsystem of type DomainManager with adapter link
    Given a DomainManager subsystem onboarding request with adapter link
    When I onboard the DomainManager type subsystem
    Then a 400 statusCode is returned
