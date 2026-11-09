package com.eurosoftit.ebok.notification.birthday;

import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TemplateInterpreter {

    private static final Pattern PATTERN = Pattern.compile("(\\{\\w+\\})");

    /**
     * @param object
     * @param textTemplate
     * @return zinterpretowany szablon wiadomosci
     */

    static String process(Object object, String textTemplate) {

        if (Objects.isNull(object)) {
            return textTemplate;
        }

        int shift = 0;
        final Matcher matcher = PATTERN.matcher(textTemplate);
        final StringBuilder textTemplateBuilder = new StringBuilder(textTemplate);
        while (matcher.find()) {
            final int start = matcher.start() + shift;
            final int end = matcher.end() + shift;

            final String group = matcher.group();
            final String interpretedTemplate = interpret(object, group);

            if (Objects.nonNull(interpretedTemplate)) {
                shift += interpretedTemplate.length() - group.length();
                textTemplateBuilder.replace(start, end, interpretedTemplate);
            }
        }

        return textTemplateBuilder.toString();
    }

    @SneakyThrows
    private static String interpret(Object object, String group) {
        final String fieldName = group.substring(1, group.length() - 1);
        final Field field = ReflectionUtils.findField(object.getClass(), fieldName);

        if (field == null) {
            return null;
        }

        field.setAccessible(true);

        return (String) field.get(object);
    }
}
