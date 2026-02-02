package io.github.victoriasemkina.validated.generator;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import jakarta.validation.constraints.Email;
import net.datafaker.Faker;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Специальный генератор для email-полей.
 * Учитывает аннотацию @Email с regexp, если она есть.
 */
public final class EmailGenerator implements ValueGenerator {
    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        String fieldName = descriptor.name().toLowerCase();
        return descriptor.type().equals(String.class) &&
                (fieldName.contains("email") || fieldName.contains("mail"));
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        // 1. Пытаемся получить аннотацию @Email
        Optional<Email> emailAnnotation = descriptor.findConstraint(Email.class);

        if (emailAnnotation.isPresent()) {
            Email emailConstraint = emailAnnotation.get();
            String regexp = emailConstraint.regexp();

            // 2. Если regexp не дефолтный (не пустая строка), генерируем по нему
            if (!regexp.isEmpty() && !regexp.equals(".*")) {
                return generateEmailByRegexp(regexp, descriptor);
            }
        }

        // 3. Иначе - стандартная генерация
        return faker.internet().emailAddress();
    }

    private String generateEmailByRegexp(String regexp, FieldDescriptor descriptor) {
        // Простейшая реализация для паттерна ".+@company\\.com"
        if (regexp.equals(".+@company\\.com")) {
            // Генерируем имя для email
            String name = faker.name().firstName().toLowerCase() +
                    "." + faker.name().lastName().toLowerCase();
            // Убираем пробелы и специальные символы
            name = name.replaceAll("[^a-zA-Z0-9.]", "");
            return name + "@company.com";
        }

        // Для других regexp можно добавить логику позже
        // Пока возвращаем fallback
        return faker.internet().emailAddress();
    }
}