
package com.example.omni_health_app.dto.response;


import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentResponseData {

    private boolean success;
    private UserAppointmentSchedule userAppointmentSchedule;
}
