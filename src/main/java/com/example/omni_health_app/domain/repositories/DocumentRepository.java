package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findByUserName(String userName);


}
