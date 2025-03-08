package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class UserUpdateResponse extends ResponseWrapper<UserUpdateResponseData> {

    @Builder
    public UserUpdateResponse(UserUpdateResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
