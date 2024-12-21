package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class UpdateAppointmentResponse extends ResponseWrapper<UpdateAppointmentResponseData> {

    @Builder
    public UpdateAppointmentResponse(UpdateAppointmentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
