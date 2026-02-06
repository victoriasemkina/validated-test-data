package io.github.victoriasemkina.validated.core;
import io.github.victoriasemkina.validated.model.FieldDescriptor;

/**
 * Интерфейс для генераторов значений.
 *
 * <p>Реализации группируются по типам данных:
 * <ul>
 *   <li>{@code generator.primitive} — примитивы и простые типы</li>
 *   <li>{@code generator.temporal} — даты и время</li>
 *   <li>{@code generator.semantic} — семантические типы (email)</li>
 * </ul>
 */
public interface ValueGenerator {
    boolean supports(FieldDescriptor descriptor);
    Object generate(FieldDescriptor descriptor);
}