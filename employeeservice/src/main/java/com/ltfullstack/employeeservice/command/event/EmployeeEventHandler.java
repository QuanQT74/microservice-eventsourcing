package com.ltfullstack.employeeservice.command.event;

import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import jakarta.ws.rs.NotFoundException;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventHandler {
    @Autowired
    private EmployeeRepository employeeRepository;

    @EventHandler
    public void on(EmployeeCreatedEvent employeeCreatedEvent){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeCreatedEvent,employee);
        employeeRepository.save(employee);
    }
    @EventHandler void on(EmployeeUpdatedEvent employeeUpdatedEvent){
       Employee employee =  employeeRepository.findById(employeeUpdatedEvent.getId()).
               orElseThrow(()-> new NotFoundException("Employ not found"));
       employee.setIsDisciplined(employeeUpdatedEvent.getIsDisciplined());
       employee.setKin(employeeUpdatedEvent.getKin());
       employee.setFirstName(employeeUpdatedEvent.getFirstName());
       employee.setLastName(employeeUpdatedEvent.getLastName());
       employeeRepository.save(employee);

    }
    @EventHandler
    public void on(EmployeeDeleteEvent employeeDeleteEvent){
        employeeRepository.findById(employeeDeleteEvent.getId()).orElseThrow(()->new NotFoundException("Employ not found"));
        employeeRepository.deleteById(employeeDeleteEvent.getId());
    }
}
