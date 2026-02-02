package io.github.victoriasemkina.validated.testmodel;

import jakarta.validation.constraints.*;

public class ComplexModel {
    @NotNull
    private String name;

    @Size(min = 10, max = 100)
    private String description;

    @Email
    private String email;

    private int count;

    public ComplexModel() {}

    public @NotNull String getName() {  return name;}

    public @Size(min = 10, max = 100) String getDescription() { return description;}

    public @Email String getEmail() {   return email;}

    public int getCount() { return count;}
}
