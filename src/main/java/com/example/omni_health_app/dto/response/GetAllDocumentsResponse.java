package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class GetAllDocumentsResponse extends ResponseWrapper<GetAllDocumentsResponseData> {

    @Builder
    public GetAllDocumentsResponse(GetAllDocumentsResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
