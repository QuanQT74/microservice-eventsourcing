package com.ltfullstack.userservice.repository;

import com.ltfullstack.userservice.dto.LoginRequestDto;
import com.ltfullstack.userservice.dto.identity.TokenResponse;
import com.ltfullstack.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByIsActive(Boolean isActive);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
