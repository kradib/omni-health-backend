package com.example.omni_health_app.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@Builder
public class ForgotPasswordRequest {

    @NonNull
    private String userName;
}