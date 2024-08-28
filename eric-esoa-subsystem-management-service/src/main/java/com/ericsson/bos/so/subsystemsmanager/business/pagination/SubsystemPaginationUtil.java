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
package com.ericsson.bos.so.subsystemsmanager.business.pagination;

import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Property;
import com.ericsson.bos.so.subsystemsmanager.api.models.entities.Subsystem;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class SubsystemPaginationUtil.
 */
@Component
public class SubsystemPaginationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SubsystemPaginationUtil.class);
    private static final String DESC = "desc";
    private static final String CHECKED = "checked";

    @PersistenceContext
    private EntityManager entityManager;


    private CriteriaQuery<Subsystem> query;

    @PostConstruct
    private void init() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        query = criteriaBuilder.createQuery(Subsystem.class);
    }

    /**
     * Gets the paginated subsystems.
     *
     * @param offset the offset
     * @param limit the limit
     * @return the paginated subsystems
     */
    public List<Subsystem> getPaginatedSubsystems(final int offset, final int limit) {
        LOG.info("Received pagination request with offset : {}  & limit : {} ", offset,
                limit);
        final TypedQuery<Subsystem> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(offset);
        typedQuery.setMaxResults(limit);

        final List<Subsystem> subsystems = typedQuery.getResultList();
        subsystems.forEach(subsystem -> subsystem.getSubsystemType().resolveCategory());

        return subsystems;
    }

    /**
     * Gets the Comparator by sort attr and sort dir.
     *
     * @param sortDir the sort dir
     * @param sortAttr the sort attr
     * @return the Comparator by sort attr and sort dir
     */
    public static Comparator<Subsystem> getComporatorBySortAttrAndSortDir(final String sortDir, final String sortAttr) {
        if (null != sortAttr) {
            final MethodType getter;
            try {
                final MethodHandles.Lookup caller = MethodHandles.lookup();
                final Stream<Method> methodStream = Arrays.stream(Subsystem.class.getMethods());
                final String methodName = "get" + sortAttr.substring(0, 1).toUpperCase() + sortAttr.substring(1);
                final Optional<Method> method = methodStream.filter(m -> m.getName().equals(methodName)).findFirst();

                if (method.isPresent()) {
                    final String returnType = method.get().getGenericReturnType().getTypeName();
                    getter = MethodType.methodType(ClassUtils.getClass(returnType));
                    final MethodHandle target = caller.findVirtual(Subsystem.class, methodName, getter);
                    final MethodType func = target.type();

                    final CallSite site = LambdaMetafactory.metafactory(caller, "apply",
                            MethodType.methodType(Function.class), func.generic(), target, func);
                    final MethodHandle factory = site.getTarget();
                    final Function r = (Function) factory.invoke();

                    final Comparator<Subsystem> ascend = Comparator.comparing(r, Comparator.nullsFirst(Comparator.naturalOrder()));
                    final Comparator<Subsystem> desend = Comparator.comparing(r, Comparator.nullsFirst(Comparator.reverseOrder()));

                    return sortDir.equalsIgnoreCase(DESC) ? desend : ascend;
                } else {
                    LOG.info("SortAttr not found {}", sortAttr);
                }
            } catch (Throwable ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return Comparator.comparing(Subsystem::getName, Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    /**
     * Find matching filter object.
     *
     * @param subsystem the subsystem
     * @param filter the filter
     * @return true, if successful
     */
    public static boolean findMatchingFilterObject(final Subsystem subsystem, final String filter) {
        final Map<String, String> filterResults;
        try {
            filterResults = new ObjectMapper().readValue(filter, HashMap.class);
            if (null != filterResults) {
                filterResults.forEach((key, value) -> iterateSubsystems(filterResults, key, value, subsystem));
                return filterResults.values().stream().allMatch(e -> e.equals(CHECKED));
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return false;
    }

    private static void iterateSubsystems(final Map<String, String> result, final String key, final String value, final Subsystem subsystem) {
        try {
            final String[] queryParams = key.split("\\.");
            if (queryParams.length == 1) {
                final Field field = subsystem.getClass().getDeclaredField(key);
                field.setAccessible(true);
                final String fieldValue = field.get(subsystem).toString();
                if (null != fieldValue && fieldValue.toUpperCase().contains(value.toUpperCase())) {
                    result.put(key, CHECKED);
                }
            } else {
                iterateConnectionProperties(result, key, value, subsystem, queryParams);
            }
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void iterateConnectionProperties(final Map<String, String> result, final String key,
            final String value, final Subsystem subsystem, final String[] queryParams) {
        if (null != subsystem.getConnectionProperties()) {
            final int connPropSize = subsystem.getConnectionProperties().size();
            for (int i = 0; i < connPropSize; i++) {
                final StringBuilder sb = new StringBuilder();
                sb.append(queryParams[0]).append("[").append(i).append("]").append(queryParams[1]);
                Property foundProperty = null;
                for(Property property: subsystem.getConnectionProperties().get(i).getProperties()){
                    if(property.getKey().equalsIgnoreCase(queryParams[1])){
                        foundProperty = property;
                    }
                }
                if(foundProperty != null && null != foundProperty.getValue() &&
                        foundProperty.getValue().toUpperCase().contains(value.toUpperCase())) {
                    result.put(key, CHECKED);
                }
            }
        } else {
            LOG.info("the specified subsystem contains no connection properties, subsystem: {}", subsystem);
        }
    }

}