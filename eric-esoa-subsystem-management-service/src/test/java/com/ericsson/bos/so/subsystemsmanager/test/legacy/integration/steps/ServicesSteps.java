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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.steps;

import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.DOMAIN_MANAGER_2;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.INVALID_SUBSYSTEM_ID;
import static com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.setup.Constants.STRING_WITH_256_CHARS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.ericsson.bos.so.subsystemsmanager.business.kms.KmsServiceImpl;
import com.ericsson.bos.so.subsystemsmanager.business.repositories.SubsystemRepository;
import com.ericsson.bos.so.subsystemsmanager.test.integration.setup.entities.PropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.CucumberRoot;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.Constants;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.ExpectedResponses;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.integration.mockservers.expecteddata.SupportedRequests;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemType;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.SubsystemUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * The Class ServicesSteps.
 *
 * @deprecated Don't use Cucumber for future tests. This structure/class is no longer maintained.
 */
@Slf4j
public class ServicesSteps extends CucumberRoot {

    public static Subsystem savedSubsystem = new Subsystem();

    public static Subsystem savedSubsystemWithNoSubsystemUsers = new Subsystem();

    private static final String PAGINATION_URL =
            "?offset=0&limit=100&sortAttr=name&sortDir=asc&filters%3D%7B%22connectionProperties.name%22%3A%22test-name%22%7D";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubsystemRepository subsystemRepository;

    @Autowired
    private KmsServiceImpl kmsService;

    private final ObjectMapper mapper = new ObjectMapper();

    private Subsystem subsystemOnboardingRequest;

    private Subsystem invalidSubsystemOnboardingRequest;

    private ResponseEntity<Object> responseEntity;

    private final Map<String, String> patchField = new HashMap<>();

    // Response
    private String greeting;

    private String putConnPropsresponse;

    private Long subsystemUserId;

    private List<Subsystem> actualGetSubsystemsResponse;

    private List<Subsystem> actualGetSubsystemsResponseList;

    private List<ConnectionProperties> actualGetConnPropsResponse;

    private List<ConnectionProperties> actualGetConnPropsResponseList;

    private final HttpHeaders reqHeaders = new HttpHeaders();

    private List<MediaType> acceptTypes = new ArrayList();

    private ConnectionProperties connectionProperties;

    private ConnectionProperties actualConnPropsPutResponse;

    private SubsystemUser actualSubsystemUser;

    private String expectedExceptionMsg;

    private String actualExceptionMsg;

    private List<Subsystem> paginatedSubsystemList;

    private List<Subsystem> actualPaginatedSubsystemList;

    private List<Object> idList;

    private HttpHeaders header;

    /**
     * Init.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    @PostConstruct
    public void init() throws IOException, ParseException {
        log.info("--> In PostConstruct..");
        acceptTypes = new ArrayList<>();
        acceptTypes.add(MediaType.APPLICATION_JSON);
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.setAccept(acceptTypes);

        log.info("--> Starting server");
        getEsoSecurityServer().startServer();
        log.info("--> After starting server");
    }

    /**
     * Destroy.
     */
    @PreDestroy
    public void destroy() {
        log.warn("--> In PreDestroy...");
    }

    /**
     * Inits the database.
     */
    @Before
    public void initDb() {
        log.info("--> initDb..");
        buildAndPersistSubsystemEntity(Constants.SUBSYSTEM_NAME_ECM);
        log.info("--> Done intiDb.");
    }

    /**
     * Teardown.
     */
    @After
    public void teardown() {
        log.info("--> Deleting records...");
        subsystemRepository.deleteAll();
        log.info("--> Deleted records.");
    }

    /**
     * I use the service.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^.* use the service$")
    public void i_use_the_service() throws Throwable {
        greeting = getTemplate().getForEntity(getServiceUrl(), String.class).getBody();
    }

    /**
     * The response will be.
     *
     * @param expectedGreeting
     *            the expected greeting
     * @throws Throwable
     *             the throwable
     */
    @Then("^the response will be \"([^\"]*)\"$")
    public void the_response_will_be(final String expectedGreeting) throws Throwable {
        assertEquals(greeting, expectedGreeting);
    }

