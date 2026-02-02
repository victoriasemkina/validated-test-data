package io.github.victoriasemkina.validated.generator;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import jakarta.validation.constraints.Size;
import net.datafaker.Faker;

import java.util.Optional;
import java.util.Random;

/**
 * Генератор строковых значений с использованием DataFaker.
 */
public final class StringGenerator implements ValueGenerator {
    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        return descriptor.type().equals(String.class);
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        String fieldName = descriptor.name().toLowerCase();

        // 1. Определяем тип поля по имени (семантическая логика)
        if (fieldName.contains("firstname") || fieldName.contains("name") ||
                fieldName.contains("имя") || fieldName.contains("firstName")) {
            return generateNameWithSize(descriptor, true);
        }

        if (fieldName.contains("lastname") || fieldName.contains("surname") ||
                fieldName.contains("фамилия") || fieldName.contains("lastName")) {
            return generateNameWithSize(descriptor, false);
        }

        if (fieldName.contains("description") || fieldName.contains("описание") ||
                fieldName.contains("text") || fieldName.contains("текст")) {
            return generateTextWithSize(descriptor);
        }

        // 2. Для остальных строк - базовая логика с @Size
        return generateGenericStringWithSize(descriptor);
    }

    private String generateNameWithSize(FieldDescriptor descriptor, boolean isFirstName) {
        Optional<Size> sizeConstraint = descriptor.getSizeConstraint();
        String name = isFirstName ? faker.name().firstName() : faker.name().lastName();

        if (sizeConstraint.isPresent()) {
            Size size = sizeConstraint.get();
            // Подгоняем имя под ограничения @Size
            return adjustStringToSize(name, size.min(), size.max());
        }

        return name;
    }

    private String generateTextWithSize(FieldDescriptor descriptor) {
        Optional<Size> sizeConstraint = descriptor.getSizeConstraint();

        if (sizeConstraint.isPresent()) {
            Size size = sizeConstraint.get();
            int min = Math.max(size.min(), 1);
            int max = size.max() == Integer.MAX_VALUE ? 500 : size.max();
            int length = random.nextInt(min, max + 1);

            // Генерируем текст нужной длины
            return faker.lorem().characters(length);
        }

        return faker.lorem().sentence();
    }

    private String generateGenericStringWithSize(FieldDescriptor descriptor) {
        Optional<Size> sizeConstraint = descriptor.getSizeConstraint();

        if (sizeConstraint.isPresent()) {
            Size size = sizeConstraint.get();
            int min = Math.max(size.min(), 1);
            int max = size.max() == Integer.MAX_VALUE ? 100 : size.max();
            int length = random.nextInt(min, max + 1);

            return faker.lorem().characters(length);
        }

        return faker.lorem().word();
    }

    private String adjustStringToSize(String original, int min, int max) {
        if (original.length() >= min && original.length() <= max) {
            return original;
        }

        // Если имя слишком короткое - повторяем
        if (original.length() < min) {
            return original.repeat((min / original.length()) + 1).substring(0, min);
        }

        // Если слишком длинное - обрезаем
        return original.substring(0, Math.min(original.length(), max));
    }
}