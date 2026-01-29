package io.github.victoriasemkina.validated;

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
}