    /**
     * I request a list of subsystems.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I request a list of subsystems$")
    public void i_request_a_list_of_subsystems() throws Throwable {
        final String getSubsystemsresponse = this.getTemplate()
                .getForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, String.class).getBody();
        actualGetSubsystemsResponse = objectMapper.readValue(getSubsystemsresponse, ArrayList.class);
        actualGetSubsystemsResponseList = mapper.convertValue(actualGetSubsystemsResponse,
                new TypeReference<List<Subsystem>>() {});
    }

    /**
     * The subsystems will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the subsystems will be returned$")
    public void the_subsystems_will_be_returned() throws Throwable {
        ResponseValidator.assertExpected(ExpectedResponses.getAllSubsystems(), actualGetSubsystemsResponseList);
    }

    /**
     * A valid subsystem onboarding request.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a valid subsystem onboarding request$")
    public void a_valid_subsystem_onboarding_request() throws Throwable {
        subsystemRepository.deleteAll();
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequest(Constants.SUBSYSTEM_NAME_ECM);
    }

    /**
     * I onboard the subsystem.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the subsystem$")
    public void i_onboard_the_subsystem() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * I onboard the subsystem with a duplicate subsystem name.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the subsystem with a duplicate subsystem name$")
    public void i_onboard_the_subsystem_with_a_duplicate_subsystem_name() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.NAME_MUST_BE_UNIQUE;
    }

    /**
     * I onboard the subsystem with an invalid subsystem type.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the subsystem with an invalid subsystemType id$")
    public void i_onboard_the_subsystem_with_an_invalid_subsystemType() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH,
                invalidSubsystemOnboardingRequest, Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_TYPE_DOES_NOT_EXIST;
    }

    /**
     * A subsystem onboarding request with a key containing space in properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a subsystem onboarding request with a key containing space in properties$")
    public void a_subsystem_onboarding_request_with_a_key_containing_space_in_properties() throws Throwable {
        invalidSubsystemOnboardingRequest = SupportedRequests
                .getSubsystemOnboardingRequest("Invalid property key with space");
        invalidSubsystemOnboardingRequest.setConnectionProperties(SupportedRequests.getInValidConnectionProperties("key with space"));
    }

    /**
     * A subsystem onboarding request with a key containing 256 chars in properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a subsystem onboarding request with a key containing 256 chars in properties$")
    public void a_subsystem_onboarding_request_with_a_key_containing_256_chars_in_properties() throws Throwable {
        invalidSubsystemOnboardingRequest = SupportedRequests
                .getSubsystemOnboardingRequest("Invalid property key with more than 256 chars");
        invalidSubsystemOnboardingRequest.setConnectionProperties(SupportedRequests.getInValidConnectionProperties(STRING_WITH_256_CHARS));
    }

    /**
     * An onboarded subsystem of type domain manager with adapter link.
     */
    @Given("^a DomainManager subsystem onboarding request with adapter link$")
    public void anOnboardedSubsystemOfTypeDomainManagerWithAdapterLink() {
        subsystemRepository.deleteAll();
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequest(Constants.SUBSYSTEM_NAME_ECM);
        subsystemOnboardingRequest.setAdapterLink("test-adapter-link");
    }

    /**
     * I onboard the domain manager type subsystem.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the DomainManager type subsystem")
    public void i_onboard_the_domain_manager_type_subsystem() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.MALFORMED_CONTENT;
    }

    /**
     * An onboarded subsystem of type NFVO without adapter link.
     */
    @Given("^an nfvo subsystem onboarding request without adapter link$")
    public void anOnboardedSubsystemOfTypeNFVOWithoutAdapterLink() {
        subsystemRepository.deleteAll();
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequestNFVO("NfvoSubsystem");
        subsystemOnboardingRequest.setAdapterLink(null);
    }

    /**
     * An onboarded subsystem of type NFVO with empty adapter link.
     */
    @Given("^an nfvo subsystem onboarding request with empty adapter link$")
    public void anOnboardedSubsystemOfTypeNFVOWithEmptyAdapterLink() {
        subsystemRepository.deleteAll();
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequestNFVO("NfvoSubsystem");
        subsystemOnboardingRequest.setAdapterLink(" ");
    }

    /**
     * I onboard the nfvo type subsystem.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the nfvo type subsystem$")
    public void i_onboard_the_nfvo_type_subsystem() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.MALFORMED_CONTENT;
    }

    /**
     * A response is returned.
     *
     * @param statusCode
     *            the status code
     * @throws Throwable
     *             the throwable
     */
    @Then("^a (\\d+) statusCode is returned$")
    public void a_response_is_returned(final int statusCode) throws Throwable {
        ResponseValidator.assertExpected(HttpStatus.valueOf(statusCode), responseEntity.getStatusCode());
    }

