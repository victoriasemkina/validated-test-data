package io.github.victoriasemkina.validated.rule;

import io.github.victoriasemkina.validated.model.FieldDescriptor;

import java.util.*;

/**
 * Движок правил, который применяет зарегистрированные правила
 * в порядке их приоритета.
 */
public class RuleEngine {
    private final List<Rule> rules = new ArrayList<>();

    public RuleEngine() {
        // Регистрируем правила по умолчанию
        registerDefaultRules();
    }

    private void registerDefaultRules() {
        registerRule(new EmailFromNameRule());
        // Здесь позже добавятся другие правила по умолчанию
    }

    /**
     * Регистрирует новое правило.
     */
    public void registerRule(Rule rule) {
        rules.add(rule);
        sortRulesByPriority();
    }

    /**
     * Регистрирует несколько правил.
     */
    public void registerRules(Rule... rulesToAdd) {
        Collections.addAll(rules, rulesToAdd);
        sortRulesByPriority();
    }

    private void sortRulesByPriority() {
        // Сортируем по убыванию приоритета (высокий приоритет → первый)
        rules.sort((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()));
    }

    /**
     * Пытается применить зарегистрированные правила к полю.
     *
     * @param field поле, для которого нужно сгенерировать значение
     * @param context контекст (уже сгенерированные значения других полей)
     * @return Optional со значением, если подходящее правило нашлось,
     *         иначе пустой Optional
     */
    public Optional<Object> applyRules(FieldDescriptor field, Map<String, Object> context) {
        for (Rule rule : rules) {
            if (rule.matches(field, context)) {
                return Optional.of(rule.generate(field, context));
            }
        }
        return Optional.empty();
    }

    /**
     * Возвращает количество зарегистрированных правил.
     */
    public int getRuleCount() {
        return rules.size();
    }
}