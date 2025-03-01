package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    List<UserAuth> findByRolesContaining(String role);
    Optional<UserAuth> findByUsername(String username);
    boolean existsByUsername(String username);
}
