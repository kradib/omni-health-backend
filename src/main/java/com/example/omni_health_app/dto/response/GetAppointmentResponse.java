package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class GetAppointmentResponse extends ResponseWrapper<GetAppointmentResponseData> {

    @Builder
    public GetAppointmentResponse(GetAppointmentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
