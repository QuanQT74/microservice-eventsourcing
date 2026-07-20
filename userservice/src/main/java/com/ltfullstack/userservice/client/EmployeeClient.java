package com.ltfullstack.userservice.client;

import lombok.Data;
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

    @Data
    public static class EmployeeCreateResult {
        private String id;
        private String memberCode;
    }

    public EmployeeCreateResult createEmployee(String firstName, String lastName, String kin) {
        try {
            String url = employeeServiceUrl + "/api/v1/employees";

            String requestBody = String.format(
                    "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"kin\":\"%s\"}",
                    firstName, lastName, kin
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<EmployeeCreateResult> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    EmployeeCreateResult.class
            );

            EmployeeCreateResult body = response.getBody();
            if (body == null || body.getId() == null) {
                throw new RuntimeException("Employee service returned empty body");
            }
            log.info("Employee created: id={}, memberCode={}", body.getId(), body.getMemberCode());
            Thread.sleep(300);
            return body;
        } catch (Exception e) {
            log.error("Failed to create employee: {}", e.getMessage());
            throw new RuntimeException("Failed to create employee: " + e.getMessage());
        }
    }
}
