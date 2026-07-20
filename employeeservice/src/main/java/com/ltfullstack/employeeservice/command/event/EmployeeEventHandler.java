package com.ltfullstack.employeeservice.command.event;

import com.ltfullstack.commonservice.event.BorrowingCreatedEvent;
import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeEventHandler {
    @Autowired
    private EmployeeRepository employeeRepository;

    @EventHandler
    public void on(EmployeeCreatedEvent employeeCreatedEvent){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeCreatedEvent, employee);
        employeeRepository.save(employee);
        log.info("Employee created: {}", employeeCreatedEvent.getId());
    }

    @EventHandler
    public void on(EmployeeUpdatedEvent employeeUpdatedEvent){
        employeeRepository.findById(employeeUpdatedEvent.getId())
            .ifPresentOrElse(employee -> {
                employee.setIsDisciplined(employeeUpdatedEvent.getIsDisciplined());
                employee.setKin(employeeUpdatedEvent.getKin());
                employee.setFirstName(employeeUpdatedEvent.getFirstName());
                employee.setLastName(employeeUpdatedEvent.getLastName());
                employeeRepository.save(employee);
                log.info("Employee updated: {}", employeeUpdatedEvent.getId());
            }, () -> log.warn("Employee not found: {}", employeeUpdatedEvent.getId()));
    }

    @EventHandler
    @DisallowReplay
    public void on(EmployeeDeleteEvent employeeDeleteEvent){
        try {
            if (!employeeRepository.existsById(employeeDeleteEvent.getId())) {
                log.warn("Employee not found: {}", employeeDeleteEvent.getId());
                return;
            }
            employeeRepository.deleteById(employeeDeleteEvent.getId());
            log.info("Employee deleted: {}", employeeDeleteEvent.getId());
        }catch (Exception ex){
            log.error("Error deleting employee: {}", ex.getMessage());
        }
    }

    @EventHandler
    public void on(BorrowingCreatedEvent event){
        log.info("BorrowingCreatedEvent received: borrowingId={}, employeeId={}", event.getId(), event.getEmployeeId());
    }
}
