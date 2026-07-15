package com.ltfullstack.userservice.controller;

import com.ltfullstack.userservice.dto.LoginRequestDto;
import com.ltfullstack.userservice.dto.identity.TockenExchange;
import com.ltfullstack.userservice.dto.identity.TokenResponse;
import com.ltfullstack.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/public")
public class PublicController {
    @Autowired
    private  UserService userService;
    @Operation(summary = "Login user")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequestDto request) {
        TokenResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
