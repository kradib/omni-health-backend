
package com.example.omni_health_app.dto.response;


import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAppointmentResponseData {

    private boolean success;
    private UserAppointmentSchedule appointmentSchedule;
}
