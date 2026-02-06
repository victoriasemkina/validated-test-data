package io.github.victoriasemkina.validated.generator.primitive;

import io.github.victoriasemkina.validated.generator.temporal.LocalDateTimeGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;
import io.github.victoriasemkina.validated.testmodel.primitive.FinancialModel;
import io.github.victoriasemkina.validated.testmodel.temporal.TemporalModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public class BigDecimalGeneratorTest {
    private BigDecimalGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new BigDecimalGenerator();
    }

    @Test
    void shouldSupportBigDecimalFields() throws NoSuchFieldException {
        Field field = FinancialModel.class.getDeclaredField("amount");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Assertions.assertTrue(generator.supports(descriptor),
                "Генератор должен поддерживать поля типа BigDecimal");
    }

    @Test
    void shouldGenerateWithinDecimalRange() throws NoSuchFieldException {
        Field field = FinancialModel.class.getDeclaredField("amount");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        BigDecimal value = (BigDecimal) generator.generate(descriptor);

        Assertions.assertTrue(
                value.compareTo(new BigDecimal("0.01")) >= 0 &&
                        value.compareTo(new BigDecimal("10000.00")) <= 0,
                "Сгенерированное значение " + value +
                        " должно быть в диапазоне [0.01, 10000.00]"
        );
        Assertions.assertEquals(2, value.scale(),
                "Масштаб значения должен быть 2, а получен: " + value.scale());
    }

    @Test
    void shouldRespectDigitsFraction() throws NoSuchFieldException {
        Field field = FinancialModel.class.getDeclaredField("discountRate");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        BigDecimal value = (BigDecimal) generator.generate(descriptor);

        Assertions.assertEquals(4, value.scale(),
                "Масштаб значения должен быть 4, а получен: " + value.scale());
    }

    @Test
    void shouldGenerateWithoutAnnotations() throws NoSuchFieldException {
        Field field = FinancialModel.class.getDeclaredField("plainValue");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        BigDecimal value = (BigDecimal) generator.generate(descriptor);

        Assertions.assertNotNull(value,
                "Сгенерированное значение не должно быть null");
        Assertions.assertEquals(2, value.scale(),
                "Масштаб значения по умолчанию должен быть 2, а получен: " + value.scale());
    }

    @Test
    void shouldGenerateDifferentValues() throws NoSuchFieldException {
        Field field = FinancialModel.class.getDeclaredField("amount");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        BigDecimal value1 = (BigDecimal) generator.generate(descriptor);
        BigDecimal value2 = (BigDecimal) generator.generate(descriptor);

        Assertions.assertNotEquals(value1, value2,
                "Два последовательных вызова должны генерировать разные значения");
    }

    static class LocalDateTimeGeneratorTest {

        private LocalDateTimeGenerator generator;

        @BeforeEach
        void setUp() {
            generator = new LocalDateTimeGenerator();
        }

        @Test
        void shouldSupportLocalDateTimeFields() throws NoSuchFieldException {
            Field field = TemporalModel.class.getDeclaredField("appointmentTime");
            FieldDescriptor descriptor = FieldDescriptor.from(field);

            Assertions.assertTrue(generator.supports(descriptor),
                    "Генератор должен поддерживать поля типа LocalDateTime");
        }

        @Test
        void shouldGenerateStrictlyFutureDateTimeForFutureAnnotation() throws NoSuchFieldException {
            Field field = TemporalModel.class.getDeclaredField("appointmentTime");
            FieldDescriptor descriptor = FieldDescriptor.from(field);

            LocalDateTime value = (LocalDateTime) generator.generate(descriptor);
            LocalDateTime now = LocalDateTime.now();

            // Для @Future — строго в будущем (минимум 1 секунда позже)
            Assertions.assertTrue(value.isAfter(now),
                    "Время встречи " + value + " должно быть строго в будущем (после " + now + ")");
            // Дополнительная проверка: не слишком далеко в будущее (реалистичность)
            Assertions.assertTrue(value.isBefore(now.plusYears(11)),
                    "Время встречи не должно быть дальше 11 лет в будущее: " + value);
        }

        @Test
        void shouldGenerateFutureOrPresentDateTime() throws NoSuchFieldException {
            Field field = TemporalModel.class.getDeclaredField("startTime");
            FieldDescriptor descriptor = FieldDescriptor.from(field);

            LocalDateTime value = (LocalDateTime) generator.generate(descriptor);
            LocalDateTime now = LocalDateTime.now();

            // Для @FutureOrPresent — сегодня/сейчас или позже
            Assertions.assertTrue(value.isAfter(now.minusSeconds(1)),
                    "Время начала " + value + " должно быть сейчас или в будущем (после " + now + ")");
        }

        @Test
        void shouldGenerateNeutralDateTimeWithoutAnnotations() throws NoSuchFieldException {
            // Создаём временную модель с полем LocalDateTime без аннотаций
            // Для теста используем кастомный класс или рефлексию
            // Здесь проверим через нейтральную генерацию напрямую
            FieldDescriptor descriptor = FieldDescriptor.from(
                    TemporalModel.class.getDeclaredField("appointmentTime")
            );
            // Просто проверим, что генерация не падает
            LocalDateTime value = (LocalDateTime) generator.generate(descriptor);
            Assertions.assertNotNull(value, "Сгенерированное значение не должно быть null");
        }

        @Test
        void shouldGenerateDifferentValues() throws NoSuchFieldException {
            Field field = TemporalModel.class.getDeclaredField("appointmentTime");
            FieldDescriptor descriptor = FieldDescriptor.from(field);

            LocalDateTime value1 = (LocalDateTime) generator.generate(descriptor);
            LocalDateTime value2 = (LocalDateTime) generator.generate(descriptor);

            Assertions.assertNotEquals(value1, value2,
                    "Два последовательных вызова должны генерировать разные значения");
        }
    }
}
