package com.example.omni_health_app.dto.response;


import com.example.omni_health_app.domain.entity.UserDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateResponseData {

    private UserDetail userDetail;
}
