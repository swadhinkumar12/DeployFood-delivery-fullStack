package com.fooddelivery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${notification.email.from:noreply@bytesoul.app}")
    private String fromEmail;

    @Value("${notification.email.provider:auto}")
    private String emailProvider;

    @Value("${notification.email.sendgrid.api-key:}")
    private String sendGridApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendEmailAsync(String toEmail, String subject, String body) {
        if (!emailEnabled || toEmail == null || toEmail.isBlank()) {
            return;
        }

        // Prefer SendGrid HTTPS API when configured to avoid blocked SMTP egress ports.
        if (canUseSendGridApi()) {
            sendViaSendGridApi(toEmail, subject, body);
            return;
        }

        if (mailSender == null) {
            log.info("[EMAIL_NOTIFY] JavaMailSender not configured. to={} subject={}", toEmail, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("[EMAIL_NOTIFY] Sent email to {}", toEmail);
        } catch (Exception ex) {
            log.warn("[EMAIL_NOTIFY] Failed sending email to {}. reason={}", toEmail, ex.getMessage());
        }
    }

    private boolean canUseSendGridApi() {
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            return false;
        }
        return "sendgrid_api".equalsIgnoreCase(emailProvider) || "auto".equalsIgnoreCase(emailProvider);
    }

    private void sendViaSendGridApi(String toEmail, String subject, String body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(sendGridApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = Map.of(
                    "personalizations", List.of(Map.of("to", List.of(Map.of("email", toEmail)))),
                    "from", Map.of("email", fromEmail),
                    "subject", subject,
                    "content", List.of(Map.of("type", "text/plain", "value", body))
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.sendgrid.com/v3/mail/send",
                    new HttpEntity<>(payload, headers),
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[EMAIL_NOTIFY] Sent email via SendGrid API to {}", toEmail);
            } else {
                log.warn("[EMAIL_NOTIFY] SendGrid API failed for {}. status={} body={}",
                        toEmail, response.getStatusCode().value(), response.getBody());
            }
        } catch (Exception ex) {
            log.warn("[EMAIL_NOTIFY] Failed sending via SendGrid API to {}. reason={}", toEmail, ex.getMessage());
        }
    }
}
