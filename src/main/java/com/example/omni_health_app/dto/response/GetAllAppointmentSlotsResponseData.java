
package com.example.omni_health_app.dto.response;


import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.dto.model.AppointmentSlotAvailable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllAppointmentSlotsResponseData {

    private List<AppointmentSlotAvailable> appointmentSlotAvailableList;
}
