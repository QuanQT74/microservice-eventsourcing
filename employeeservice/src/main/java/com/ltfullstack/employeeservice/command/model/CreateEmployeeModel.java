package com.ltfullstack.employeeservice.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeModel {
    @NotBlank(message = "First name is madatory")
    private String firstName;

    @NotBlank(message = "Last name is madatory")
    private String lastName;

    @NotBlank(message = "Kin is madatory")
    private String Kin;

}
