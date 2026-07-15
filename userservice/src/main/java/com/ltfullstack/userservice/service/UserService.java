package com.ltfullstack.userservice.service;

import com.ltfullstack.userservice.dto.CreateUserRequest;
import com.ltfullstack.userservice.dto.LoginRequestDto;
import com.ltfullstack.userservice.dto.UpdateUserRequest;
import com.ltfullstack.userservice.dto.UserResponse;
import com.ltfullstack.userservice.dto.identity.TokenResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getAllUsers();
    List<UserResponse> getAllUsersByStatus(Boolean isActive);
    UserResponse getUserById(String id);
    UserResponse updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
    TokenResponse login(LoginRequestDto dto);
}
