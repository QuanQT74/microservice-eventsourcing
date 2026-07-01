package com.ltfullstack.employeeservice.command.command;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeCommand {

    @TargetAggregateIdentifier

    private String id;

    private String firstName;

    private String lastName;

    private String Kin;

    private Boolean isDisciplined;

}
