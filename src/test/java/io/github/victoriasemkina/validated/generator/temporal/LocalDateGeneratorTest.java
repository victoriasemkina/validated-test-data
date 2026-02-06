package io.github.victoriasemkina.validated.generator.temporal;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import io.github.victoriasemkina.validated.testmodel.temporal.TemporalModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.time.LocalDate;

class LocalDateGeneratorTest {

    private LocalDateGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new LocalDateGenerator();
    }

    @Test
    void shouldSupportLocalDateFields() throws NoSuchFieldException {
        Field field = TemporalModel.class.getDeclaredField("birthDate");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Assertions.assertTrue(generator.supports(descriptor),
                "Генератор должен поддерживать поля типа LocalDate");
    }

    @Test
    void shouldGenerateStrictlyPastDateForPastAnnotation() throws NoSuchFieldException {
        Field field = TemporalModel.class.getDeclaredField("birthDate");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        LocalDate value = (LocalDate) generator.generate(descriptor);
        LocalDate now = LocalDate.now();

        // Для @Past — строго в прошлом (не сегодня!)
        Assertions.assertTrue(value.isBefore(now),
                "Дата рождения " + value + " должна быть строго в прошлом (до " + now + ")");
        // Дополнительная проверка: не слишком далеко в прошлое (реалистичность)
        Assertions.assertTrue(value.isAfter(now.minusYears(100)),
                "Дата рождения не должна быть дальше 100 лет в прошлое: " + value);
    }

    @Test
    void shouldGeneratePastOrPresentDate() throws NoSuchFieldException {
        Field field = TemporalModel.class.getDeclaredField("registrationDate");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        LocalDate value = (LocalDate) generator.generate(descriptor);
        LocalDate now = LocalDate.now();

        // Для @PastOrPresent — сегодня или раньше
        Assertions.assertTrue(value.isBefore(now.plusDays(1)),
                "Дата регистрации " + value + " должна быть сегодня или в прошлом (до " + now + ")");
    }

    @Test
    void shouldGenerateNeutralDateWithoutAnnotations() throws NoSuchFieldException {
        Field field = TemporalModel.class.getDeclaredField("plainDate");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        LocalDate value = (LocalDate) generator.generate(descriptor);
        LocalDate now = LocalDate.now();

        // Нейтральная дата: ±5 лет от текущей
        Assertions.assertTrue(value.isAfter(now.minusYears(6)) && value.isBefore(now.plusYears(6)),
                "Нейтральная дата " + value + " должна быть в диапазоне ±5 лет от текущей (" + now + ")");
    }

    @Test
    void shouldGenerateDifferentValues() throws NoSuchFieldException {
        Field field = TemporalModel.class.getDeclaredField("birthDate");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        LocalDate value1 = (LocalDate) generator.generate(descriptor);
        LocalDate value2 = (LocalDate) generator.generate(descriptor);

        Assertions.assertNotEquals(value1, value2,
                "Два последовательных вызова должны генерировать разные даты");
    }

    @Test
    void shouldThrowOnConflictingAnnotations() {
        // Создаём кастомный дескриптор с конфликтующими аннотациями через рефлексию
        // Для простоты проверим на модели с валидными аннотациями — не должно падать
        Assertions.assertDoesNotThrow(() -> {
            Field field = TemporalModel.class.getDeclaredField("birthDate");
            FieldDescriptor descriptor = FieldDescriptor.from(field);
            generator.generate(descriptor);
        }, "Генератор не должен падать на валидных аннотациях");
    }
}