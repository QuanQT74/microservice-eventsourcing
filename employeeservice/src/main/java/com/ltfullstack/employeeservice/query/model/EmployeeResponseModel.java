package com.ltfullstack.employeeservice.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeResponseModel {

    private String id;

    private String firstName;

    private String lastName;

    private String Kin;

    private Boolean isDisciplined;
}
