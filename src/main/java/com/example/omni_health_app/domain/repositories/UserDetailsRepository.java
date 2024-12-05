package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

}
