package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class DownloadDocumentResponse extends ResponseWrapper<DownloadDocumentResponseData> {

    @Builder
    public DownloadDocumentResponse(DownloadDocumentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
