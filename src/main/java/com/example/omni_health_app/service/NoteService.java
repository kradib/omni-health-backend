package com.example.omni_health_app.service;

import com.example.omni_health_app.domain.entity.Notes;
import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;
import com.example.omni_health_app.domain.entity.UserAuth;
import com.example.omni_health_app.domain.repositories.NotesRepository;
import com.example.omni_health_app.domain.repositories.UserAppointmentScheduleRepository;
import com.example.omni_health_app.domain.repositories.UserAuthRepository;
import com.example.omni_health_app.dto.request.AddNoteRequest;
import com.example.omni_health_app.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final UserAppointmentScheduleRepository userAppointmentScheduleRepository;
    private final UserAuthRepository userAuthRepository;
    private final NotesRepository notesRepository;


    @Transactional
    public Notes addNote(final String userName, final Long appointmentId, AddNoteRequest request) throws BadRequestException {

        UserAppointmentSchedule appointment = userAppointmentScheduleRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found with id: " + appointmentId));
        final Optional<UserAuth> userAuthOptional = userAuthRepository.findByUsername(userName);
        if (userAuthOptional.isEmpty()) {
            throw new BadRequestException(String.format("User %s does not exist", userName));
        }

        Notes note = Notes.builder()
                .appointment(appointment)
                .note(request.getNote())
                .name(userAuthOptional.get().getUserDetail().getFirstName() + " " + userAuthOptional.get().getUserDetail().getLastName())
                .createdAt(LocalDateTime.now())
                .build();
        return notesRepository.save(note);
    }



}
