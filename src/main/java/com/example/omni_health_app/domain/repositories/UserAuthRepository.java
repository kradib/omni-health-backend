package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    Optional<UserAuth> findByUsername(String username);
    boolean existsByUsername(String username);
}
