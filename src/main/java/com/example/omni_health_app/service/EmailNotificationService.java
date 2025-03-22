package com.example.omni_health_app.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements INotificationService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendNotification(final String to, final String subject, final String body) {

        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Successfully sent email to {}", to);
        } catch (final Exception e) {
            //TODO: enable retry later
           log.error("Failed to send email notifications ", e);
        }

    }
}
