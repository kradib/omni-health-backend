
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
public class GetAllAppointmentResponseData {

    private boolean success;
    private List<UserAppointmentSchedule> ownAppointments;
    private List<UserAppointmentSchedule> appointments;
    private Map<String, List<UserAppointmentSchedule>> dependentAppointments;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
