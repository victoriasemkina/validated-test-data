package io.github.victoriasemkina.validated;

import io.github.victoriasemkina.validated.generator.DefaultGenerator;
import io.github.victoriasemkina.validated.generator.EmailGenerator;
import io.github.victoriasemkina.validated.generator.StringGenerator;
import io.github.victoriasemkina.validated.generator.ValueGenerator;
import io.github.victoriasemkina.validated.model.FieldDescriptor;
import io.github.victoriasemkina.validated.rule.RuleEngine;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Главный класс-билдер для генерации валидных объектов.
 * Использует систему правил (RuleEngine) для контекстной генерации.
 */
public class ValidatedBuilder<T> {

    private final Class<T> targetClass;
    private final List<ValueGenerator> generators;
    private final RuleEngine ruleEngine;
    private final Map<String, Object> fieldOverrides = new HashMap<>();
    private final Validator validator;
    private static final int MAX_ATTEMPTS = 10;

    private ValidatedBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.generators = List.of(
                new EmailGenerator(),
                new StringGenerator(),
                new DefaultGenerator()
        );
        this.ruleEngine = new RuleEngine(); // Инициализация движка правил

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    /**
     * Статический фабричный метод для создания билдера.
     */
    public static <T> ValidatedBuilder<T> forClass(Class<T> clazz) {
        Objects.requireNonNull(clazz, "Целевой класс не может быть null");
        return new ValidatedBuilder<>(clazz);
    }

    /**
     * Переопределяет значение для конкретного поля.
     * Пример: ValidatedBuilder.forClass(User.class)
     *          .override("firstName", "Анна")
     *          .override("lastName", "Иванова")
     *          .buildValid();
     */
    public ValidatedBuilder<T> override(String fieldName, Object value) {
        fieldOverrides.put(fieldName, value);
        return this;
    }

    /**
     * Главный метод: строит экземпляр, гарантированно проходящий валидацию.
     * Использует RuleEngine для контекстной генерации.
     */
    public T buildValid() {
        System.out.println("=== Генерация объекта класса: " + targetClass.getSimpleName() + " ===");

        List<FieldDescriptor> fields = scanFields();
        System.out.println("Найдено полей: " + fields.size());

        // Цикл попыток генерации валидного объекта
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.println("Попытка генерации #" + attempt);

            try {
                T instance = targetClass.getDeclaredConstructor().newInstance();
                Map<String, Object> generationContext = new HashMap<>(); // Контекст для правил

                // 1. Генерация значений для всех полей
                for (FieldDescriptor field : fields) {
                    Object value;

                    // A. Проверяем ручные переопределения
                    if (fieldOverrides.containsKey(field.name())) {
                        value = fieldOverrides.get(field.name());
                    }
                    // B. Пробуем применить правило (если есть контекст)
                    else if (!generationContext.isEmpty()) {
                        Optional<Object> ruleResult = ruleEngine.applyRules(field, generationContext);
                        if (ruleResult.isPresent()) {
                            value = ruleResult.get();
                            System.out.println("  [ПРАВИЛО] поле " + field.name() + " = " + value);
                        } else {
                            // C. Используем обычный генератор
                            ValueGenerator generator = findGenerator(field);
                            value = generator.generate(field);
                        }
                    }
                    // C. Для первого поля или если правил нет - обычный генератор
                    else {
                        ValueGenerator generator = findGenerator(field);
                        value = generator.generate(field);
                    }

                    // Сохраняем значение в контекст для следующих полей
                    generationContext.put(field.name(), value);

                    // Устанавливаем значение в объект
                    Field javaField = targetClass.getDeclaredField(field.name());
                    javaField.setAccessible(true);
                    javaField.set(instance, value);

                    // Логируем установленные значения (кроме тех, что уже залогированы правилами)
                    if (!fieldOverrides.containsKey(field.name()) &&
                            !ruleEngine.applyRules(field, generationContext).isPresent()) {
                        String displayValue = formatValueForDisplay(value);
                        System.out.println("  Установлено поле " + field.name() + " = " + displayValue);
                    }
                }

                // 2. ВАЛИДАЦИЯ: проверяем объект
                Set<ConstraintViolation<T>> violations = validator.validate(instance);

                if (violations.isEmpty()) {
                    System.out.println("=== Объект успешно создан и валидирован ===");
                    return instance; // Успех!
                } else {
                    // Если есть нарушения, логируем и пробуем снова
                    System.out.println("  Нарушения валидации: ");
                    for (ConstraintViolation<T> violation : violations) {
                        System.out.println("    - " + violation.getPropertyPath() + ": " + violation.getMessage());
                    }
                }

            } catch (Exception e) {
                System.err.println("Ошибка при создании объекта в попытке #" + attempt + ": " + e.getMessage());
                // Продолжаем попытки
            }
        }

        // Если все попытки исчерпаны
        throw new IllegalStateException("Не удалось сгенерировать валидный объект класса " +
                targetClass.getName() + " после " + MAX_ATTEMPTS + " попыток");
    }

    /**
     * Сканирует поля класса и создаёт их описания.
     */
    private List<FieldDescriptor> scanFields() {
        List<FieldDescriptor> descriptors = new ArrayList<>();
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            descriptors.add(FieldDescriptor.from(field));
        }

        return descriptors;
    }

    /**
     * Находит подходящий генератор для поля.
     */
    private ValueGenerator findGenerator(FieldDescriptor descriptor) {
        return generators.stream()
                .filter(g -> g.supports(descriptor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No generator found for field: " + descriptor.name()
                ));
    }

    /**
     * Форматирует значение для отображения в логах.
     */
    private String formatValueForDisplay(Object value) {
        if (value == null) {
            return "null";
        }

        String strValue = String.valueOf(value);

        // Сокращаем длинные строки для удобства чтения
        if (strValue.length() > 50) {
            return strValue.substring(0, 47) + "...";
        }

        return strValue;
    }
}