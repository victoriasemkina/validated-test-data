package io.github.victoriasemkina.validated;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatedBuilderContextTest {

    static class Employee {
        private String firstName;
        private String lastName;

        @jakarta.validation.constraints.Email(regexp = ".+@company\\.com")
        private String workEmail;

        public Employee() {}

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getWorkEmail() { return workEmail; }
    }

    @Test
    void shouldGenerateContextualEmail() {
        System.out.println("\n=== Тест контекстной генерации email ===");

        Employee employee = ValidatedBuilder.forClass(Employee.class).buildValid();

        assertNotNull(employee);
        assertNotNull(employee.getFirstName());
        assertNotNull(employee.getLastName());
        assertNotNull(employee.getWorkEmail());

        // Проверяем, что email соответствует паттерну firstName.lastName@company.com
        String expectedEmailPattern =
                employee.getFirstName().toLowerCase() + "." +
                        employee.getLastName().toLowerCase() + "@company.com";

        assertEquals(expectedEmailPattern, employee.getWorkEmail().toLowerCase(),
                "Email должен быть сгенерирован на основе имени и фамилии");

        System.out.println("Сгенерирован: " + employee.getFirstName() + " " +
                employee.getLastName() + " <" + employee.getWorkEmail() + ">");
    }

    @Test
    void shouldUseOverrideValues() {
        System.out.println("\n=== Тест override() метода ===");

        Employee employee = ValidatedBuilder.forClass(Employee.class)
                .override("firstName", "Анна")
                .override("lastName", "Иванова")
                .buildValid();

        assertEquals("Анна", employee.getFirstName());
        assertEquals("Иванова", employee.getLastName());
        assertEquals("анна.иванова@company.com", employee.getWorkEmail().toLowerCase());
    }
}