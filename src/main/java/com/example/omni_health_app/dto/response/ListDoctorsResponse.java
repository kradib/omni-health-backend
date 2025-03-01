package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class ListDoctorsResponse extends ResponseWrapper<ListDoctorsResponseData> {

    @Builder
    public ListDoctorsResponse(ListDoctorsResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
