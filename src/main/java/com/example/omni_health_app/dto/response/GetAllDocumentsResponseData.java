
package com.example.omni_health_app.dto.response;


import com.example.omni_health_app.domain.entity.DocumentEntity;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllDocumentsResponseData {

    private boolean success;
    private List<DocumentMetadata> documentMetaData;
}
