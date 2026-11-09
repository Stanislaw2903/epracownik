package com.eurosoftit.ebok.domain.specification;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Like {
    Type type() default Type.BOTH;
    String field() default StringUtils.EMPTY;

    enum Type {
        BEFORE{
            @Override
            public String toPredicate(String value) {
                return "%" + value;
            }
        }, AFTER {
            @Override
            public String toPredicate(String value) {
                return value + "%";
            }
        }, BOTH {
            @Override
            public String toPredicate(String value) {
                return  "%" + value+ "%";
            }
        };

        public abstract String toPredicate(String value);
    }
}
