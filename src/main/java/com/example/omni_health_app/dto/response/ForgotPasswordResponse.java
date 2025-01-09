package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class ForgotPasswordResponse extends ResponseWrapper<ForgotPasswordResponseData> {

    @Builder
    public ForgotPasswordResponse(ForgotPasswordResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}