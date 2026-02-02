package io.github.victoriasemkina.validated.rule;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import jakarta.validation.constraints.Email;

import java.util.Map;
import java.util.Optional;

/**
 * Правило: генерация email на основе firstName и lastName.
 *
 * Применяется если:
 * 1. Поле имеет аннотацию @Email с regexp = ".+@company\\.com"
 * 2. В контексте уже есть значения для полей "firstName" и "lastName"
 */
public class EmailFromNameRule implements Rule {

    @Override
    public boolean matches(FieldDescriptor targetField, Map<String, Object> context) {
        // 1. Проверяем, что это поле с аннотацией @Email
        Optional<Email> emailAnnotation = targetField.findConstraint(Email.class);
        if (emailAnnotation.isEmpty()) {
            return false;
        }

        // 2. Проверяем специфичный regexp (если указан)
        String regexp = emailAnnotation.get().regexp();
        boolean isCompanyEmail = regexp.equals(".+@company\\.com");
        boolean isDefaultEmail = regexp.isEmpty() || regexp.equals(".*");

        // Правило применяется только для company email ИЛИ любых email
        // если в контексте есть имена (можно настроить логику)
        if (!isCompanyEmail && !isDefaultEmail) {
            return false;
        }

        // 3. Проверяем, что в контексте есть нужные поля
        boolean hasFirstName = context.containsKey("firstName");
        boolean hasLastName = context.containsKey("lastName");

        return hasFirstName && hasLastName;
    }

    @Override
    public Object generate(FieldDescriptor targetField, Map<String, Object> context) {
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");

        // Базовая логика: firstName.lastName@company.com
        String emailName = firstName.toLowerCase() + "." + lastName.toLowerCase();

        // Проверяем, какой домен нужен по аннотации
        Optional<Email> emailAnnotation = targetField.findConstraint(Email.class);
        if (emailAnnotation.isPresent()) {
            String regexp = emailAnnotation.get().regexp();
            if (regexp.equals(".+@company\\.com")) {
                return emailName + "@company.com";
            }
        }

        // Fallback: любой домен
        return emailName + "@example.com";
    }

    @Override
    public int getPriority() {
        return 10; // Высокий приоритет
    }
}