package com.ltfullstack.userservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class EmployeeClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${employee-service.url}")
    private String employeeServiceUrl;

    public String createEmployee(String firstName, String lastName, String kin) {
        try {
            String url = employeeServiceUrl + "/api/v1/employees";
            
            String requestBody = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"kin\":\"%s\"}",
                firstName, lastName, kin
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            String employeeId = response.getBody();
            log.info("Employee creation response: {}", response.getStatusCode());
            log.info("Employee ID from response: {}", employeeId);
            
            // Wait for Axon to process the event
            Thread.sleep(500);
            
            return employeeId;
        } catch (Exception e) {
            log.error("Failed to create employee: {}", e.getMessage());
            throw new RuntimeException("Failed to create employee: " + e.getMessage());
        }
    }
}
