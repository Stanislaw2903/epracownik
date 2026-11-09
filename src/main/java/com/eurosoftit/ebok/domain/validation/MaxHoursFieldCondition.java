package com.eurosoftit.ebok.domain.validation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = MaxHoursValidator.class)
public @interface MaxHoursFieldCondition {
    String name();
    String value();
}
