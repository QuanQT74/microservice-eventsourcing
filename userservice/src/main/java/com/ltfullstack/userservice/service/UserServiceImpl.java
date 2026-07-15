package com.ltfullstack.userservice.service;

import com.google.errorprone.annotations.Var;
import com.ltfullstack.userservice.dto.CreateUserRequest;
import com.ltfullstack.userservice.dto.LoginRequestDto;
import com.ltfullstack.userservice.dto.UpdateUserRequest;
import com.ltfullstack.userservice.dto.UserResponse;
import com.ltfullstack.userservice.dto.identity.TokenResponse;
import com.ltfullstack.userservice.dto.identity.UserCreationParam;
import com.ltfullstack.userservice.entity.User;
import com.ltfullstack.userservice.repository.AuthClient;
import com.ltfullstack.userservice.repository.IdentityClient;
import com.ltfullstack.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ltfullstack.userservice.dto.identity.Credential;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakTokenService keycloakTokenService;

    @Autowired
    IdentityClient identityClient;

    @Autowired
    AuthClient authClient;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        String accessToken = keycloakTokenService.getAccessToken();
        log.info("TOKEN: {}", accessToken);

        var creationResponse = identityClient.CreaterUser(
                UserCreationParam.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .firstName(request.getFullName().split(" ")[0])
                        .lastName(request.getFullName().contains(" ")
                                ? request.getFullName().substring(request.getFullName().indexOf(" ") + 1)
                                : "")
                        .enabled(true)
                        .emailVerified(false)
                        .credentials(List.of(
                                Credential.builder()
                                        .value(request.getPassword())
                                        .temporary(false)
                                        .build()
                        ))
                        .build(),
                "Bearer " + accessToken
        );

        String location = creationResponse.getHeaders().getFirst("Location");
        String keycloakUserId = location != null ? location.substring(location.lastIndexOf("/") + 1) : null;

        User user = User.builder()
                .id(keycloakUserId)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .isActive(true)
                .build();
        

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersByStatus(Boolean isActive) {
        return userRepository.findAllByIsActive(isActive).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!user.getUsername().equals(request.getUsername()) 
                && userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (!user.getEmail().equals(request.getEmail()) 
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setIsActive(request.getIsActive());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public TokenResponse login(LoginRequestDto dto) {
        java.util.Map<String, String> formData = new java.util.HashMap<>();
        formData.put("grant_type", "password");
        formData.put("client_id", clientId);
        formData.put("client_secret", clientSecret);
        formData.put("username", dto.getUsername());
        formData.put("password", dto.getPassword());
        formData.put("scope", "openid");
        return authClient.login(formData);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
