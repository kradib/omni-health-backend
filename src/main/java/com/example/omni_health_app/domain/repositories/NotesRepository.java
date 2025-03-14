package com.example.omni_health_app.domain.repositories;

import com.example.omni_health_app.domain.entity.Notes;
import com.example.omni_health_app.domain.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {

}