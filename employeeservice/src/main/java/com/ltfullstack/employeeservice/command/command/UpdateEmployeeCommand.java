package com.ltfullstack.employeeservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeCommand {
    @TargetAggregateIdentifier
    private String id;

    private String firstName;

    private String lastName;

    private String Kin;

    private Boolean isDisciplined;

}
