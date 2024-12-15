package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetail, Long> {

}
