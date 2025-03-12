package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.DocumentEntity;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.repositories.DocumentRepository;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.response.DocumentMetadata;
import com.example.omni_health_app.dto.response.DownloadDocumentResponseData;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg");
    private static final String BASE_UPLOAD_DIR = "/var/uploads/";
    private final DocumentRepository documentRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;


    public DocumentMetadata uploadFile(MultipartFile file, String userName, String documentName)
            throws BadRequestException, IOException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Only PDF and JPG files are allowed!");
        }

        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        Path userFolderPath = Paths.get(BASE_UPLOAD_DIR, userName);
        if (!Files.exists(userFolderPath)) {
            Files.createDirectories(userFolderPath);
        }
        Path filePath = userFolderPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setUserName(userName);
        documentEntity.setDocumentName(documentName);
        documentEntity.setFilePath(filePath.toString());
        documentEntity.setDateUploaded(LocalDateTime.now());
        documentRepository.save(documentEntity);
        return DocumentMetadata.builder()
                .documentName(documentEntity.getDocumentName())
                .id(documentEntity.getId())
                .dateUploaded(documentEntity.getDateUploaded())
                .build();
    }

    public List<DocumentMetadata> getAllDocuments(String userName) throws BadRequestException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }
        return documentRepository.findByUserName(userName).stream().map(documentEntity -> DocumentMetadata.builder()
                .id(documentEntity.getId())
                .documentName(documentEntity.getDocumentName())
                .dateUploaded(documentEntity.getDateUploaded())
                .build()).toList();
    }

    public DocumentEntity getDocument(String userName, long id) throws BadRequestException, UserAuthException, IOException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(id);

        if (documentEntityOptional.isEmpty()) {
            throw new BadRequestException("document does not exist");
        }
        if(!documentEntityOptional.get().getUserName().equals(userName)) {
            throw new UserAuthException("Document does not belong to user");
        }
        Path filePath = Paths.get(documentEntityOptional.get().getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new BadRequestException("document does not exist");
        }
        return documentEntityOptional.get();
    }

    public DocumentEntity getDocument(String doctorUserName, long appointmentId, long documentId) throws BadRequestException,
            UserAuthException, IOException {
        Optional<UserAppointmentSchedule> userAppointmentScheduleOptional =
                userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException("Appointment does not exist");
        }
        if(!userAppointmentScheduleOptional.get().getDoctorDetail().getUserAuth().getUsername().equals(doctorUserName)) {
            throw new UserAuthException("Doctor does not have enough permission to view this appointment documents");
        }
        return getDocument(userAppointmentScheduleOptional.get().getUsername(), documentId);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
