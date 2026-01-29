package io.github.victoriasemkina.validated.model;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Record для описания поля и его ограничений валидации.
 */
public record FieldDescriptor(
        String name,
        Class<?> type,
        List<Annotation> constraints
) {
    @Override
    public String toString() {
        return String.format("Field[name=%s, type=%s, constraints=%d]",
                name, type.getSimpleName(), constraints.size());
    }
}