package com.eurosoftit.ebok.service;

import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class SpecificationUtil {

    public static <T> Specification<T> likeUser(final String value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(eventRoot.get(field).get("login"), "%"+ value + "%");
    }

    public static <T> Specification<T> like(final String value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(eventRoot.get(field), "%"+ value + "%");
    }

    public static <T> Specification<T> isTrue(final boolean value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(eventRoot.get(field), value );
    }

    public static <T> Specification<T> lowerThan(final ZonedDateTime value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.lessThanOrEqualTo(eventRoot.get(field), value );
    }

    public static <T> Specification<T> greaterThan(final ZonedDateTime value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.greaterThanOrEqualTo(eventRoot.get(field), value );
    }

    public static <T> Specification<T> equal(final Object value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(eventRoot.get(field), value );
    }
}
