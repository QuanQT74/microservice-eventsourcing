package com.ltfullstack.employeeservice.command.controller;

import com.ltfullstack.employeeservice.command.command.CreateEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.DeleteEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.UpdateEmployeeCommand;
import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import com.ltfullstack.employeeservice.command.model.CreateEmployeeModel;
import com.ltfullstack.employeeservice.command.model.UpdateEmployeeModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/employees")
public class EmployeeCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public String addEmployees(@RequestBody CreateEmployeeModel employeeModel) {
        String employeeId = UUID.randomUUID().toString();
        log.info("Creating employee with ID: {}", employeeId);

        // Save directly to DB first (workaround for Axon event handler not working)
        Employee employee = Employee.builder()
                .id(employeeId)
                .firstName(employeeModel.getFirstName())
                .lastName(employeeModel.getLastName())
                .Kin(employeeModel.getKin())
                .isDisciplined(false)
                .build();
        employeeRepository.save(employee);
        log.info("Employee saved to DB: {}", employeeId);

        // Also send command for event sourcing
        CreateEmployeeCommand command = new CreateEmployeeCommand(
                employeeId,
                employeeModel.getFirstName(),
                employeeModel.getLastName(),
                employeeModel.getKin(),
                false);

        commandGateway.sendAndWait(command);
        log.info("Employee command sent successfully: {}", employeeId);
        return employeeId;
    }

    @PutMapping("/{employeeId}")
    public String updateEmployees(@RequestBody UpdateEmployeeModel employeeModel, @PathVariable String employeeId) {
        // Update directly in DB
        employeeRepository.findById(employeeId).ifPresent(employee -> {
            employee.setFirstName(employeeModel.getFirstName());
            employee.setLastName(employeeModel.getLastName());
            employee.setKin(employeeModel.getKin());
            employee.setIsDisciplined(employeeModel.getIsDisciplined());
            employeeRepository.save(employee);
            log.info("Employee updated in DB: {}", employeeId);
        });

        // Send command for event sourcing
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
        // Delete directly from DB
        employeeRepository.deleteById(employeeId);
        log.info("Employee deleted from DB: {}", employeeId);

        // Send command for event sourcing
        DeleteEmployeeCommand deleteEmployeeCommand = new DeleteEmployeeCommand(employeeId);
        return commandGateway.sendAndWait(deleteEmployeeCommand);
    }
}
