package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserAuth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    Optional<UserAuth> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
                SELECT ua FROM UserAuth ua 
                JOIN ua.userDetail ud 
                WHERE (:role IS NULL OR ua.roles = :role) 
                AND (:name IS NULL OR LOWER(CONCAT(ud.firstName, ' ', ud.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))
                    OR LOWER(ud.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
                    OR LOWER(ud.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
            """)
    Page<UserAuth> findByRoleAndName(
            @Param("role") String role,
            @Param("name") String name,
            Pageable pageable
    );
}
