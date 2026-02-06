package io.github.victoriasemkina.validated.generator.primitive;

import io.github.victoriasemkina.validated.core.ValueGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Random;

public final class BigDecimalGenerator implements ValueGenerator {

    private static final Random RANDOM = new Random();
    private static final int DEFAULT_SCALE = 2;
    private static final BigDecimal DEFAULT_MIN = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_MAX = new BigDecimal("9999999999.99");

    @Override
    public boolean supports(FieldDescriptor descriptor) {
        return descriptor.type().equals(BigDecimal.class);
    }

    @Override
    public Object generate(FieldDescriptor descriptor) {
        BigDecimal minValue = getDecimalMin(descriptor);
        BigDecimal maxValue = getDecimalMax(descriptor);
        int scale = getScaleFromDigits(descriptor);

        return generateInRange(minValue, maxValue, scale);
    }

    private BigDecimal generateInRange(BigDecimal min, BigDecimal max, int scale) {
        // Обработка дефолтных значений
        if (min == null) min = DEFAULT_MIN;
        if (max == null) max = DEFAULT_MAX;

        // Защита от некорректных диапазонов
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "Некорректный диапазон: min=" + min + " > max=" + max
            );
        }

        // Генерация случайного значения в диапазоне [min, max)
        BigDecimal range = max.subtract(min);
        BigDecimal randomFraction = new BigDecimal(RANDOM.nextDouble());
        BigDecimal randomValue = randomFraction.multiply(range).setScale(scale, RoundingMode.HALF_UP);

        return min.add(randomValue).setScale(scale, RoundingMode.HALF_UP);
    }

    private BigDecimal getDecimalMin(FieldDescriptor descriptor) {
        Optional<DecimalMin> annotation = descriptor.findConstraint(DecimalMin.class);
        return annotation.map(ann -> new BigDecimal(ann.value())).orElse(null);
    }

    private BigDecimal getDecimalMax(FieldDescriptor descriptor) {
        Optional<DecimalMax> annotation = descriptor.findConstraint(DecimalMax.class);
        return annotation.map(ann -> new BigDecimal(ann.value())).orElse(null);
    }

    private int getScaleFromDigits(FieldDescriptor descriptor) {
        Optional<Digits> annotation = descriptor.findConstraint(Digits.class);
        return annotation.map(Digits::fraction).orElse(DEFAULT_SCALE);
    }
}
