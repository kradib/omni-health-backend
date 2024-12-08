package com.example.omni_health_app.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignInResponseData {

    private String authToken;
}
