package io.github.victoriasemkina.validated.testmodel;

import jakarta.validation.constraints.NotNull;

public class NotNullModel {
    @NotNull
    private String mandatoryField;

    private String optionalField;

    // Конструктор по умолчанию
    public NotNullModel() {}

    public String getMandatoryField() { return mandatoryField; }
    public String getOptionalField() { return optionalField; }
}
