package io.github.victoriasemkina.validated.generator.temporal;

import io.github.victoriasemkina.validated.core.ValueGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Генератор для LocalDateTime с поддержкой временных ограничений.
 * Учитывает аннотации: @Past, @Future, @PastOrPresent, @FutureOrPresent.
 */
public final class LocalDateTimeGenerator implements ValueGenerator {

    private static final Random RANDOM = new Random();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        return descriptor.type().equals(LocalDateTime.class);
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        // Проверяем конфликты аннотаций
        checkForConflicts(descriptor);

        // Определяем тип ограничения
        if (hasAnnotation(descriptor, Past.class)) {
            return generatePastDateTime(false);
        }
        if (hasAnnotation(descriptor, Future.class)) {
            return generateFutureDateTime(false);
        }
        if (hasAnnotation(descriptor, PastOrPresent.class)) {
            return generatePastDateTime(true);
        }
        if (hasAnnotation(descriptor, FutureOrPresent.class)) {
            return generateFutureDateTime(true);
        }

        // Нет аннотаций — генерируем нейтральную дату-время
        return generateNeutralDateTime();
    }

    private void checkForConflicts(FieldDescriptor descriptor) {
        boolean hasPast = hasAnnotation(descriptor, Past.class)
                || hasAnnotation(descriptor, PastOrPresent.class);
        boolean hasFuture = hasAnnotation(descriptor, Future.class)
                || hasAnnotation(descriptor, FutureOrPresent.class);

        if (hasPast && hasFuture) {
            throw new IllegalStateException(
                    "Конфликт аннотаций на поле '" + descriptor.name() +
                            "': не могут одновременно присутствовать @Past/@PastOrPresent и @Future/@FutureOrPresent"
            );
        }
    }

    private boolean hasAnnotation(FieldDescriptor descriptor, Class<?> annotationClass) {
        return descriptor.findConstraint((Class) annotationClass).isPresent();
    }

    private LocalDateTime generatePastDateTime(boolean includePresent) {
        LocalDateTime now = LocalDateTime.now();
        if (includePresent && RANDOM.nextBoolean()) {
            return now;
        }
        // Минимум 1 секунда — до 50 лет в прошлое
        long secondsBack = 1 + RANDOM.nextInt(365 * 24 * 60 * 60 * 50);
        return now.minusSeconds(secondsBack);
    }

    private LocalDateTime generateFutureDateTime(boolean includePresent) {
        LocalDateTime now = LocalDateTime.now();
        if (includePresent && RANDOM.nextBoolean()) {
            return now;
        }
        // Минимум 1 секунда — до 10 лет в будущее
        long secondsForward = 1 + RANDOM.nextInt(365 * 24 * 60 * 60 * 10);
        return now.plusSeconds(secondsForward);
    }

    private LocalDateTime generateNeutralDateTime() {
        // ±5 лет от текущего момента
        LocalDateTime now = LocalDateTime.now();
        long secondsOffset = RANDOM.nextInt(365 * 24 * 60 * 60 * 10)
                - (365 * 24 * 60 * 60 * 5);
        return now.plusSeconds(secondsOffset);
    }
}