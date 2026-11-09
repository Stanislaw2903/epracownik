package com.eurosoftit.ebok.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = MaxHoursValidator.class)
public @interface MaxHours {
    int max();
    String filedFrom();
    String filedTo();

    MaxHoursFieldCondition[] checkIf() default {};

    String message() default "{javax.validation.constraints.MaxHours.message}";

    Class<?>[] groups() default {};

    @SuppressWarnings("unused")
    Class<? extends Payload>[] payload() default {};
}
