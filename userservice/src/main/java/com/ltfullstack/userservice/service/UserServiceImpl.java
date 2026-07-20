package com.ltfullstack.userservice.service;

import com.google.errorprone.annotations.Var;
import com.ltfullstack.userservice.dto.CreateUserRequest;
import com.ltfullstack.userservice.dto.LoginRequestDto;
import com.ltfullstack.userservice.dto.UpdateUserRequest;
import com.ltfullstack.userservice.dto.UserResponse;
import com.ltfullstack.userservice.dto.identity.TokenResponse;
import com.ltfullstack.userservice.dto.identity.UserCreationParam;
import com.ltfullstack.userservice.entity.User;
import com.ltfullstack.userservice.client.EmployeeClient;
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
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    EmployeeClient employeeClient;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    @Value("${employee-service.url}")
    @NonFinal
    String employeeServiceUrl;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Sanitize username - Keycloak only allows a-z, A-Z, 0-9, _, -, .
        String sanitizedUsername = request.getUsername()
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9._-]", "_"); // Replace invalid chars with _
        
        // Ensure username doesn't start with special chars
        while (sanitizedUsername.startsWith(".") || sanitizedUsername.startsWith("-") || sanitizedUsername.startsWith("_")) {
            sanitizedUsername = sanitizedUsername.substring(1);
        }
        
        if (sanitizedUsername.isEmpty()) {
            throw new RuntimeException("Username is invalid after sanitization");
        }
        
        if (userRepository.existsByUsername(sanitizedUsername)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        String accessToken = keycloakTokenService.getAccessToken();
        log.info("ADMIN TOKEN obtained: {}", accessToken.substring(0, Math.min(50, accessToken.length())));

        log.info("Creating user in Keycloak: {}", sanitizedUsername);
        String[] nameParts = request.getFullName().trim().split("\\s+");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 
                ? request.getFullName().substring(request.getFullName().indexOf(" ") + 1).trim()
                : request.getFullName();
        if (lastName.isEmpty()) {
            lastName = firstName;
        }
        
        var creationResponse = identityClient.CreaterUser(
                UserCreationParam.builder()
                        .username(sanitizedUsername)
                        .email(request.getEmail())
                        .firstName(firstName)
                        .lastName(lastName)
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

        log.info("Keycloak response status: {}", creationResponse.getStatusCode());
        log.info("Keycloak response headers: {}", creationResponse.getHeaders());

        String location = creationResponse.getHeaders().getFirst("Location");
        log.info("Keycloak Location header: {}", location);

        if (location == null || location.isEmpty()) {
            log.error("Failed to create user in Keycloak - no Location header returned");
            throw new RuntimeException("Failed to create user in Keycloak - no Location header returned");
        }

        String keycloakUserId = location.substring(location.lastIndexOf("/") + 1);
        log.info("Keycloak user ID: {}", keycloakUserId);

        // Tạo Employee tự động — lưu cả UUID (employeeId) và memberCode
        String memberCode;
        String employeeId = null;
        try {
            var created = employeeClient.createEmployee(firstName, lastName, keycloakUserId);
            employeeId = created.getId();
            memberCode = created.getMemberCode();
            log.info("Employee created with id={}, memberCode={}", employeeId, memberCode);
        } catch (Exception e) {
            log.error("Failed to create employee, generating fallback code: {}", e.getMessage());
            memberCode = "EMP" + System.currentTimeMillis() % 10000;
        }
        
        User user = User.builder()
                .id(keycloakUserId)
                .username(sanitizedUsername)
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .isActive(true)
                .employeeId(employeeId)
                .memberCode(memberCode)
                .build();

        try {
            User savedUser = userRepository.save(user);
            log.info("=== USER SAVED SUCCESS ===");
            log.info("User saved to local DB with ID: {} and memberCode: {}", savedUser.getId(), memberCode);
            return mapToResponse(savedUser);
        } catch (Exception e) {
            log.error("=== USER SAVE FAILED ===");
            log.error("Error saving user: {}", e.getMessage());
            log.error("User data: id={}, username={}, email={}, memberCode={}", 
                keycloakUserId, sanitizedUsername, request.getEmail(), memberCode);
            throw new RuntimeException("Failed to save user to database: " + e.getMessage(), e);
        }
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
    @Transactional(readOnly = true)
    public UserResponse getUserByEmployeeId(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("User not found with employeeId: " + employeeId));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        // Backfill employeeId nếu account cũ chỉ có memberCode
        if ((user.getEmployeeId() == null || user.getEmployeeId().isBlank())
                && user.getMemberCode() != null) {
            try {
                getEmployeeId(user.getId());
                user = userRepository.findById(user.getId()).orElse(user);
            } catch (Exception ignored) {
                // keep response without employeeId
            }
        }
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

    @Override
    public UserResponse fixMemberCode(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String keycloakUserId = user.getId();
        try {
            var created = employeeClient.createEmployee(
                    user.getFullName().split("\\s+")[0],
                    user.getFullName().contains(" ") ? user.getFullName().substring(user.getFullName().indexOf(" ") + 1).trim() : user.getFullName(),
                    keycloakUserId
            );
            user.setEmployeeId(created.getId());
            user.setMemberCode(created.getMemberCode());
            log.info("Employee linked: id={}, memberCode={}", created.getId(), created.getMemberCode());
        } catch (Exception e) {
            log.error("Failed to create employee: {}", e.getMessage());
            throw new RuntimeException("Failed to fix memberCode: " + e.getMessage(), e);
        }

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public String getEmployeeId(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (user.getEmployeeId() != null && !user.getEmployeeId().isBlank()) {
            return user.getEmployeeId();
        }
        // Legacy: chỉ có memberCode → resolve UUID từ employee service rồi backfill
        if (user.getMemberCode() != null && !user.getMemberCode().isBlank()) {
            try {
                String url = employeeServiceUrl + "/api/v1/employees/by-member-code/" + user.getMemberCode();
                var emp = restTemplate.getForObject(url, java.util.Map.class);
                if (emp != null && emp.get("id") != null) {
                    String employeeId = String.valueOf(emp.get("id"));
                    user.setEmployeeId(employeeId);
                    userRepository.save(user);
                    log.info("Backfilled employeeId={} for user {}", employeeId, id);
                    return employeeId;
                }
            } catch (Exception e) {
                log.warn("Could not resolve employeeId from memberCode {}: {}", user.getMemberCode(), e.getMessage());
            }
        }
        return user.getEmployeeId();
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
                .memberCode(user.getMemberCode())
                .employeeId(user.getEmployeeId())
                .build();
    }
}
