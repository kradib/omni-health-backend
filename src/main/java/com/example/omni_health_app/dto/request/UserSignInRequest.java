package com.example.omni_health_app.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserSignInRequest {

    @NonNull
    private String username;
    @NonNull
    private String password;

}
