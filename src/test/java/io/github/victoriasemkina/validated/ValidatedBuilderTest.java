package io.github.victoriasemkina.validated;

import io.github.victoriasemkina.validated.testmodel.ComplexModel;
import io.github.victoriasemkina.validated.testmodel.NotNullModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class ValidatedBuilderTest {

    // Простой тестовый класс
    static class TestPerson {
        private String name;
        private String email;
        private int age;

        public TestPerson() {}

        // Геттеры для проверки
        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }
    }

    @Test
    void shouldCreateInstanceOfClass() {
        // When
        TestPerson person = ValidatedBuilder.forClass(TestPerson.class)
                .buildValid();

        // Then
        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getName());
        Assertions.assertNotNull(person.getEmail());
        Assertions.assertTrue(person.getAge() >= 0);
    }

    @Test
    void shouldGenerateValidEmail() {
        // When
        TestPerson person = ValidatedBuilder.forClass(TestPerson.class)
                .buildValid();

        // Then
        String email = person.getEmail();
        Assertions.assertTrue(email.contains("@"),
                "Email должен содержать '@', а получили: " + email);
        Assertions.assertTrue(email.contains("."),
                "Email должен содержать '.', а получили: " + email);
    }

    @Test
    void shouldGenerateDifferentNames() {
        // When
        TestPerson person1 = ValidatedBuilder.forClass(TestPerson.class)
                .buildValid();
        TestPerson person2 = ValidatedBuilder.forClass(TestPerson.class)
                .buildValid();

        // Then
        Assertions.assertNotEquals(person1.getName(), person2.getName(),
                "Два сгенерированных имени не должны совпадать");
    }

    @Test
    void shouldRespectNotNullAnnotation() {
        System.out.println("=== Тест @NotNull (с валидацией) ===");

        for (int i = 0; i < 3; i++) {
            NotNullModel model = ValidatedBuilder.forClass(NotNullModel.class)
                    .buildValid();

            System.out.println("Создан объект: mandatoryField = '" +
                    model.getMandatoryField() + "'");

            Assertions.assertNotNull(model.getMandatoryField(),
                    "Поле с @NotNull не должно быть null. Попытка: " + (i + 1));

            // Дополнительная проверка: строка не должна быть пустой
            // (хотя @NotNull этого не требует, но генератор строк даёт не пустые значения)
            Assertions.assertFalse(model.getMandatoryField().isEmpty());
        }
    }

    @Test
    void shouldGenerateValidObjectForComplexClass() {
        System.out.println("=== Тест ComplexModel ===");
        ComplexModel model = ValidatedBuilder.forClass(ComplexModel.class)
                .buildValid();

        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getName());

        // Проверяем @Size
        String desc = model.getDescription();
        Assertions.assertNotNull(desc);
        Assertions.assertTrue(desc.length() >= 10 && desc.length() <= 100,
                "Длина description должна быть 10-100, а получили: " + desc.length());

        // Проверяем @Email
        String email = model.getEmail();
        Assertions.assertNotNull(email);
        Assertions.assertTrue(email.contains("@"),
                "Email должен содержать '@', а получили: " + email);
    }
}