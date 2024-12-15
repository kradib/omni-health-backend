package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class CancelAppointmentResponse extends ResponseWrapper<CancelAppointmentResponseData> {

    @Builder
    public CancelAppointmentResponse(CancelAppointmentResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
