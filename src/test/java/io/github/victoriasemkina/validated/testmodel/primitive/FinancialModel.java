package io.github.victoriasemkina.validated.testmodel.primitive;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

/**
 * Тестовая модель для проверки генерации финансовых данных.
 */
public class FinancialModel {

    @DecimalMin("0.01")
    @DecimalMax("10000.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Digits(integer = 3, fraction = 4)
    private BigDecimal discountRate;

    // Поле без аннотаций — для проверки дефолтного поведения
    private BigDecimal plainValue;

    public FinancialModel() {}

    public @DecimalMin("0.01") @DecimalMax("10000.00") @Digits(integer = 10, fraction = 2) BigDecimal getAmount() {
        return amount;
    }

    public @DecimalMin("0.00") @DecimalMax("100.00") @Digits(integer = 3, fraction = 4) BigDecimal getDiscountRate() {
        return discountRate;
    }

    public BigDecimal getPlainValue() {
        return plainValue;
    }
}
