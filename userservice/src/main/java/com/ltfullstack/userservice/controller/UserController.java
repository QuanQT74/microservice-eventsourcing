package com.ltfullstack.userservice.controller;

import com.ltfullstack.userservice.dto.CreateUserRequest;
import com.ltfullstack.userservice.dto.LoginRequestDto;
import com.ltfullstack.userservice.dto.UpdateUserRequest;
import com.ltfullstack.userservice.dto.UserResponse;
import com.ltfullstack.userservice.dto.identity.TokenResponse;
import com.ltfullstack.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User CRUD")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) Boolean isActive) {
        List<UserResponse> users;
        if (isActive != null) {
            users = userService.getAllUsersByStatus(isActive);
        } else {
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user by ID")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user by ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

  \
}
