package io.github.victoriasemkina.validated;

import io.github.victoriasemkina.validated.generator.DefaultGenerator;
import io.github.victoriasemkina.validated.generator.EmailGenerator;
import io.github.victoriasemkina.validated.generator.StringGenerator;
import io.github.victoriasemkina.validated.generator.ValueGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Главный класс-билдер для генерации валидных объектов.
 */
public class ValidatedBuilder<T> {

    private final Class<T> targetClass;
    private final List<ValueGenerator> generators;

    private ValidatedBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.generators = List.of(
                new EmailGenerator(),
                new StringGenerator(),
                new DefaultGenerator()
        );
    }

    public static <T> ValidatedBuilder<T> forClass(Class<T> clazz) {
        return new ValidatedBuilder<>(clazz);
    }

    private List<FieldDescriptor> scanFields() {
        List<FieldDescriptor> descriptors = new ArrayList<>();
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            descriptors.add(new FieldDescriptor(
                    field.getName(),
                    field.getType(),
                    List.of(field.getAnnotations())
            ));
        }

        return descriptors;
    }

    private ValueGenerator findGenerator(FieldDescriptor descriptor) {
        return generators.stream()
                .filter(g -> g.supports(descriptor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No generator found for field: " + descriptor.name()
                ));
    }

    public T buildValid() {
        System.out.println("=== Генерация объекта класса: " + targetClass.getSimpleName() + " ===");

        List<FieldDescriptor> fields = scanFields();
        System.out.println("Найдено полей: " + fields.size());

        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();

            for (FieldDescriptor field : fields) {
                Field javaField = targetClass.getDeclaredField(field.name());
                javaField.setAccessible(true);

                ValueGenerator generator = findGenerator(field);
                Object value = generator.generate(field);

                javaField.set(instance, value);
                System.out.println("  Установлено поле " + field.name() + " = " + value);
            }

            System.out.println("=== Объект успешно создан ===");
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании объекта", e);
        }
    }
}
