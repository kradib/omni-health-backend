
package com.example.omni_health_app.dto.response;


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
    private LocalDateTime appointmentTime;
    private String userName;
    private String doctorName;
}
