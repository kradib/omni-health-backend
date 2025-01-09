package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class ResetPasswordResponse extends ResponseWrapper<ResetPasswordResponseData> {

    @Builder
    public ResetPasswordResponse(ResetPasswordResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}