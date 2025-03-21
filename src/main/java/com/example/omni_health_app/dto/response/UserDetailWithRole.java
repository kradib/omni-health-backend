package com.example.omni_health_app.dto.response;

import com.example.omni_health_app.domain.entity.UserDetail;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailWithRole {
    private UserDetail userDetail;
    private String roles;
}
