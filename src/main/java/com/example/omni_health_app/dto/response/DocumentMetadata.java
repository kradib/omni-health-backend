package com.example.omni_health_app.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentMetadata {

    private final long id;
    private final String documentName;
    private final LocalDateTime dateUploaded;
}
