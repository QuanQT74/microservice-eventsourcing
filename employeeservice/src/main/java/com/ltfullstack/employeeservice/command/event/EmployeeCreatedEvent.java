package com.ltfullstack.employeeservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatedEvent {
    private  String id;

    private String firstName;

    private String lastName;

    private String Kin;

    private Boolean isDisciplined;

    private String memberCode;
}
