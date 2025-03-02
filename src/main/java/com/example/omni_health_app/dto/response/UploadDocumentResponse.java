package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class UploadDocumentResponse extends ResponseWrapper<UploadDocumentResponseData> {

    @Builder
    public UploadDocumentResponse(UploadDocumentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
