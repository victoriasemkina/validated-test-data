// src/test/java/io/github/victoriasemkina/validated/generator/semantic/ValidatedBuilderContextTest.java

package io.github.victoriasemkina.validated.generator.semantic;

import io.github.victoriasemkina.validated.core.ValidatedBuilder;
import io.github.victoriasemkina.validated.testmodel.semantic.EmployeeModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class ValidatedBuilderContextTest {

    @Test
    void shouldGenerateContextualEmail() {
        EmployeeModel emp = ValidatedBuilder.forClass(EmployeeModel.class)
                .buildValid();

        Assertions.assertNotNull(emp.getName(), "Имя не должно быть null");
        Assertions.assertNotNull(emp.getEmail(), "Email не должен быть null");
        Assertions.assertNotNull(emp.getDepartment(), "Department не должен быть null");

        // Проверяем формат корпоративного email
        Assertions.assertTrue(emp.getEmail().endsWith("@company.com"),
                "Email '" + emp.getEmail() + "' должен заканчиваться на '@company.com'");
    }

    @Test
    void shouldUseOverrideValues() {
        // ИСПОЛЬЗУЕМ ЛАТИНИЦУ для корректной генерации email
        EmployeeModel emp = ValidatedBuilder.forClass(EmployeeModel.class)
                .override("name", "Anna")  // ← латиница вместо кириллицы "Анна"
                .override("department", "IT")
                .buildValid();

        Assertions.assertEquals("Anna", emp.getName());
        Assertions.assertEquals("IT", emp.getDepartment());

        // Проверяем, что email содержит нормализованное имя "anna"
        Assertions.assertTrue(emp.getEmail().contains("anna"),
                "Email '" + emp.getEmail() + "' должен содержать 'anna'");
        Assertions.assertTrue(emp.getEmail().endsWith("@company.com"));
    }
}