    /**
     * A duplicate subsystem onboarding request.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a duplicate subsystem onboarding request$")
    public void a_duplicate_subsystem_onboarding_request() throws Throwable {
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequestNFVO(Constants.SUBSYSTEM_NAME_ECM);
    }

    /**
     * A subsystem onboarding request with an invalid subsystem type.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a subsystem onboarding request with an invalid subsystemType$")
    public void a_subsystem_onboarding_request_with_an_invalid_subsystemType() throws Throwable {
        invalidSubsystemOnboardingRequest = SupportedRequests
                .getSubsystemOnboardingRequestWithInvalidSubsystemType("Invalid SubsystemType");
        invalidSubsystemOnboardingRequest.setSubsystemTypeId(Constants.INVALID_SUBSYSTEM_TYPE_ID);
    }

    /**
     * I onboard the invalid subsystem.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the invalid subsystem$")
    public void i_onboard_the_invalid_subsystem() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH,
                invalidSubsystemOnboardingRequest, Object.class);
    }

    /**
     * A subsystem onboarding request with a partial onboarding request.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a subsystem onboarding request with a partial onboarding request$")
    public void a_subsystem_onboarding_request_with_a_partial_onboarding_request() throws Throwable {
        invalidSubsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequestWithPartialFields();
    }

    /**
     * I onboard the invalid subsystem request with partial info.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the invalid subsystem request with partial info$")
    public void i_onboard_the_invalid_subsystem_request_with_partial_info() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH,
                invalidSubsystemOnboardingRequest, Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.MALFORMED_CONTENT;
    }

    /**
     * I make a get request specifying a subsystem with an invalid id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a get request specifying a subsystem with an invalid id$")
    public void i_make_a_get_request_specifying_a_subsystem_with_an_invalid_id() throws Throwable {
        responseEntity = this.getTemplate().getForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + INVALID_SUBSYSTEM_ID,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * I delete a subsystem that exists.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I delete a subsystem that exists$")
    public void i_delete_a_subsystem_that_exists() throws Throwable {
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  +
                savedSubsystemWithNoSubsystemUsers.getId(),
                HttpMethod.DELETE, HttpEntity.EMPTY, Object.class);
    }

    /**
     * I delete a subsystem that does not exists.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I delete a subsystem that does not exists$")
    public void i_delete_a_subsystem_that_does_not_exists() throws Throwable {
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + INVALID_SUBSYSTEM_ID,
                HttpMethod.DELETE, HttpEntity.EMPTY, Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * I patch a subsystem for a subsystem that exists.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a patch request with a valid Subsystem id$")
    public void i_patch_a_Subsystem_for_a_subsystem_that_exists() throws Throwable {
        final JSONObject updateBody = new JSONObject();
        updateBody.put("name", "patched-field");
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + savedSubsystem.getId(),
                HttpMethod.PATCH, new HttpEntity(updateBody.toString(), reqHeaders), Object.class);
    }

    /**
     * I patch a subsystem with an invalid id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a patch request with an invalid Subsystem id$")
    public void i_patch_a_Subsystem_with_an_invalid_id() throws Throwable {
        final JSONObject updateBody = new JSONObject();
        updateBody.put("name", "patched-field");
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + INVALID_SUBSYSTEM_ID,
                HttpMethod.PATCH, new HttpEntity(updateBody.toString(), reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * I patch a subsystem with an subsystem type id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a patch request with a invalid SubsystemType id$")
    public void i_patch_a_Subsystem_with_an_subsystemType_id() throws Throwable {
        final JSONObject updateBody = new JSONObject();
        updateBody.put("subsystemTypeId", "9999");
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + savedSubsystem.getId(),
                HttpMethod.PATCH, new HttpEntity(updateBody.toString(), reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_TYPE_DOES_NOT_EXIST;
    }

    /**
     * I make a put request with a valid a subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a put request with a valid Subsystem id$")
    public void i_make_a_put_request_with_a_valid_a_Subsystem_id() throws Throwable {
        final Subsystem updatedSubsystem = savedSubsystem;
        updatedSubsystem.setName("valid-put-request");
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + savedSubsystem.getId(),
                HttpMethod.PUT, new HttpEntity(updatedSubsystem, reqHeaders), Object.class);
    }

    /**
     * I make a put request with a invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a put request with a invalid Subsystem id$")
    public void i_make_a_put_request_with_a_invalid_Subsystem_id() throws Throwable {
        final Subsystem updatedSubsystem = savedSubsystem;
        updatedSubsystem.setName("valid-put-request");
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + INVALID_SUBSYSTEM_ID,
                HttpMethod.PUT, new HttpEntity(updatedSubsystem, reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * I make a put request with an invalid a subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a put request with a invalid SubsystemType id$")
    public void i_make_a_put_request_with_an_invalid_a_Subsystem_id() throws Throwable {
        final Subsystem updatedSubsystem = savedSubsystem;
        updatedSubsystem.setSubsystemTypeId(Constants.INVALID_SUBSYSTEM_TYPE_ID);
        responseEntity = this.getTemplate().exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + savedSubsystem.getId(),
                HttpMethod.PUT, new HttpEntity(updatedSubsystem, reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_TYPE_DOES_NOT_EXIST;
    }

    /**
     * I request a list of connection properties for a specific subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I request a list of connectionProperties for a specific subsystem id$")
    public void i_request_a_list_of_connectionProperties_for_a_specific_subsystem_id() throws Throwable {
        final String getConnPropsresponse = this.getTemplate()
                .exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH,
                        HttpMethod.GET, new HttpEntity(reqHeaders), String.class)
                .getBody();
        actualGetConnPropsResponse = objectMapper.readValue(getConnPropsresponse, ArrayList.class);
        actualGetConnPropsResponseList = mapper.convertValue(actualGetConnPropsResponse,
                new TypeReference<List<ConnectionProperties>>() {});
    }

    /**
     * The expected connection properties will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the expected connectionProperties will be returned$")
    public void the_expected_connectionProperties_will_be_returned() throws Throwable {
        ResponseValidator.assertExpectedConnProps(ExpectedResponses.getAllConnProps(), actualGetConnPropsResponseList);
    }

    /**
     * A post request on connection properties with a valid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a post request on ConnectionProperties with a valid subsystem id$")
    public void a_post_request_on_ConnectionProperties_with_a_valid_subsystem_id() throws Throwable {
        connectionProperties = SupportedRequests.getValidConnectionProperties();
        connectionProperties.setSubsystemId(savedSubsystem.getId());
    }

    /**
     * I perform a post request on connection properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I perform a post request on ConnectionProperties$")
    public void i_perform_a_post_request_on_ConnectionProperties() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH,
                connectionProperties, Object.class);
    }

    /**
     * I request a list of connection properties for an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I request a list of connectionProperties for an invalid subsystem id$")
    public void i_request_a_list_of_connectionProperties_for_an_invalid_subsystem_id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + Constants.INVALID_SUBSYSTEM_ID + Constants.CONNECTION_PROPS_PATH,
                HttpMethod.GET, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * A post request on connection properties with an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^a post request on ConnectionProperties with an invalid subsystem id$")
    public void a_post_request_on_ConnectionProperties_with_an_invalid_subsystem_id() throws Throwable {
        connectionProperties = SupportedRequests.getConnectionPropertiesWithInvalidSubsystemId();
        responseEntity = this.getTemplate().postForEntity(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH  + Constants.INVALID_SUBSYSTEM_ID + Constants.CONNECTION_PROPS_PATH,
                connectionProperties, Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * A get request on connection properties with a valid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^a get request on connectionProperties with a valid connection properties id$")
    public void a_get_request_on_connectionProperties_with_a_valid_connection_properties_id() throws Throwable {
        final String getConnPropsresponse = this.getTemplate()
                .exchange(
                        getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                                + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                                + savedSubsystem.getConnectionProperties().get(0).getId(),
                        HttpMethod.GET, new HttpEntity(reqHeaders), String.class)
                .getBody();
        connectionProperties = objectMapper.readValue(getConnPropsresponse, ConnectionProperties.class);
    }

    /**
     * The expected connection property will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the expected connectionProperty will be returned$")
    public void the_expected_connectionProperty_will_be_returned() throws Throwable {
        ResponseValidator.assertExpectedConnProps(ExpectedResponses.getConnProp(), connectionProperties);
    }

    /**
     * A get request on connection properties with a invalid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^a get request on connectionProperties with a invalid connection properties id$")
    public void a_get_request_on_connectionProperties_with_a_invalid_connection_properties_id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + Constants.INVALID_CONNECTION_PROPS_ID,
                HttpMethod.GET, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.CONNECTION_PROPERTIES_DOES_NOT_EXIST;
    }

    /**
     * A valid put request on connection properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a valid put request on ConnectionProperties$")
    public void a_valid_put_request_on_ConnectionProperties() throws Throwable {
        connectionProperties = SupportedRequests.getValidConnectionProperties();
    }

    /**
     * I perform a put request on connection properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I perform a put request on ConnectionProperties$")
    public void i_perform_a_put_request_on_ConnectionProperties() throws Throwable {
        connectionProperties.setSubsystemId(savedSubsystem.getId());
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId(),
                HttpMethod.PUT, new HttpEntity(connectionProperties, reqHeaders), Object.class);
        putConnPropsresponse = this.getTemplate()
                .exchange(
                        getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH
                                + Constants.$_SLASH
                                + savedSubsystem.getConnectionProperties().get(0).getId(),
                        HttpMethod.PUT, new HttpEntity(reqHeaders), String.class)
                .getBody();
    }

    /**
     * The connection properties is successfully updated.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the ConnectionProperties is successfully updated$")
    public void the_ConnectionProperties_is_successfully_updated() throws Throwable {
        final String getConnPropsresponse = this.getTemplate()
                .exchange(
                        getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                                + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                                + savedSubsystem.getConnectionProperties().get(0).getId(),
                        HttpMethod.GET, new HttpEntity(reqHeaders), String.class)
                .getBody();
        connectionProperties = objectMapper.readValue(getConnPropsresponse, ConnectionProperties.class);
        actualConnPropsPutResponse = mapper.convertValue(connectionProperties,
                new TypeReference<ConnectionProperties>() {});
        ResponseValidator.assertExpectedConnProps(SupportedRequests.getValidConnectionProperties(),
                actualConnPropsPutResponse);
    }

    /**
     * I update a subsystem that specifies an invalid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I update a subsystem that specifies an invalid connection properties Id$")
    public void i_update_a_subsystem_that_specifies_an_invalid_connection_properties_Id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + Constants.INVALID_CONNECTION_PROPS_ID,
                HttpMethod.PUT, new HttpEntity(SupportedRequests.getValidConnectionProperties(), reqHeaders),
                Object.class);
        putConnPropsresponse = this.getTemplate()
                .exchange(
                        getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                                + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                                + savedSubsystem.getConnectionProperties().get(0).getId(),
                        HttpMethod.PUT, new HttpEntity(reqHeaders), String.class)
                .getBody();
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.CONNECTION_PROPERTIES_DOES_NOT_EXIST;
    }

    /**
     * I update a subsystem that specifies an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I update a subsystem that specifies an invalid subsystem Id$")
    public void i_update_a_subsystem_that_specifies_an_invalid_subsystem_Id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + "123" + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId(),
                HttpMethod.PUT, new HttpEntity(SupportedRequests.getValidConnectionProperties(), reqHeaders),
                Object.class);
        putConnPropsresponse = this.getTemplate()
                .exchange(
                        getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                                + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                                + savedSubsystem.getConnectionProperties().get(0).getId(),
                        HttpMethod.PUT, new HttpEntity(reqHeaders), String.class)
                .getBody();
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * A valid patch request on connection properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a valid patch request on ConnectionProperties$")
    public void a_valid_patch_request_on_ConnectionProperties() throws Throwable {
        patchField.put(Constants.$_USERNAME, Constants.$_PATCHED_USERNAME);
    }

    /**
     * I perform a patch request on connection properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I perform a patch request on ConnectionProperties$")
    public void i_perform_a_patch_request_on_ConnectionProperties() throws Throwable {
        final JSONObject patchBody = new JSONObject();
        patchBody.put(Constants.$_USERNAME, Constants.$_PATCHED_USERNAME);
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId(),
                HttpMethod.PATCH, new HttpEntity(patchBody.toString(), reqHeaders), Object.class);
    }

    /**
     * The connection properties is successfully patched.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the ConnectionProperties is successfully patched$")
    public void the_ConnectionProperties_is_successfully_patched() throws Throwable {
        final String getConnPropsresponse = this.getTemplate()
                .exchange(
                        getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                                + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                                + savedSubsystem.getConnectionProperties().get(0).getId(),
                        HttpMethod.GET, new HttpEntity(reqHeaders), String.class)
                .getBody();
        connectionProperties = objectMapper.readValue(getConnPropsresponse, ConnectionProperties.class);
        actualConnPropsPutResponse = mapper.convertValue(connectionProperties,
                new TypeReference<ConnectionProperties>() {});
        final ConnectionProperties expectedConnProps = SupportedRequests.getValidPatchedConnectionProperties();
        ResponseValidator.assertExpectedConnProps(expectedConnProps, actualConnPropsPutResponse);
    }

    /**
     * I partially update a subsystem that specifies an invalid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I partially update a subsystem that specifies an invalid connection properties Id$")
    public void i_partially_update_a_subsystem_that_specifies_an_invalid_connection_properties_Id() throws Throwable {
        final JSONObject patchBody = new JSONObject();
        patchBody.put(Constants.$_USERNAME, Constants.$_PATCHED_USERNAME);
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + Constants.INVALID_CONNECTION_PROPS_ID,
                HttpMethod.PATCH, new HttpEntity(patchBody.toString(), reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.CONNECTION_PROPERTIES_DOES_NOT_EXIST;
    }

    /**
     * I partially update a subsystem that specifies an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I partially update a subsystem that specifies an invalid subsystem Id$")
    public void i_partially_update_a_subsystem_that_specifies_an_invalid_subsystem_Id() throws Throwable {
        final JSONObject patchBody = new JSONObject();
        patchBody.put(Constants.$_USERNAME, Constants.$_PATCHED_USERNAME);
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + Constants.INVALID_SUBSYSTEM_ID + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId(),
                HttpMethod.PATCH, new HttpEntity(patchBody.toString(), reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * I delete a connection properties that exists.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I delete a connection properties that exists$")
    public void i_delete_a_connection_properties_that_exists() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId(),
                HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
    }

    /**
     * I delete a subsystem that specifying an invalid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I delete a subsystem specifying an invalid connection properties Id$")
    public void i_delete_a_subsystem_that_specifying_an_invalid_connection_properties_Id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + Constants.INVALID_CONNECTION_PROPS_ID,
                HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.CONNECTION_PROPERTIES_DOES_NOT_EXIST;
    }

    /**
     * I delete a subsystem that specifying an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I delete a subsystem specifying an invalid subsystem Id$")
    public void i_delete_a_subsystem_that_specifying_an_invalid_subsystem_Id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + Constants.INVALID_SUBSYSTEM_ID + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId(),
                HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * A valid post request on subsystem users.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a valid post request on SubsystemUsers$")
    public void a_valid_post_request_on_SubsystemUsers() throws Throwable {
        actualSubsystemUser = SupportedRequests.getValidSubsystemUser();
    }

    /**
     * I request a list of subsystem users for a specific connection property.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I request a list of subsystemUsers for a specific connection property$")
    public void i_request_a_list_of_subsystemUsers_for_a_specific_connection_property() throws Throwable {
        responseEntity = this.getTemplate().postForEntity(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId() + "/subsystem-users",
                new HttpEntity(actualSubsystemUser, reqHeaders), Object.class);
        final Object subsystemUsersResponseAsString = responseEntity.getBody();
        actualSubsystemUser = mapper.convertValue(subsystemUsersResponseAsString, new TypeReference<SubsystemUser>() {});
    }

    /**
     * The expected subsystem users will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the expected subsystemUsers will be returned$")
    public void the_expected_subsystemUsers_will_be_returned() throws Throwable {
        final Set<SubsystemUser> expectedReponseList = new HashSet<>();
        expectedReponseList.add(SupportedRequests.getValidSubsystemUser());
        ResponseValidator.assertExpectedSubsystemUsers(SupportedRequests.getValidSubsystemUser(), actualSubsystemUser);
    }

    /**
     * A post request on subsystem users with an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a post request on SubsystemUsers with an invalid subsystem id$")
    public void a_post_request_on_SubsystemUsers_with_an_invalid_subsystem_id() throws Throwable {
        actualSubsystemUser = SupportedRequests.getValidSubsystemUser();
        responseEntity = this.getTemplate().postForEntity(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + Constants.INVALID_SUBSYSTEM_ID + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId() + "/subsystem-users",
                new HttpEntity(actualSubsystemUser, reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * A post request on subsystem users with an invalid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a post request on SubsystemUsers with an invalid connection properties id$")
    public void a_post_request_on_SubsystemUsers_with_an_invalid_connection_properties_id() throws Throwable {
        actualSubsystemUser = SupportedRequests.getValidSubsystemUser();
        responseEntity = this.getTemplate().postForEntity(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + Constants.INVALID_CONNECTION_PROPS_ID + "/subsystem-users",
                new HttpEntity(actualSubsystemUser, reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.CONNECTION_PROPERTIES_DOES_NOT_EXIST;
    }

    /**
     * A valid delete request on subsystem users.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a valid delete request on SubsystemUsers$")
    public void a_valid_delete_request_on_SubsystemUsers() throws Throwable {
        responseEntity = this.getTemplate()
                .exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId() + Constants.SUBSYSTEM_USERS_PATH
                        + subsystemUserId, HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
    }

    /**
     * A delete request on subsystem users with an invalid subsystem id.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a delete request on SubsystemUsers with an invalid subsystem id$")
    public void a_delete_request_on_SubsystemUsers_with_an_invalid_subsystem_id() throws Throwable {
        responseEntity = this.getTemplate()
                .exchange(getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + Constants.INVALID_SUBSYSTEM_ID + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId() + Constants.SUBSYSTEM_USERS_PATH
                        + subsystemUserId, HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_DOES_NOT_EXIST;
    }

    /**
     * A delete request on subsystem users with an invalid connection properties id.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a delete request on SubsystemUsers with an invalid connection properties id$")
    public void a_delete_request_on_SubsystemUsers_with_an_invalid_connection_properties_id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + Constants.INVALID_CONNECTION_PROPS_ID + Constants.SUBSYSTEM_USERS_PATH + subsystemUserId,
                HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.CONNECTION_PROPERTIES_DOES_NOT_EXIST;
    }

    /**
     * A delete request on subsystem users with an invalid subsystem user id.
     *
     * @throws Throwable
     *             the throwable
     */
    @Given("^a delete request on SubsystemUsers with an invalid subsystem user id$")
    public void a_delete_request_on_SubsystemUsers_with_an_invalid_subsystem_user_id() throws Throwable {
        responseEntity = this.getTemplate().exchange(
                getServiceUrl() + Constants.SUBSYSTEM_PATH + Constants.$_SLASH
                        + savedSubsystem.getId() + Constants.CONNECTION_PROPS_PATH + Constants.$_SLASH
                        + savedSubsystem.getConnectionProperties().get(0).getId() + Constants.SUBSYSTEM_USERS_PATH
                        + Constants.INVALID_SUBSYSTEM_USER_ID,
                HttpMethod.DELETE, new HttpEntity(reqHeaders), Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_USER_DOES_NOT_EXIST;
    }

    /**
     * Builds the and persist subsystem entity.
     *
     * @param subsystemName
     *            the subsystem name
     */
    private void buildAndPersistSubsystemEntity(final String subsystemName) {
        final SubsystemUser subsystemUser = new SubsystemUser();
        final List<SubsystemUser> usersSet = new ArrayList<>();
        usersSet.add(subsystemUser);

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setSubsystemUsers(usersSet);
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(subsystemName);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 1);
        /*
         * SubsystemType subsystemType = new SubsystemType();
         * subsystemType.setId((long)1); subsystemType.setTenancySupport(false);
         * subsystemType.setType("DomainManager");
         * subsystemOnboardingRequest.setSubsystemType(subsystemType );
         */

        savedSubsystem = subsystemRepository.saveAndFlush(subsystemOnboardingRequest);
        subsystemUserId = savedSubsystem.getConnectionProperties().get(0).getSubsystemUsers().get(0).getId();
    }

