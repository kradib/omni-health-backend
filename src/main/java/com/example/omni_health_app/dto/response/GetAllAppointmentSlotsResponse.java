package com.example.omni_health_app.dto.response;

import lombok.Builder;

public class GetAllAppointmentSlotsResponse extends ResponseWrapper<GetAllAppointmentSlotsResponseData> {

    @Builder
    public GetAllAppointmentSlotsResponse(GetAllAppointmentSlotsResponseData data, ResponseMetadata responseMetadata) {
        super(data, responseMetadata);
    }
}
