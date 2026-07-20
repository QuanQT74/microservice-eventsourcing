package com.ltfullstack.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;

    private String username;

    private String email;

    private String fullName;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String memberCode;

    private String employeeId;
}