    /**
     * Builds the and persist subsystem entity with no subsystem users associated.
     *
     * @param subsystemName
     *            the subsystem name
     */
    private void buildAndPersistSubsystemEntityWithNoSubsystemUsersAssociated(final String subsystemName) {

        final ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setProperties(PropertyFactory.buildDefaultProperties());
        final List<ConnectionProperties> connPropsList = new ArrayList<>();
        connPropsList.add(connectionProperties);

        final Subsystem subsystemOnboardingRequest = new Subsystem();
        subsystemOnboardingRequest.setName(subsystemName);
        subsystemOnboardingRequest.setConnectionProperties(connPropsList);
        subsystemOnboardingRequest.setHealthCheckTime("test-heathCheckTime");
        subsystemOnboardingRequest.setOperationalState(OperationalState.REACHABLE);
        subsystemOnboardingRequest.setUrl("test-url");
        subsystemOnboardingRequest.setSubsystemTypeId((long) 1);

        savedSubsystemWithNoSubsystemUsers = subsystemRepository.saveAndFlush(subsystemOnboardingRequest);
    }

    /**
     * The expected exception is returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the expected exception is returned$")
    public void the_expected_exception_is_returned() throws Throwable {
        ResponseValidator.assertExpected(expectedExceptionMsg, actualExceptionMsg);
    }

    /**
     * I request a list of paginated subsystems.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I request a list of paginated subsystems$")
    public void i_request_a_list_of_paginated_subsystems() throws Throwable {
        final String getSubsystemsresponse = this.getTemplate()
                .getForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, String.class).getBody();
        header = this.getTemplate().getForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, String.class).getHeaders();
        paginatedSubsystemList = objectMapper.readValue(getSubsystemsresponse, List.class);
        actualPaginatedSubsystemList = mapper.convertValue(paginatedSubsystemList,
                new TypeReference<List<Subsystem>>() {});
    }

    /**
     * The paginated subsystems will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the paginated subsystems will be returned$")
    public void the_paginated_subsystems_will_be_returned() throws Throwable {
        ResponseValidator.assertExpected(ExpectedResponses.getAllSubsystems(), actualPaginatedSubsystemList);
        ResponseValidator.assertExpected(ExpectedResponses.getAllSubsystems(), header);
    }

    /**
     * I send a get request on subsystems filtered ids.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I send a get request on subsystems filtered Ids$")
    public void i_send_a_get_request_on_subsystems_filtered_Ids() throws Throwable {
        final String response = this.getTemplate().getForEntity(getServiceUrl() + "/subsystems?select=id", String.class)
                .getBody();
        idList = objectMapper.readValue(response, new TypeReference<List<Object>>() {});
    }

    /**
     * The filtered ids of subsystems will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the filtered Ids of subsystems will be returned$")
    public void the_filtered_Ids_of_subsystems_will_be_returned() throws Throwable {
        ResponseValidator.assertExpectedId(ExpectedResponses.getFilteredIds(savedSubsystem.getId()), idList);
    }

    /**
     * The filtered id and name of subsystems will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the filtered id and name of subsystems will be returned$")
    public void the_filtered_id_and_name_of_subsystems_will_be_returned() throws Throwable {
        ResponseValidator.assertExpectedIdAndName(ExpectedResponses.getFilteredIdAndName(savedSubsystem.getId()),
                actualGetSubsystemsResponseList);
    }

    /**
     * I request a list of paginated subsystems with filter on connection properties.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I request a list of paginated subsystems with filter on ConnectionProperties$")
    public void i_request_a_list_of_paginated_subsystems_with_filter_on_ConnectionProperties() throws Throwable {
        final String getSubsystemsresponse = this.getTemplate()
                .getForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH + PAGINATION_URL, String.class).getBody();
        header = this.getTemplate()
                .getForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH + PAGINATION_URL, String.class).getHeaders();
        paginatedSubsystemList = objectMapper.readValue(getSubsystemsresponse, List.class);
        actualPaginatedSubsystemList = mapper.convertValue(paginatedSubsystemList,
                new TypeReference<List<Subsystem>>() {});
    }

    /**
     * The paginated and filtered subsystems will be returned.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^the paginated and filtered subsystems will be returned$")
    public void the_paginated_and_filtered_subsystems_will_be_returned() throws Throwable {
        ResponseValidator.assertExpected(ExpectedResponses.getAllSubsystems(), actualPaginatedSubsystemList);
        ResponseValidator.assertExpected(ExpectedResponses.getAllSubsystems(), header);
    }

    /**
     * A subsystem onboarding request with no properties inside connectionproperties.
     */
    @Given("^a subsystem onboarding request with no properties inside Connectionproperties$")
    public void aSubsystemOnboardingRequestWithNoPropertiesInsideConnectionproperties() {
        invalidSubsystemOnboardingRequest = SupportedRequests
                .getSubsystemOnboardingRequest("mySubsystem");
        invalidSubsystemOnboardingRequest.setSubsystemTypeId(Constants.INVALID_SUBSYSTEM_TYPE_ID);
        final List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        connectionPropertiesList.add(SupportedRequests.getConnectionPropertiesWithNoProperties());
        invalidSubsystemOnboardingRequest.setConnectionProperties(connectionPropertiesList);
    }

