package com.eurosoftit.ebok;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean value) {

        if(Objects.isNull(value)){
            return null;
        }

        return value ? "T" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String value) {

        if(StringUtils.isBlank(value)){
            return null;
        }

        return "T".equalsIgnoreCase(value);
    }
}
