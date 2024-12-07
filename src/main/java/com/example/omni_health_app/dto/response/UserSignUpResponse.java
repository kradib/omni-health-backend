package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class UserSignUpResponse extends ResponseWrapper<UserSignUpResponseData> {

    @Builder
    public UserSignUpResponse(UserSignUpResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
