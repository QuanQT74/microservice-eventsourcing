package com.ltfullstack.employeeservice.query.projection;

import com.ltfullstack.commonservice.model.EmployeeResponseCommandModel;
import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import com.ltfullstack.employeeservice.query.model.EmployeeResponseModel;
import com.ltfullstack.employeeservice.query.queries.GetAllEmployeeQuery;
import com.ltfullstack.commonservice.queries.GetDetailEmployeeQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
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
                    BeanUtils.copyProperties(employee, employeeResponseModel);
                    return employeeResponseModel;
                }).toList();
    }

    @QueryHandler
    public EmployeeResponseCommandModel handle(GetDetailEmployeeQuery employeeQuery){
        Employee employee = employeeRepository.findById(employeeQuery.getId())
            .orElseThrow(() -> new RuntimeException("Employee Not Found: " + employeeQuery.getId()));
        EmployeeResponseCommandModel model = new EmployeeResponseCommandModel();
        BeanUtils.copyProperties(employee, model);
        return model;
    }
}