    /**
     * A subsystem onboarding request with an encrypted key not in properties.
     */
    @Given("^a subsystem onboarding request with an encrypted key not in properties$")
    public void aSubsystemOnboardingRequestWithAnEncryptedKeyNotInProperties() {
        invalidSubsystemOnboardingRequest = SupportedRequests
                .getSubsystemOnboardingRequestWithInvalidEncryptedKey();
    }

    /**
     * A subsystem onboarding request with duplicated keys under one connection property.
     */
    @Given("^a subsystem onboarding request with duplicated keys under one connectionProperty$")
    public void aSubsystemOnboardingRequestWithDuplicatedKeysUnderOneConnectionProperty() {
        invalidSubsystemOnboardingRequest = SupportedRequests
                .getSubsystemOnboardingRequest("Duplicate property keys in one connectionProperty");
        invalidSubsystemOnboardingRequest.setConnectionProperties(SupportedRequests.getInValidConnectionProperties("key1"));
    }

    /**
     * A post request on connection properties with no properties defined.
     */
    @Given("^a post request on ConnectionProperties with no properties defined$")
    public void aPostRequestOnConnectionPropertiesWithNoPropertiesDefined() {
        connectionProperties = SupportedRequests.getConnectionPropertiesWithNoProperties();
        connectionProperties.setSubsystemId(savedSubsystem.getId());
    }

