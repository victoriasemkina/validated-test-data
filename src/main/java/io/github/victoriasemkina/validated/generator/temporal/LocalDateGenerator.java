// src/main/java/io/github/victoriasemkina/validated/generator/LocalDateGenerator.java

package io.github.victoriasemkina.validated.generator.temporal;

import io.github.victoriasemkina.validated.core.ValueGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.Random;

/**
 * Генератор для LocalDate с поддержкой временных ограничений.
 * Учитывает аннотации: @Past, @Future, @PastOrPresent, @FutureOrPresent.
 */
public final class LocalDateGenerator implements ValueGenerator {

    private static final Random RANDOM = new Random();

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        return descriptor.type().equals(LocalDate.class);
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        // Проверяем конфликты аннотаций
        checkForConflicts(descriptor);

        // Определяем тип ограничения
        if (hasAnnotation(descriptor, Past.class)) {
            return generatePastDate(false);
        }
        if (hasAnnotation(descriptor, Future.class)) {
            return generateFutureDate(false);
        }
        if (hasAnnotation(descriptor, PastOrPresent.class)) {
            return generatePastDate(true);
        }
        if (hasAnnotation(descriptor, FutureOrPresent.class)) {
            return generateFutureDate(true);
        }

        // Нет аннотаций — генерируем нейтральную дату
        return generateNeutralDate();
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

    private LocalDate generatePastDate(boolean includePresent) {
        LocalDate now = LocalDate.now();
        if (includePresent && RANDOM.nextBoolean()) {
            return now;
        }
        // 1 день — 50 лет в прошлое
        int daysBack = 1 + RANDOM.nextInt(365 * 50);
        return now.minusDays(daysBack);
    }

    private LocalDate generateFutureDate(boolean includePresent) {
        LocalDate now = LocalDate.now();
        if (includePresent && RANDOM.nextBoolean()) {
            return now;
        }
        // 1 день — 10 лет в будущее
        int daysForward = 1 + RANDOM.nextInt(365 * 10);
        return now.plusDays(daysForward);
    }

    private LocalDate generateNeutralDate() {
        // ±5 лет от текущей даты
        LocalDate now = LocalDate.now();
        int daysOffset = RANDOM.nextInt(365 * 10) - (365 * 5);
        return now.plusDays(daysOffset);
    }
}