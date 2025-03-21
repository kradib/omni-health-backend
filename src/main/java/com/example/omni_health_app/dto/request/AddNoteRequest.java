package com.example.omni_health_app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddNoteRequest {

    @NonNull
    private String note;

}
