package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetail, Long> {

    @Query("SELECT ud FROM UserDetail ud JOIN ud.userAuth ua WHERE ua.username = :username")
    UserDetail findByUsername(String username);

}
