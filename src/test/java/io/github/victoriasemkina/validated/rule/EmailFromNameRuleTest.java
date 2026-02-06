package io.github.victoriasemkina.validated.rule;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class EmailFromNameRuleTest {

    // Тестовая модель
    static class TestUser {
        @jakarta.validation.constraints.Email(regexp = ".+@company\\.com")
        private String workEmail;

        @jakarta.validation.constraints.Email // Без regexp
        private String personalEmail;
    }

    @Test
    void shouldMatchWhenCompanyEmailAndNamesInContext() throws Exception {
        EmailFromNameRule rule = new EmailFromNameRule();
        Field field = TestUser.class.getDeclaredField("workEmail");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Map<String, Object> context = new HashMap<>();
        context.put("firstName", "John");
        context.put("lastName", "Doe");

        Assertions.assertTrue(rule.matches(descriptor, context),
                "Правило должно срабатывать для корпоративного email с именем в контексте");
    }

    @Test
    void shouldGenerateCorrectEmail() throws Exception {
        EmailFromNameRule rule = new EmailFromNameRule();
        Field field = TestUser.class.getDeclaredField("workEmail");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Map<String, Object> context = new HashMap<>();
        context.put("firstName", "John");
        context.put("lastName", "Doe");

        Object result = rule.generate(descriptor, context);
        Assertions.assertEquals("john.doe@company.com", result, // ← С ТОЧКОЙ!
                "Ожидался email 'john.doe@company.com' с точкой как разделителем");
    }

    @Test
    void shouldNotMatchWithoutNamesInContext() throws Exception {
        EmailFromNameRule rule = new EmailFromNameRule();
        Field field = TestUser.class.getDeclaredField("workEmail");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Map<String, Object> emptyContext = new HashMap<>();

        Assertions.assertFalse(rule.matches(descriptor, emptyContext),
                "Правило не должно срабатывать без имени в контексте");
    }

    @Test
    void shouldGenerateEmailFromFirstNameOnly() throws Exception {
        EmailFromNameRule rule = new EmailFromNameRule();
        Field field = TestUser.class.getDeclaredField("workEmail");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Map<String, Object> context = new HashMap<>();
        context.put("firstName", "Anna");

        Object result = rule.generate(descriptor, context);
        Assertions.assertEquals("anna@company.com", result);
    }

    @Test
    void shouldNormalizeSpecialCharacters() throws Exception {
        EmailFromNameRule rule = new EmailFromNameRule();
        Field field = TestUser.class.getDeclaredField("workEmail");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Map<String, Object> context = new HashMap<>();
        context.put("firstName", "John-Doe!");
        context.put("lastName", "Smith_123");

        Object result = rule.generate(descriptor, context);
        // "John-Doe!" → "johndoe", "Smith_123" → "smith123" → "johndoe.smith123@company.com"
        Assertions.assertEquals("johndoe.smith123@company.com", result); // ← С ТОЧКОЙ!
    }
}