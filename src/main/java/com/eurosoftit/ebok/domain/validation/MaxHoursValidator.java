package com.eurosoftit.ebok.domain.validation;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.nonNull;

public class MaxHoursValidator implements ConstraintValidator<MaxHours, Object> {

    private MaxHours annotation;

    @Override
    public void initialize(MaxHours annotation) {
        this.annotation = annotation;
    }

    @Override
    @SneakyThrows
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Class<?> clazz = object.getClass();

        Temporal from = getTemporal(annotation.filedFrom(), object, clazz);
        Temporal to = getTemporal(annotation.filedTo(), object, clazz);

        MaxHoursFieldCondition[] maxHoursFieldConditions = annotation.checkIf();
        if (ArrayUtils.isNotEmpty(maxHoursFieldConditions)) {
            for (MaxHoursFieldCondition maxHoursFieldCondition : maxHoursFieldConditions) {
                String name = maxHoursFieldCondition.name();
                String value = maxHoursFieldCondition.value();

                Field declaredField = clazz.getDeclaredField(name);
                declaredField.setAccessible(true);
                Object realValue = declaredField.get(object);

                if(nonNull(realValue) && !realValue.toString().equalsIgnoreCase(value)){
                   return true;
                }
            }
        }

        if(nonNull(from) && nonNull(to) && HOURS.between(from, to) > annotation.max()) {
            addInvalidMessages(context);
            return false;
        }

        return true;
    }

    private void addInvalidMessages(ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(annotation.message())
            .addPropertyNode(annotation.filedFrom())
            .addConstraintViolation();

        context.buildConstraintViolationWithTemplate(annotation.message())
            .addPropertyNode(annotation.filedTo())
            .addConstraintViolation();
        context.disableDefaultConstraintViolation();

    }

    private Temporal getTemporal(String fieldName, Object object, Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        Field filedFrom = clazz.getDeclaredField(fieldName);
        filedFrom.setAccessible(true);
        return (Temporal) filedFrom.get(object);
    }
}
