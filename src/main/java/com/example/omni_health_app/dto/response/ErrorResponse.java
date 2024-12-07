package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class ErrorResponse extends ResponseWrapper<ErrorResponseData> {

    @Builder
    public ErrorResponse(ResponseMetadata responseMetadata) {
        super(ErrorResponseData.builder()
                .success(false)
                .build(), responseMetadata);
    }
}
