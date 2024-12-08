package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class UserSignInResponse extends ResponseWrapper<UserSignInResponseData> {

    @Builder
    public UserSignInResponse(UserSignInResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