    /**
     * A post request on connection properties with duplicated property keys.
     */
    @Given("^a post request on ConnectionProperties with duplicated property keys$")
    public void aPostRequestOnConnectionPropertiesWithDuplicatedPropertyKeys() {
        connectionProperties = SupportedRequests.getInValidConnectionProperties("key1").get(0);
        connectionProperties.setSubsystemId(savedSubsystem.getId());
    }

    /**
     * A post request on connection properties with an invalid encrypted key.
     */
    @Given("^a post request on ConnectionProperties with an invalid encrypted key$")
    public void aPostRequestOnConnectionPropertiesWithAnInvalidEncryptedKey() {
        connectionProperties = SupportedRequests.getValidConnectionProperties();
        final List<String> encryptedKeys = new ArrayList<>();
        encryptedKeys.add("UNIQUE_KEY_DOESNT_EXIST");
        connectionProperties.setEncryptedKeys(encryptedKeys);
        connectionProperties.setSubsystemId(savedSubsystem.getId());
    }

    /**
     * The subsystem user will be assigned to the correct connection property object.
     */
    @Then("^the subsystemUser will be assigned to the correct connectionPropertyObject$")
    public void theSubsystemUserWillBeAssignedToTheCorrectConnectionPropertyObject() {
        assertEquals(savedSubsystem.getConnectionProperties().get(0).getId(), actualSubsystemUser.getConnectionPropsId());
    }

