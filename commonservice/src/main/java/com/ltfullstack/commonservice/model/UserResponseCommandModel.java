package com.ltfullstack.commonservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseCommandModel {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private Boolean isActive;
}
