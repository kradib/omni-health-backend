package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class UserSignUpResponse extends ResponseWrapper<UserSIgnUpResponseData> {

    @Builder
    public UserSignUpResponse(UserSIgnUpResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
