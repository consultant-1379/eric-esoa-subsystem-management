Feature: Connection Properties feature

  #Scenario: List of a Subsystems Connection Properties returned when valid request is made
    #When I request a list of connectionProperties for a specific subsystem id
    #Then the expected connectionProperties will be returned

  Scenario: Expected response returned when get request to Connection Properties with an invalid subsystem id is made
    When I request a list of connectionProperties for an invalid subsystem id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: ConnectionProperties successfully created when valid post request is sent
    Given a post request on ConnectionProperties with a valid subsystem id
    When I perform a post request on ConnectionProperties
    Then a 201 statusCode is returned

  Scenario: Expected response returned when post request to Connection Properties with an invalid subsystem id is made
    When a post request on ConnectionProperties with an invalid subsystem id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: ConnectionProperties is not created when I send a POST request with a body with no properties defined
    Given a post request on ConnectionProperties with no properties defined
    When I perform a post request on ConnectionProperties
    Then a 400 statusCode is returned

  Scenario: ConnectionProperties is not created when I send a POST request with a body with duplicated properties keys
    Given a post request on ConnectionProperties with duplicated property keys
    When I perform a post request on ConnectionProperties
    Then a 400 statusCode is returned

  #Scenario: ConnectionProperties is not created when I send a POST request with a body with an invalid encrypted key
    #Given a post request on ConnectionProperties with an invalid encrypted key
    #When I perform a post request on ConnectionProperties
    #Then a 400 statusCode is returned

  #Scenario: Expected Connection Properties returned when a get request is made with a valid Connection Properties Id
    #When a get request on connectionProperties with a valid connection properties id
    #Then the expected connectionProperty will be returned

  Scenario: Expected Connection Properties returned when a get request is made with a invalid Connection Properties Id
    When a get request on connectionProperties with a invalid connection properties id
    Then a 404 statusCode is returned
    And the expected exception is returned

  #Scenario: ConnectionProperties successfully updated when valid put request is sent
    #Given a valid put request on ConnectionProperties
    #When I perform a put request on ConnectionProperties
    #Then a 201 statusCode is returned
    #And the ConnectionProperties is successfully updated

  Scenario: Expected response returned when a put connection properties request is made with an invalid connection properties Id
    When I update a subsystem that specifies an invalid connection properties Id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when a put connection properties request is made with an invalid subsystem Id
    When I update a subsystem that specifies an invalid subsystem Id
    Then a 404 statusCode is returned
    And the expected exception is returned

  #Scenario: ConnectionProperties successfully updated when valid patch request is sent
    #Given a valid patch request on ConnectionProperties
    #When I perform a patch request on ConnectionProperties
    #Then a 201 statusCode is returned
    #And the ConnectionProperties is successfully patched

  Scenario: Expected response returned when a patch connection properties request is made with an invalid connection properties Id
    When I partially update a subsystem that specifies an invalid connection properties Id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when a patch connection properties request is made with an invalid subsystem Id
    When I partially update a subsystem that specifies an invalid subsystem Id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when valid delete connection properties request is sent
    When I delete a connection properties that exists
    Then a 204 statusCode is returned

  Scenario: Expected response returned when delete connection properties request contains an invalid connection properties Id
    When I delete a subsystem specifying an invalid connection properties Id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when delete connection properties request contains an invalid subsystem Id
    When I delete a subsystem specifying an invalid subsystem Id
    Then a 404 statusCode is returned
    And the expected exception is returned
