package com.ltfullstack.commonservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseCommandModel {

    private String id;

    private String firstName;

    private String lastName;

    private String Kin;

    private Boolean isDisciplined;
}
