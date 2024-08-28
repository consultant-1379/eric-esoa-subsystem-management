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
package com.ericsson.bos.so.subsystemsmanager.test.legacy.business.pagination;

import com.ericsson.bos.so.subsystemsmanager.business.pagination.SubsystemPaginationUtil;
import com.ericsson.bos.so.subsystemsmanager.test.legacy.OldPropertyFactory;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.ConnectionProperties;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.OperationalState;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The Class SubsystemPaginationUtilTest.
 */
public class SubsystemPaginationUtilTest {

    private final List<Subsystem> subsystemList = new ArrayList<>();

    /**
     * Setup.
     *
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {
        final List<Long> ids = Arrays.asList(Long.valueOf(1), Long.valueOf(2), Long.valueOf(3), Long.valueOf(4));
        final List<String> names = Arrays.asList("testnamea", "testnameb", "testnameA", "testnamea");
        final List<String> uploadTimes =
                Arrays.asList("t09/02/2018 10:00:01", "t09/02/2018 10:01:00", "t09/02/2018 10:01:00", "t09/02/2018 10:00:00");
        final List<OperationalState> types =
                Arrays.asList(OperationalState.REACHABLE, OperationalState.REACHABLE, OperationalState.UNREACHABLE, OperationalState.UNREACHABLE);
        final List<Long> subsystemTypeIds = Arrays.asList(Long.valueOf(1), Long.valueOf(2), Long.valueOf(3), Long.valueOf(4));
        final List<ConnectionProperties> connPropListOne = Arrays.asList(setUpConnectionPropertyOne());
        final List<ConnectionProperties> connPropListTwo = Arrays.asList(setUpConnectionPropertyTwo());
        final List<ConnectionProperties> connPropListThree = Arrays.asList(setUpConnectionPropertyOne());
        final List<ConnectionProperties> connPropListFour = Arrays.asList(setUpConnectionPropertyTwo());
        final List<List<ConnectionProperties>> connectionProperties =
                Arrays.asList(connPropListOne, connPropListTwo, connPropListThree, connPropListFour);
        for (int i = 0; i < names.size(); i++) {
            final Subsystem subsystem = new Subsystem();
            subsystem.setId(ids.get(i));
            subsystem.setHealthCheckTime(uploadTimes.get(i));
            subsystem.setName(names.get(i));
            subsystem.setConnectionProperties(null);
            subsystem.setSubsystemType(null);
            subsystem.setOperationalState(types.get(i));
            subsystem.setSubsystemTypeId(subsystemTypeIds.get(i));
            subsystem.setConnectionProperties(connectionProperties.get(i));
            subsystemList.add(subsystem);
        }
    }

    /**
     * Given name when comparator by sort attr and sort dir asending.
     */
    @Test
    public void givenNamewhenComporatorBySortAttrAndSortDir_Asending() {
        final Comparator<Subsystem> comparator = SubsystemPaginationUtil.getComporatorBySortAttrAndSortDir("asc", "name");
        final Stream<Subsystem> stream = subsystemList.stream();
        final List<Subsystem> list = stream.sorted(comparator).collect(Collectors.toList());
        assertEquals("testnameA", list.get(0).getName());
    }

    /**
     * Given name when comparator by sort attr and sort dir descending.
     */
    @Test
    public void givenNamewhenComporatorBySortAttrAndSortDir_Descending() {
        final Comparator<Subsystem> comparator = SubsystemPaginationUtil.getComporatorBySortAttrAndSortDir("desc", "name");
        final Stream<Subsystem> stream = subsystemList.stream();
        final List<Subsystem> list = stream.sorted(comparator).collect(Collectors.toList());
        assertEquals("testnameA", list.get(3).getName());
    }

    /**
     * Given null comparator by sort attr and sort dir null.
     */
    @Test
    public void givenNullComporatorBySortAttrAndSortDir_Null() {
        final Comparator<Subsystem> comparator = SubsystemPaginationUtil.getComporatorBySortAttrAndSortDir(null, null);
        final Stream<Subsystem> stream = subsystemList.stream();
        final List<Subsystem> list = stream.sorted(comparator).collect(Collectors.toList());
        assertEquals(4, list.size());
    }

