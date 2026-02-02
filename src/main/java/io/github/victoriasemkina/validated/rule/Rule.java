package io.github.victoriasemkina.validated.rule;

import io.github.victoriasemkina.validated.model.FieldDescriptor;

import java.util.Map;

/**
 * Интерфейс для правил генерации, которые могут использовать значения
 * уже сгенерированных полей (контекст).
 */
public interface Rule {

    /**
     * Подходит ли это правило для данного поля в текущем контексте?
     *
     * @param targetField поле, для которого нужно сгенерировать значение
     * @param context мапа уже сгенерированных значений (имя поля → значение)
     * @return true, если правило применимо
     */
    boolean matches(FieldDescriptor targetField, Map<String, Object> context);

    /**
     * Сгенерировать значение с учётом контекста.
     * Вызывается только если matches() вернул true.
     */
    Object generate(FieldDescriptor targetField, Map<String, Object> context);

    /**
     * Приоритет правила (чем выше, тем раньше применяется).
     * По умолчанию 0.
     */
    default int getPriority() {
        return 0;
    }
}
