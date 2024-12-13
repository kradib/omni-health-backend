package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class CreateAppointmentResponse extends ResponseWrapper<CreateAppointmentResponseData> {

    @Builder
    public CreateAppointmentResponse(CreateAppointmentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
