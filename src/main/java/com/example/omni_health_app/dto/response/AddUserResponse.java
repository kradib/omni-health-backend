package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class AddUserResponse extends ResponseWrapper<AddUserResponseData> {

    @Builder
    public AddUserResponse(AddUserResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
