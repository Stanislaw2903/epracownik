package com.eurosoftit.ebok.domain.specification;

import com.eurosoftit.ebok.common.controller.IEntity;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SpecificationReflectionBuilder {
    @SneakyThrows
    public static <K extends Serializable, T extends IEntity<K>, P> Specification<T> process(P object) {

        Field[] declaredFields = object.getClass().getDeclaredFields();

        Specification<T> specification = Specification.where(null);
        for (Field declaredField : declaredFields) {
            specification = process(declaredField, object, specification);
        }

        if (object.getClass().isAnnotationPresent(DateToAndFromInRange.class)) {
            Field dateFrom = object.getClass().getDeclaredField("dateFrom");
            dateFrom.setAccessible(true);
            Field dateTo = object.getClass().getDeclaredField("dateTo");
            dateTo.setAccessible(true);
            ZonedDateTime from = (ZonedDateTime) dateFrom.get(object);
            ZonedDateTime to = (ZonedDateTime) dateTo.get(object);

            final Specification<T> dateFromCondition = betweenThan(null, to, "dateFrom", "dateFrom");
            final Specification<T> dateToCondition = betweenThan(from, null, "dateTo", "dateTo");
            specification = specification.and(dateFromCondition).and(dateToCondition);
        }

        return specification;
    }

    @SneakyThrows
    private static <K extends Serializable, T extends IEntity<K>, P> Specification<T> process(Field field, P params, Specification<T> specification) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        field.setAccessible(true);
        Object value = field.get(params);
        for (Annotation annotation : annotations) {
            if (DateEquals.class.isInstance(annotation)) {
                DateEquals sourceAnnotation = DateEquals.class.cast(annotation);
                specification = specification.and(greaterThan((ZonedDateTime) value, getFieldName(field, sourceAnnotation.filed())));
            }

            if (DateFrom.class.isInstance(annotation)) {
                DateFrom sourceAnnotation = DateFrom.class.cast(annotation);
                specification = specification.and(greaterThan((ZonedDateTime) value, getFieldName(field, sourceAnnotation.filed())));
            }

            if (DateTo.class.isInstance(annotation)) {
                DateTo sourceAnnotation = DateTo.class.cast(annotation);
                specification = specification.and(lowerThan((ZonedDateTime) value, getFieldName(field, sourceAnnotation.filed())));
            }

            if (Equal.class.isInstance(annotation)) {
                Equal sourceAnnotation = Equal.class.cast(annotation);
                if (value instanceof Collection) {
                    if (!CollectionUtils.isEmpty((Collection) value)) {
                        specification = specification.and(in((List<Object>) value, getFieldName(field, sourceAnnotation.field())));
                    }
                } else {
                    specification = specification.and(equal(value, getFieldName(field, sourceAnnotation.field())));
                }
            }

            if (Like.class.isInstance(annotation)) {
                Like sourceAnnotation = Like.class.cast(annotation);
                specification = specification.and(like((String) value, getFieldName(field, sourceAnnotation.field()), sourceAnnotation.type()));
            }
        }

        return specification;
    }

    private static String getFieldName(Field field, String name) {
        return StringUtils.isBlank(name) ? field.getName() : name;
    }

    public static <T> Specification<T> like(final String value, String field, Like.Type type) {
        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : criteriaBuilder.like(criteriaBuilder.lower(getPath(field, eventRoot, JoinType.INNER)), type.toPredicate(value.toLowerCase()));
    }

    public static <T> Specification<T> isTrue(final boolean value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : criteriaBuilder.equal(getPath(field, eventRoot, JoinType.INNER), value);
    }

    public static <T> Specification<T> lowerThan(final ZonedDateTime value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : criteriaBuilder.lessThanOrEqualTo(getPath(field, eventRoot, JoinType.INNER), value);
    }

    public static <T> Specification<T> equals(final ZonedDateTime value, String field) {

        final ZonedDateTime realValue;
        if (value != null) {
            realValue = value.truncatedTo(ChronoUnit.HOURS);
        } else {
            realValue = null;
        }

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(realValue) ? null : criteriaBuilder.equal(criteriaBuilder.function("TRUNC", ZonedDateTime.class, getPath(field, eventRoot, JoinType.INNER)), value);
    }

    public static <T> Specification<T> betweenThan(final ZonedDateTime dateFrom, final ZonedDateTime dateTo, String fieldNameFrom, String fieldNameTo) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) -> {
            if (dateFrom == null) {
                if (dateTo != null) {
                    return criteriaBuilder.or(criteriaBuilder.isNull(eventRoot.get(fieldNameFrom)), criteriaBuilder.lessThanOrEqualTo(eventRoot.get(fieldNameFrom), dateTo));
                }

                return null;
            } else if (dateTo == null) {
                return criteriaBuilder.or(criteriaBuilder.isNull(eventRoot.get(fieldNameTo)), criteriaBuilder.greaterThanOrEqualTo(eventRoot.get(fieldNameTo), dateFrom.withZoneSameLocal(ZoneId.systemDefault())));
            }

            Path<ZonedDateTime> startDate = eventRoot.get(fieldNameFrom);
            Predicate startPredicate = criteriaBuilder.or(criteriaBuilder.isNull(startDate), criteriaBuilder.lessThanOrEqualTo(startDate, dateTo));
            Path<ZonedDateTime> endingDate = eventRoot.get(fieldNameTo);
            Predicate endPredicate = criteriaBuilder.or(criteriaBuilder.isNull(endingDate), criteriaBuilder.greaterThanOrEqualTo(endingDate, dateFrom));
            Predicate bothDatesNull = criteriaBuilder.and(criteriaBuilder.isNull(startDate), criteriaBuilder.isNull(endingDate));
            Predicate finalCondition = criteriaBuilder.or(bothDatesNull, criteriaBuilder.and(startPredicate, endPredicate));
            return finalCondition;
        };
    }

    public static <T> Specification<T> greaterThan(final ZonedDateTime value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : criteriaBuilder.greaterThanOrEqualTo(getPath(field, eventRoot, JoinType.INNER), value);
    }

    public static <T> Specification<T> equal(Object value, String field) {
        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : criteriaBuilder.equal(getPath(field, eventRoot, JoinType.INNER), value);
    }

    public static <T> Specification<T> in(final List<Object> value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : getPath(field, eventRoot, JoinType.INNER).in(value);
    }

    private static <T, Y> Path<Y> getPath(String field, Root<T> eventRoot, JoinType joinType) {
        Path<Y> path = null;
        Join<?, ?> join = null;
        for (String pathPart : field.split("\\.")) {
            if (Objects.isNull(path)) {
                path = eventRoot.get(pathPart);
                Class<?> javaType = path.getJavaType();
                if (shouldJoin(javaType)) {
                    join = eventRoot.getJoins().stream()
                        .filter(joinRecord -> joinRecord.getAttribute().getName().equalsIgnoreCase(pathPart) && joinRecord.getJoinType().equals(joinType))
                        .findFirst().orElseGet(() -> eventRoot.join(pathPart, joinType));
                }
            } else {
                if (join != null) {
                    path = join.get(pathPart);
                } else {
                    path = path.get(pathPart);
                }

                Class<?> javaType = path.getJavaType();
                if (shouldJoin(javaType)) {
                    join = eventRoot.getJoins().stream()
                        .filter(joinRecord -> joinRecord.getAttribute().getName().equalsIgnoreCase(pathPart) && joinRecord.getJoinType().equals(joinType))
                        .findFirst().orElseGet(() -> eventRoot.join(pathPart, joinType));
                }
            }
        }
        return path;
    }

    private static boolean shouldJoin(Class<?> javaType) {
        return javaType.isAssignableFrom(IEntity.class) || Collection.class.isAssignableFrom(javaType) || javaType.isAnnotationPresent(Entity.class);
    }

    public static <T> Specification<T> distinct() {
        return (root, query, cb) -> {
            query.distinct(true);
            return null;
        };
    }
}
