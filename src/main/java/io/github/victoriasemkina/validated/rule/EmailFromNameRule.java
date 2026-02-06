package io.github.victoriasemkina.validated.rule;

import io.github.victoriasemkina.validated.model.FieldDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Правило для генерации корпоративного email на основе имени пользователя.
 * Формат: {firstName}.{lastName}@company.com
 */
public class EmailFromNameRule implements Rule {

    // Поля с именем в порядке приоритета
    private static final List<String> NAME_FIELDS = Arrays.asList(
            "firstName", "first_name", "givenName",
            "lastName", "last_name", "surname",
            "name", "fullName", "full_name"
    );

    private static final String COMPANY_DOMAIN = "company.com";
    private static final int PRIORITY = 100;

    @Override
    public boolean matches(FieldDescriptor field, Map<String, Object> context) {
        // 1. Проверяем, что поле содержит "email" или "mail" в названии
        String fieldName = field.name().toLowerCase();
        boolean isEmailField = fieldName.contains("email") || fieldName.contains("mail");
        if (!isEmailField) {
            return false;
        }

        // 2. Проверяем наличие имени в контексте
        return hasNameInContext(context);
    }

    @Override
    public Object generate(FieldDescriptor field, Map<String, Object> context) {
        String firstName = extractField(context, "firstName", "first_name", "givenName", "name");
        String lastName = extractField(context, "lastName", "last_name", "surname");

        // Формируем локальную часть email
        StringBuilder localPart = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            localPart.append(normalize(firstName));
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (localPart.length() > 0) {
                localPart.append("."); // ← ТОЧКА как разделитель (стандартный формат)
            }
            localPart.append(normalize(lastName));
        }

        // Фолбэк если оба поля пустые
        if (localPart.length() == 0) {
            localPart.append("user");
        }

        return localPart.toString() + "@" + COMPANY_DOMAIN;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    private boolean hasNameInContext(Map<String, Object> context) {
        return NAME_FIELDS.stream()
                .anyMatch(context::containsKey);
    }

    private String extractField(Map<String, Object> context, String... candidates) {
        for (String candidate : candidates) {
            if (context.containsKey(candidate)) {
                Object value = context.get(candidate);
                if (value instanceof String str && !str.trim().isEmpty()) {
                    return str.trim();
                }
            }
        }
        return null;
    }

    private String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        // Оставляем только латинские буквы и цифры (удаляем спецсимволы, пробелы, кириллицу)
        // НО точки и дефисы НЕ добавляем сюда — они удаляются, а точка добавляется явно как разделитель
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]", ""); // удаляем всё кроме букв и цифр
    }
}