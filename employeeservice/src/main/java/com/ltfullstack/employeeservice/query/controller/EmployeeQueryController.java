package com.ltfullstack.employeeservice.query.controller;

import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.query.model.EmployeeResponseModel;
import com.ltfullstack.employeeservice.query.queries.GetAllEmployeeQuery;
import com.ltfullstack.employeeservice.query.queries.GetDetailEmployeeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Target;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Query")
public class EmployeeQueryController {
    @Autowired
    private QueryGateway queryGateway;
    @Operation(
            summary = "Get List Employee",
            description = "Get endpoin for employee with filter",
            responses = {
                    @ApiResponse(
                            description = "Seccues",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description ="Unauthorized / Invalid Token",
                            responseCode = "401"
                    )
            }
    )

    @GetMapping
    public List<EmployeeResponseModel> getAllDetail(@RequestParam(required = false,defaultValue = "false") Boolean isDisciplined){
        List<EmployeeResponseModel> list = queryGateway.
                query(new GetAllEmployeeQuery(isDisciplined),
                        ResponseTypes.multipleInstancesOf(EmployeeResponseModel.class)).join();
        return list;
    }

    @Operation(
            summary = "Get employee by ID",
            description = "Retrieve detailed information of an employee using the employee ID.",
            responses = {
                    @ApiResponse(
                            description = "Employee retrieved successfully",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Employee not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }

    )

    @GetMapping("/{employeeid}")
    public EmployeeResponseModel getDetailEmployee(@PathVariable String employeeid){
        EmployeeResponseModel employeeResponseModel = queryGateway.
                query(new GetDetailEmployeeQuery(employeeid),
                        ResponseTypes.instanceOf(EmployeeResponseModel.class)).join();
        return employeeResponseModel;
    }

}
