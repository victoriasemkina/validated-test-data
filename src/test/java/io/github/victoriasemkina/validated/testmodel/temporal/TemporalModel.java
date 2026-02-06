package io.github.victoriasemkina.validated.testmodel.temporal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Тестовая модель для проверки генерации дат и времени.
 */
public class TemporalModel {

    @Past
    private LocalDate birthDate;

    @Future
    private LocalDateTime appointmentTime;

    @PastOrPresent
    private LocalDate registrationDate;

    @FutureOrPresent
    private LocalDateTime startTime;

    // Поле без аннотаций — для проверки дефолтного поведения
    private LocalDate plainDate;

    public TemporalModel() {}

    public @Past LocalDate getBirthDate() { return birthDate; }
    public @Future LocalDateTime getAppointmentTime() { return appointmentTime; }
    public @PastOrPresent LocalDate getRegistrationDate() { return registrationDate; }
    public @FutureOrPresent LocalDateTime getStartTime() { return startTime; }
    public LocalDate getPlainDate() { return plainDate; }
}