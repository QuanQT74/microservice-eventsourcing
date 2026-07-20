package com.ltfullstack.borrowingservice.query.controller;

import com.ltfullstack.borrowingservice.query.model.BorrowingResponseModel;
import com.ltfullstack.borrowingservice.query.queries.GetBorrowingByIdQuery;
import com.ltfullstack.borrowingservice.query.queries.GetBorrowingByUserQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
@Tag(name = "Borrowing Query")
@Slf4j
public class BorrowingQueryController {
    @Autowired
    private QueryGateway queryGateway;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${user-service.url:http://userservice:9004}")
    private String userServiceUrl;

    @Operation(summary = "Get my borrowings (current authenticated user only)")
    @GetMapping("/me")
    public List<BorrowingResponseModel> getMine(
            @RequestHeader(value = "X-User-ID", required = false) String keycloakUserId,
            @RequestHeader(value = "X-Employee-Id", required = false) String employeeIdHeader
    ) {
        String employeeId = resolveEmployeeId(keycloakUserId, employeeIdHeader);
        if (employeeId == null || employeeId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Cannot resolve current member. Please sign in again.");
        }
        log.info("GET /borrowings/me -> employeeId={}", employeeId);
        return queryGateway.query(
                new GetBorrowingByUserQuery(employeeId),
                ResponseTypes.multipleInstancesOf(BorrowingResponseModel.class)
        ).join();
    }

    @Operation(summary = "Get borrowings by employeeId — only for the owner")
    @GetMapping("/user/{userId}")
    public List<BorrowingResponseModel> getByUser(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-ID", required = false) String keycloakUserId,
            @RequestHeader(value = "X-Employee-Id", required = false) String employeeIdHeader
    ) {
        String callerEmployeeId = resolveEmployeeId(keycloakUserId, employeeIdHeader);
        if (callerEmployeeId == null || !callerEmployeeId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only view your own borrowings.");
        }
        return queryGateway.query(
                new GetBorrowingByUserQuery(userId),
                ResponseTypes.multipleInstancesOf(BorrowingResponseModel.class)
        ).join();
    }

    @Operation(summary = "Get active borrowings — disabled (privacy)")
    @GetMapping("/active")
    public List<BorrowingResponseModel> getActive() {
        return Collections.emptyList();
    }

    @Operation(summary = "Get borrowing by ID")
    @GetMapping("/{id}")
    public BorrowingResponseModel getById(@PathVariable String id) {
        return queryGateway.query(
                new GetBorrowingByIdQuery(id),
                ResponseTypes.instanceOf(BorrowingResponseModel.class)
        ).join();
    }

    private String resolveEmployeeId(String keycloakUserId, String employeeIdHeader) {
        if (employeeIdHeader != null && !employeeIdHeader.isBlank()) {
            return employeeIdHeader.trim();
        }
        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            return null;
        }
        try {
            String url = userServiceUrl + "/api/v1/users/" + keycloakUserId + "/employee-id";
            String employeeId = restTemplate.getForObject(url, String.class);
            if (employeeId != null) {
                employeeId = employeeId.trim().replace("\"", "");
            }
            return employeeId;
        } catch (Exception ex) {
            log.warn("Failed to resolve employeeId for user {}: {}", keycloakUserId, ex.getMessage());
            return null;
        }
    }
}
