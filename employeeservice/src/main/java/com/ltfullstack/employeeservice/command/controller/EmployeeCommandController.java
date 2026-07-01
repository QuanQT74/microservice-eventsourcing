package com.ltfullstack.employeeservice.command.controller;

import com.ltfullstack.employeeservice.command.command.CreateEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.DeleteEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.UpdateEmployeeCommand;
import com.ltfullstack.employeeservice.command.model.CreateEmployeeModel;
import com.ltfullstack.employeeservice.command.model.UpdateEmployeeModel;
import jakarta.validation.Valid;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/employees")
public class EmployeeCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addEmployees(@Valid @RequestBody CreateEmployeeModel employeeModel) {
        CreateEmployeeCommand command = new CreateEmployeeCommand(
                UUID.randomUUID().toString(),
                employeeModel.getFirstName(),
                employeeModel.getLastName(),
                employeeModel.getKin(),
                false);
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{employeeId}")
    public String updateEmployees(@RequestBody UpdateEmployeeModel employeeModel, @PathVariable String employeeId) {
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(
                employeeId,
                employeeModel.getFirstName(),
                employeeModel.getLastName(),
                employeeModel.getKin(),
                employeeModel.getIsDisciplined());
        return commandGateway.sendAndWait(command);
    }

    @DeleteMapping("/{employeeId}")
    public String deleteEmployees(@PathVariable String employeeId) {
        DeleteEmployeeCommand deleteEmployeeCommand = new DeleteEmployeeCommand(employeeId);
        return commandGateway.sendAndWait(deleteEmployeeCommand);
    }
}