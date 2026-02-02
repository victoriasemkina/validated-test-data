package io.github.victoriasemkina.validated.model;

import jakarta.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Неизменяемая запись (Record), описывающая поле класса и его ограничения валидации.
 * Использование Java 17 Record для моделирования данных.
 */
public record FieldDescriptor(
        String name,
        Class<?> type,
        List<Annotation> constraints,
        boolean isRequired
) {

    public static FieldDescriptor from(Field field) {
        Annotation[] annotations = field.getAnnotations();
        boolean isRequired = Arrays.stream(annotations)
                .anyMatch(a -> a.annotationType().equals(NotNull.class) ||
                        a.annotationType().equals(NotBlank.class) ||
                        a.annotationType().equals(NotEmpty.class));

        return new FieldDescriptor(
                field.getName(),
                field.getType(),
                Arrays.asList(annotations),
                isRequired
        );
    }

    /**
     * Проверяет, есть ли у поля ограничение указанного типа.
     */
    public boolean hasConstraint(Class<? extends Annotation> constraintType) {
        return constraints.stream()
                .anyMatch(c -> c.annotationType().equals(constraintType));
    }

    /**
     * Возвращает ограничение указанного типа, если оно есть.
     */
    public <T extends Annotation> Optional<T> findConstraint(Class<T> constraintType) {
        return constraints.stream()
                .filter(c -> c.annotationType().equals(constraintType))
                .findFirst()
                .map(constraintType::cast);
    }

    /**
     * Проверяет, есть ли у поля ограничение @Size и возвращает его параметры.
     */
    public Optional<Size> getSizeConstraint() {
        return findConstraint(Size.class);
    }

    /**
     * Проверяет, есть ли у поля ограничение @NotNull/@NotBlank/@NotEmpty.
     */
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public String toString() {
        return String.format("Field[name=%s, type=%s, constraints=%d, required=%s]",
                name, type.getSimpleName(), constraints.size(), isRequired);
    }
}