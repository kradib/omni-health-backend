package com.example.omni_health_app.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignUpResponseData {

    private String username;
}