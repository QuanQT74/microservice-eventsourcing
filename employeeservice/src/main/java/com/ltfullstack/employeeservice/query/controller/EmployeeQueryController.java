package com.ltfullstack.employeeservice.query.controller;

import com.ltfullstack.commonservice.model.EmployeeResponseCommandModel;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import com.ltfullstack.employeeservice.query.model.EmployeeResponseModel;
import com.ltfullstack.employeeservice.query.queries.GetAllEmployeeQuery;
import com.ltfullstack.commonservice.queries.GetDetailEmployeeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Query")
@Slf4j
public class EmployeeQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private EmployeeRepository employeeRepository;
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
       log.info("Calling to getALLEmployee");
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

    @GetMapping("/by-member-code/{code}")
    public EmployeeResponseCommandModel getByMemberCode(@PathVariable String code) {
        return employeeRepository.findByMemberCode(code)
                .map(emp -> {
                    EmployeeResponseCommandModel model = new EmployeeResponseCommandModel();
                    model.setId(emp.getId());
                    model.setFirstName(emp.getFirstName());
                    model.setLastName(emp.getLastName());
                    model.setKin(emp.getKin());
                    model.setIsDisciplined(emp.getIsDisciplined());
                    return model;
                })
                .orElseThrow(() -> new RuntimeException("Employee not found with memberCode: " + code));
    }

    @GetMapping("/{employeeid}")
    public EmployeeResponseCommandModel getDetailEmployee(@PathVariable String employeeid){
        EmployeeResponseCommandModel employeeResponseModel = queryGateway.
                query(new GetDetailEmployeeQuery(employeeid),
                        ResponseTypes.instanceOf(EmployeeResponseCommandModel.class)).join();
        return employeeResponseModel;
    }

}
