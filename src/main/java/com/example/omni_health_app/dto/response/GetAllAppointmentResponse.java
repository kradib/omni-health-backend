package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class GetAllAppointmentResponse extends ResponseWrapper<GetAllAppointmentResponseData> {

    @Builder
    public GetAllAppointmentResponse(GetAllAppointmentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