    /**
     * A valid subsystem with connection properties.
     *
     * @param numOfConnectionProperties
     *            the num of connection properties
     */
    @Given("^a valid subsystem with (\\d+) ConnectionProperties$")
    public void aValidSubsystemWithConnectionProperties(int numOfConnectionProperties) {
        subsystemRepository.deleteAll();
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequest(Constants.SUBSYSTEM_NAME_ECM);
        subsystemOnboardingRequest.setConnectionProperties(SupportedRequests.createListOfConnectionProperties(numOfConnectionProperties));
    }

    /**
     * Gets the service url.
     *
     * @return the service url
     */
    private String getServiceUrl() {
        return "http://localhost:" + getPort() + "/subsystem-manager/v1";
    }

    /**
     * I try to onboard A second subsystem of type nfvo.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I try to onboard a second subsystem of type nfvo$")
    public void iTryToOnboardASecondSubsystemOfTypeNfvo() throws Throwable {
        subsystemOnboardingRequest.setName("secondNfvo");
        subsystemOnboardingRequest.setAdapterLink("test-adapter-link");
        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(2L);
        subsystemType.setType("NFVO");
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_IN_USE;
    }

    /**
     * I onboard the subsystem of type nfvo.
     *
     * @throws Throwable
     *             the throwable
     */
    @When("^I onboard the subsystem of type nfvo$")
    public void iOnboardTheSubsystemOfTypeNfvo() throws Throwable {
        final SubsystemType subsystemType = new SubsystemType();
        subsystemType.setId(2L);
        subsystemType.setType("NFVO");
        subsystemOnboardingRequest.setSubsystemType(subsystemType);
        subsystemOnboardingRequest.setAdapterLink("test-adapter-link");
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
    }

