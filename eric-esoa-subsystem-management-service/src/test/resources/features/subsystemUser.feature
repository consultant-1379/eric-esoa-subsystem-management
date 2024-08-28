Feature: SubsystemUser feature

  Scenario: Expected response returned when valid post request is made to subsystemUsers
    Given a valid post request on SubsystemUsers
    When I request a list of subsystemUsers for a specific connection property
    Then the subsystemUser will be assigned to the correct connectionPropertyObject

  Scenario: Expected response returned when post request is made to subsystemUsers with an invalid subsystem id
    Given a post request on SubsystemUsers with an invalid subsystem id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when post request is made to subsystemUsers with an invalid connection properties id
    Given a post request on SubsystemUsers with an invalid connection properties id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when valid delete request is made to subsystemUsers
    Given a valid delete request on SubsystemUsers
    Then a 204 statusCode is returned

  Scenario: Expected response returned when delete request is made to subsystemUsers with an invalid subsystem id
    Given a delete request on SubsystemUsers with an invalid subsystem id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when delete request is made to subsystemUsers with an invalid connection properties id
    Given a delete request on SubsystemUsers with an invalid connection properties id
    Then a 404 statusCode is returned
    And the expected exception is returned

  Scenario: Expected response returned when delete request is made to subsystemUsers with an invalid subsystem user id
    Given a delete request on SubsystemUsers with an invalid subsystem user id
    Then a 404 statusCode is returned
    And the expected exception is returned