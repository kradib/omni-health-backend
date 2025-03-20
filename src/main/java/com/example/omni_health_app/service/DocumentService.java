package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.AppointmentDocument;
import com.example.omni_health_app.domain.entity.DocumentEntity;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.repositories.AppointmentDocumentRepository;
import com.example.omni_health_app.domain.repositories.DocumentRepository;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.response.DocumentMetadata;
import com.example.omni_health_app.exception.BadRequestException;
import com.example.omni_health_app.exception.UserAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.omni_health_app.util.FileUtils.getFileExtension;


@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg");
    private static final String BASE_UPLOAD_DIR = "/var/uploads/";
    private final DocumentRepository documentRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final AppointmentDocumentRepository appointmentDocumentRepository;
    private final IDocumentProcessor documentProcessor;


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

        final String filePath = documentProcessor.uploadFile(file, originalFilename);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setUserName(userName);
        documentEntity.setDocumentName(documentName);
        documentEntity.setFilePath(filePath);
        documentEntity.setDateUploaded(LocalDateTime.now());
        documentRepository.save(documentEntity);
        return DocumentMetadata.builder()
                .documentName(documentEntity.getDocumentName())
                .id(documentEntity.getId())
                .dateUploaded(documentEntity.getDateUploaded())
                .build();
    }


    public DocumentMetadata uploadFileForAppointment(MultipartFile file, String userName, String documentName,
                                                      Long appointmentId)
            throws BadRequestException, IOException {
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }
        final Optional<UserAppointmentSchedule> userAppointmentScheduleOptional =
                userAppointmentScheduleRepository.findById(appointmentId);
        if (userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException("Appointment does not exist");
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Only PDF and JPG files are allowed!");
        }

        final String filePath = documentProcessor.uploadFile(file, originalFilename);


        AppointmentDocument appointmentDocument = AppointmentDocument.builder()
                .appointment(userAppointmentScheduleOptional.get())
                .filePath(filePath)
                .dateUploaded(LocalDateTime.now())
                .documentName(documentName)
                .build();

        appointmentDocumentRepository.save(appointmentDocument);
        return DocumentMetadata.builder()
                .documentName(appointmentDocument.getDocumentName())
                .id(appointmentDocument.getId())
                .dateUploaded(appointmentDocument.getDateUploaded())
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

        return documentEntityOptional.get();
    }

    public AppointmentDocument getAppointmentDocument(String userName, long appointmentId, long documentId) throws BadRequestException, IOException {
        Optional<UserAppointmentSchedule> userAppointmentScheduleOptional =
                userAppointmentScheduleRepository.findById(appointmentId);
        if(userAppointmentScheduleOptional.isEmpty()) {
            throw new BadRequestException("Appointment does not exist");
        }
        final Optional<AppointmentDocument> appointmentDocumentOptional = appointmentDocumentRepository.findById(documentId);
        if(appointmentDocumentOptional.isEmpty()) {
            throw new BadRequestException("Document does not exist");
        }
        if(!userAppointmentScheduleOptional.get().getUsername().equals(userName) && !userAppointmentScheduleOptional.get().getDoctorDetail().getUserAuth().getUsername().equals(userName)) {
            throw new BadRequestException(String.format("userName %s does not have access to the appointment " +
                    "documents", userName));
        }

        return appointmentDocumentOptional.get();
    }

    public InputStreamResource getDocumentMedia(final String path) {
        return documentProcessor.downloadFile(path);
    }


}
