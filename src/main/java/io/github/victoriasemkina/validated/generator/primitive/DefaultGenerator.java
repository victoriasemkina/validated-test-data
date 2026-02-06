package io.github.victoriasemkina.validated.generator.primitive;

import io.github.victoriasemkina.validated.core.ValueGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;
import net.datafaker.Faker;

import java.util.Random;

/**
 * Генератор по умолчанию для любых типов.
 */
public final class DefaultGenerator implements ValueGenerator {
    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        return true;
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        Class<?> type = descriptor.type();

        if (type.equals(int.class) || type.equals(Integer.class)) {
            return random.nextInt(100);
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            return random.nextLong(1000);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return random.nextDouble() * 100;
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(String.class)) {
            return faker.lorem().word();
        }

        return null;
    }
}