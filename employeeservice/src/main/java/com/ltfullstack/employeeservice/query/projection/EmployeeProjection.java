package com.ltfullstack.employeeservice.query.projection;

import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import com.ltfullstack.employeeservice.query.model.EmployeeResponseModel;
import com.ltfullstack.employeeservice.query.queries.GetAllEmployeeQuery;
import com.ltfullstack.employeeservice.query.queries.GetDetailEmployeeQuery;
import jakarta.ws.rs.NotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeProjection {
    @Autowired
    private EmployeeRepository employeeRepository;

    @QueryHandler
    public List<EmployeeResponseModel> handle(GetAllEmployeeQuery employeeQuery){
        List<Employee> employees = employeeRepository.findAllByIsDisciplined(employeeQuery.getIsDisciplined());
        return employees.stream()
                .map(employee -> {
                    EmployeeResponseModel employeeResponseModel = new EmployeeResponseModel();
                    BeanUtils.copyProperties(employee,employeeResponseModel);
                    return employeeResponseModel;
                }).toList();

    }
    @QueryHandler
    public  EmployeeResponseModel handle(GetDetailEmployeeQuery employeeQuery)throws Exception {
        Employee employee = employeeRepository.findById(employeeQuery.getId()).orElseThrow(() -> new NotFoundException("Employee Not Found"));
        EmployeeResponseModel model = new EmployeeResponseModel();
        BeanUtils.copyProperties(employee,model);
        return model;
    }

}
