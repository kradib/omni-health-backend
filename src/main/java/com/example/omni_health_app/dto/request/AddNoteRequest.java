package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.checkerframework.common.aliasing.qual.Unique;

@Data
@Builder
public class AddNoteRequest {

    @NonNull
    private String note;

}
