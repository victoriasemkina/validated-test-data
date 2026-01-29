package io.github.victoriasemkina.validated.generator;

import io.github.victoriasemkina.validated.model.FieldDescriptor;

/**
 * Запечатанный интерфейс для всех генераторов значений.
 * Гарантирует, что только разрешённые классы могут его реализовывать.
 */
public sealed interface ValueGenerator
        permits EmailGenerator, StringGenerator, DefaultGenerator {

    /**
     * Может ли этот генератор создать значение для данного поля?
     */
    boolean supports(FieldDescriptor descriptor);

    /**
     * Сгенерировать значение для поля.
     */
    Object generate(FieldDescriptor descriptor);
}
