package io.github.victoriasemkina.validated.generator;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import net.datafaker.Faker;

/**
 * Генератор строковых значений с использованием DataFaker.
 */
public final class StringGenerator implements ValueGenerator {
    private final Faker faker = new Faker();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        return descriptor.type().equals(String.class);
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        // Позже будет добавлена логика для разных аннотаций
        return faker.name().firstName();
    }
}