package com.ltfullstack.employeeservice.query.controller;

import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.query.model.EmployeeResponseModel;
import com.ltfullstack.employeeservice.query.queries.GetAllEmployeeQuery;
import com.ltfullstack.employeeservice.query.queries.GetDetailEmployeeQuery;
import io.swagger.v3.oas.annotations.Operation;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeQueryController {
    @Autowired
    private QueryGateway queryGateway;
    @Operation(

    )
    @GetMapping
    public List<EmployeeResponseModel> getAllDetail(@RequestParam(required = false,defaultValue = "false") Boolean isDisciplined){
        List<EmployeeResponseModel> list = queryGateway.query(new GetAllEmployeeQuery(isDisciplined), ResponseTypes.multipleInstancesOf(EmployeeResponseModel.class)).join();
        return list;

    }
    @GetMapping("/{employeeid}")
    public EmployeeResponseModel getDetailEmployee(@PathVariable String employeeid){
        EmployeeResponseModel employeeResponseModel = queryGateway.query(new GetDetailEmployeeQuery(employeeid), ResponseTypes.instanceOf(EmployeeResponseModel.class)).join();
        return employeeResponseModel;
    }

}
