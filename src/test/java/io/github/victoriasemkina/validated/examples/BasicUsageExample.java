package io.github.victoriasemkina.validated.examples;

import io.github.victoriasemkina.validated.core.ValidatedBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Простой пример использования библиотеки Validated Test Data.
 * Запустите main-метод, чтобы увидеть работу библиотеки.
 */
public class BasicUsageExample {
    // Модель пользователя из README
    public static class User {
        @NotNull
        @Size(min = 2, max = 20)  // Разумные ограничения для имён
        private String firstName;

        @NotNull
        @Size(min = 2, max = 25)  // Разумные ограничения для фамилий
        private String lastName;

        @Email(regexp = ".+@company\\.com")
        private String corporateEmail;

        public User() {}

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getCorporateEmail() {
            return corporateEmail;
        }

        @Override
        public String toString() {
            return String.format("User[firstName='%s', lastName='%s', email='%s']",
                    firstName, lastName, corporateEmail);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Пример использования Validated Test Data ===\n");

        // 1. Базовое использование
        System.out.println("1. Базовая генерация валидного пользователя:");
        User user = ValidatedBuilder.forClass(User.class).buildValid();
        System.out.println("   " + user);

        // 2. Генерация нескольких экземпляров
        System.out.println("\n2. Генерация 3 пользователей:");
        for (int i = 1; i <= 3; i++) {
            User u = ValidatedBuilder.forClass(User.class).buildValid();
            System.out.println("   " + i + ". " + u.getFirstName() + " " +
                    u.getLastName() + " <" + u.getCorporateEmail() + ">");
        }

        System.out.println("\n=== Пример завершён ===");
    }
}
