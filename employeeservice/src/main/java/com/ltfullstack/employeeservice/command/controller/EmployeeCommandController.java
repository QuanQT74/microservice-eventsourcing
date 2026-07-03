package com.ltfullstack.employeeservice.command.controller;

import com.ltfullstack.employeeservice.command.command.CreateEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.DeleteEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.UpdateEmployeeCommand;
import com.ltfullstack.employeeservice.command.model.CreateEmployeeModel;
import com.ltfullstack.employeeservice.command.model.UpdateEmployeeModel;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/employees")
@Tag(name = "Employee Command")
public class EmployeeCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @Operation(
            summary = "Create a new employee",
            description = "Create a new employee and publish an EmployeeCreatedEvent to Axon Server.",
            responses = {
                    @ApiResponse(
                            description = "Employee created successfully",

                            responseCode = "200"),
                    @ApiResponse(
                            responseCode = "400",

                            description = "Invalid employee data"),
                    @ApiResponse(
                            responseCode = "500",

                            description = "Internal server error")
            }
    )
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
    @Operation(
            summary = "Update an existing employee",
            description = "Update employee information by employee ID and publish an EmployeeUpdatedEvent.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",

                            description = "Employee updated successfully"),
                    @ApiResponse(
                            responseCode = "400",

                            description = "Invalid request"),
                    @ApiResponse(
                            responseCode = "404",

                            description = "Employee not found"),
                    @ApiResponse(
                            responseCode = "500",

                            description = "Internal server error")
            }
    )
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

    @Operation(
            summary = "Delete an employee",
            description = "Delete an employee by employee ID and publish an EmployeeDeletedEvent.",
            tags = {"Employee Command"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Employee deleted successfully"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error")
            }
    )
    @DeleteMapping("/{employeeId}")
    public String deleteEmployees(@PathVariable String employeeId) {
        DeleteEmployeeCommand deleteEmployeeCommand = new DeleteEmployeeCommand(employeeId);
        return commandGateway.sendAndWait(deleteEmployeeCommand);
    }
}