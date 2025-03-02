package com.example.omni_health_app.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "document")
@Data
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String filePath;


}
