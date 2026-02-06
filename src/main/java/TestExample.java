import io.github.victoriasemkina.validated.core.ValidatedBuilder;

public class TestExample {
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private boolean active;
    private double salary;

    public TestExample() {}

    // Геттеры
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public boolean isActive() { return active; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return String.format(
                "TestExample[firstName=%s, lastName=%s, email=%s, age=%d, active=%s, salary=%.2f]",
                firstName, lastName, email, age, active, salary
        );
    }

    public static void main(String[] args) {
        System.out.println("=== Тестируем ValidatedBuilder с DataFaker ===\n");

        TestExample example = ValidatedBuilder.forClass(TestExample.class)
                .buildValid();

        System.out.println("\n🎉 Результат: " + example);
    }
}