package io.github.victoriasemkina.validated.generator;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import net.datafaker.Faker;

/**
 * Специальный генератор для email-полей.
 * Распознаёт поля по имени (содержит "email" или "mail").
 */
public final class EmailGenerator implements ValueGenerator {
    private final Faker faker = new Faker();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        String fieldName = descriptor.name().toLowerCase();
        return descriptor.type().equals(String.class) &&
                (fieldName.contains("email") || fieldName.contains("mail"));
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        return faker.internet().emailAddress();
    }
}