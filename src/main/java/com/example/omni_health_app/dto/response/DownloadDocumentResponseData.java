
package com.example.omni_health_app.dto.response;


import com.example.omni_health_app.domain.entity.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadDocumentResponseData {

    private boolean success;
    private Resource file;
    private DocumentEntity documentEntity;
}