    /**
     * Given incorrect name when comparator by sort attr and sort dir exception.
     *
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    public void givenincorrectNamewhenComporatorBySortAttrAndSortDir_Exception() throws ClassNotFoundException {
        final Comparator<Subsystem> comparator = SubsystemPaginationUtil.getComporatorBySortAttrAndSortDir(null, "a");
        final Stream<Subsystem> stream = subsystemList.stream();
        final List<Subsystem> list = stream.sorted(comparator).collect(Collectors.toList());
        assertEquals(4, list.size());
    }

    /**
     * Given name then find the match with name.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    public void givenNamefindMatchwithName() throws JsonProcessingException {
        final String json = "{\"name\":\"testnamea\"}";
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(0), json);
        assertTrue(value);
    }

    /**
     * Given one filter field when filter called then true returned.
     */
    @Test
    public void givenOneFilterField_whenFilterCalled_thenTrueReturned() {
        final String json = "{\"name\": \"testnameb\"}";
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(1), json);
        assertTrue(value);
    }

    /**
     * Given nested filter field when filter called then true returned.
     */
    @Test
    public void givenNestedFilterField_whenFilterCalled_thenTrueReturned() {
        final String json = "{\"connectionProperties.name\": \"connPropOne\"}";
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(0), json);
        assertTrue(value);
    }

    /**
     * Given not existed nested filter field when filter called then false returned.
     */
    @Test
    public void givenNotExistedNestedFilterField_whenFilterCalled_thenFalseReturned() {
        final String json = "{\"connectionProperties.something\": \"connPropOne\"}";
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(0), json);
        assertFalse(value);
    }

    /**
     * Given nested filter field and not existed value when filter called then false returned.
     */
    @Test
    public void givenNestedFilterFieldAndNotExistedValue_whenFilterCalled_thenFalseReturned() {
        final String json = "{\"connectionProperties.name\": \"something\"}";
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(0), json);
        assertFalse(value);
    }

    /**
     * Given name find match assert false when not matched.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    public void givenNamefindMatch_nomatch() throws JsonProcessingException {
        final String json = "{\"name\":\"testnameZ\"}";
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(1), json);
        assertFalse(value);
    }

    /**
     * Given null json find match null.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    public void givenNullJsonfindMatch_null() throws JsonProcessingException {
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(1), "{}");
        assertTrue(value);
    }

    /**
     * Given name when find match exception.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    public void givenNamewhenfindMatch_Exception() throws JsonProcessingException {
        final String json = "{\"name\":\"testnameZ\"}".replace("name", "name12");
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(1), json);
        assertFalse(value);
    }

    /**
     * Find match throws exception when wrong json.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    public void findMatch_Exception_wrongjson() throws JsonProcessingException {
        final boolean value = SubsystemPaginationUtil.findMatchingFilterObject(subsystemList.get(1), "{");
        assertFalse(value);
    }

    /**
     * Teardown.
     *
     * @throws Exception
     *             the exception
     */
    @After
    public void teardown() throws Exception {
        subsystemList.clear();
    }

    /**
     * Setup connection property two.
     *
     * @return the connection properties
     */
    private ConnectionProperties setUpConnectionPropertyTwo() {
        final ConnectionProperties connProp = new ConnectionProperties();
        connProp.setId(Long.valueOf(10));
        connProp.setSubsystemId(Long.valueOf(9));
        connProp.setProperties(OldPropertyFactory.createCustomTestProperties(
                "username_1", "password_1", "connPropTwo", "tenant_1"));
        return connProp;
    }

    /**
     * Setup connection property one.
     *
     * @return the connection properties
     */
    private ConnectionProperties setUpConnectionPropertyOne() {
        final ConnectionProperties connProp = new ConnectionProperties();
        connProp.setId(Long.valueOf(13));
        connProp.setSubsystemId(Long.valueOf(9));
        connProp.setProperties(OldPropertyFactory.createCustomTestProperties(
                "username_1", "password_1", "connPropOne", "tenant_1"));
        return connProp;
    }
}
