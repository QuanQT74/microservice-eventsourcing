package com.ltfullstack.employeeservice.config;

import com.ltfullstack.employeeservice.command.data.Employee;
import com.ltfullstack.employeeservice.command.data.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initEmployees(EmployeeRepository employeeRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                employeeRepository.save(Employee.builder()
                        .id("emp-001")
                        .firstName("Nguyen")
                        .lastName("Van Minh")
                        .Kin("KIN-2024-001")
                        .isDisciplined(false)
                        .build());

                employeeRepository.save(Employee.builder()
                        .id("emp-002")
                        .firstName("Tran")
                        .lastName("Thi Lan")
                        .Kin("KIN-2024-002")
                        .isDisciplined(false)
                        .build());

                employeeRepository.save(Employee.builder()
                        .id("emp-003")
                        .firstName("Le")
                        .lastName("Van Duc")
                        .Kin("KIN-2024-003")
                        .isDisciplined(false)
                        .build());

                employeeRepository.save(Employee.builder()
                        .id("emp-004")
                        .firstName("Pham")
                        .lastName("Thi Hoa")
                        .Kin("KIN-2024-004")
                        .isDisciplined(true)
                        .build());

                employeeRepository.save(Employee.builder()
                        .id("emp-005")
                        .firstName("Hoang")
                        .lastName("Van Nam")
                        .Kin("KIN-2024-005")
                        .isDisciplined(false)
                        .build());

                System.out.println("Initialized 5 sample employees");
            }
        };
    }
}
