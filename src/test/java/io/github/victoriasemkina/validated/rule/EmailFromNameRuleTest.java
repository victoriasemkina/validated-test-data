package io.github.victoriasemkina.validated.rule;

import io.github.victoriasemkina.validated.model.FieldDescriptor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

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

        Assertions.assertTrue(rule.matches(descriptor, context));
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
        Assertions.assertEquals("john.doe@company.com", result);
    }

    @Test
    void shouldNotMatchWithoutNamesInContext() throws Exception {
        EmailFromNameRule rule = new EmailFromNameRule();
        Field field = TestUser.class.getDeclaredField("workEmail");
        FieldDescriptor descriptor = FieldDescriptor.from(field);

        Map<String, Object> emptyContext = new HashMap<>();

        Assertions.assertFalse(rule.matches(descriptor, emptyContext));
    }
}