    /**
     * I onboard A second subsystem with same type but different vendor.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^I onboard a second subsystem with same type but different vendor$")
    public void iOnboardASecondSubsystemWithSameTypeButDifferentVendor() throws Throwable {
        subsystemOnboardingRequest.setName(DOMAIN_MANAGER_2);
        subsystemOnboardingRequest.setVendor("3PPVendor");
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
    }

    /**
     * I onboard A second subsystem with type domain manager and same vendor.
     *
     * @throws Throwable
     *             the throwable
     */
    @Then("^I onboard a second subsystem with type Domain Manager and same vendor$")
    public void iOnboardASecondSubsystemWithTypeDomainManagerAndSameVendor() throws Throwable {
        subsystemOnboardingRequest.setName(DOMAIN_MANAGER_2);
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
        actualExceptionMsg = responseEntity.getBody().toString();
        expectedExceptionMsg = Constants.SUBSYSTEM_IN_USE;
    }

    /**
     * An onboarded subsystem of type NFVO.
     */
    @Given("^an onboarded subsystem of type NFVO$")
    public void anOnboardedSubsystemOfTypeNFVO() {
        subsystemOnboardingRequest = SupportedRequests.getSubsystemOnboardingRequestNFVO("NfvoSubsystem");
        responseEntity = this.getTemplate().postForEntity(getServiceUrl() + Constants.SUBSYSTEM_PATH, subsystemOnboardingRequest,
                Object.class);
    }

    /**
     * I make A get request with the following query.
     *
     * @param query
     *            the query
     * @throws Throwable
     *             the throwable
     */
    @When("^I make a get request with the following query \"([^\"]*)\"$")
    public void iMakeAGetRequestWithTheFollowingQuery(String query) throws Throwable {
        final String getSubsystemsResponse = this.getTemplate()
                .getForEntity(getServiceUrl() + "/subsystems?" + query, String.class).getBody();
        actualGetSubsystemsResponse = objectMapper.readValue(getSubsystemsResponse, ArrayList.class);
        actualGetSubsystemsResponseList = mapper.convertValue(actualGetSubsystemsResponse,
                new TypeReference<List<Subsystem>>() {});
    }

    /**
     * A list of subsystems with type are returned.
     *
     * @param subsystemType
     *            the subsystem type
     * @throws Throwable
     *             the throwable
     */
    @Then("^a list of subsystems with type \"([^\"]*)\" are returned$")
    public void aListOfSubsystemsWithTypeAreReturned(String subsystemType) throws Throwable {
        assertTrue(actualGetSubsystemsResponseList.stream()
                .allMatch(subsystem -> subsystem.getSubsystemType().getType().equalsIgnoreCase(subsystemType)));
    }

}
