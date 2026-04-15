package com.healthcare.appointment.notification.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final WebClient webClient;
    private final String notificationServiceUrl;

    public NotificationServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${notification.service.url:http://localhost:8084}") String notificationServiceUrl) {
        this.webClient = webClientBuilder.build();
        this.notificationServiceUrl = notificationServiceUrl;
        log.info("NotificationServiceClient initialized with URL: {}", notificationServiceUrl);
    }

    public Mono<Void> sendAppointmentApprovedNotification(String patientId, String appointmentId, String startTime, String authToken) {
        Map<String, String> request = Map.of(
                "patientId", patientId,
                "doctorId", "",
                "appointmentId", appointmentId,
                "notificationType", "APPOINTMENT_APPROVED",
                "startTime", startTime);
        
        String url = notificationServiceUrl + "/api/notifications/appointment";
        log.info("Sending appointment approval notification to: {} for patient: {}, appointment: {}", 
                url, patientId, appointmentId);
        
        var webClientRequest = webClient.post()
                .uri(url)
                .bodyValue(request);
        
        // Add Authorization header if token is provided
        if (authToken != null && !authToken.isEmpty()) {
            webClientRequest.header("Authorization", authToken);
        }
        
        return webClientRequest.retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(success -> log.info("Successfully sent notification for appointment: {}", appointmentId))
                .doOnError(error -> log.error("Failed to send notification for appointment: {}. Error: {}", 
                        appointmentId, error.getMessage()));
    }

    public Mono<Void> sendAppointmentCreatedNotification(String patientId, String doctorId, String appointmentId, String startTime, String authToken) {
        Map<String, String> request = Map.of(
                "patientId", patientId,
                "doctorId", doctorId == null ? "" : doctorId,
                "appointmentId", appointmentId,
                "notificationType", "APPOINTMENT_CREATED",
                "startTime", startTime);
        
        String url = notificationServiceUrl + "/api/notifications/appointment";
        log.info("Sending appointment created notification to: {} for patient: {}, doctor: {}, appointment: {}", 
                url, patientId, doctorId, appointmentId);
        
        var webClientRequest = webClient.post()
                .uri(url)
                .bodyValue(request);
        
        // Add Authorization header if token is provided
        if (authToken != null && !authToken.isEmpty()) {
            webClientRequest.header("Authorization", authToken);
        }
        
        return webClientRequest.retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(success -> log.info("Successfully sent appointment created notification for appointment: {}", appointmentId))
                .doOnError(error -> log.error("Failed to send appointment created notification for appointment: {}. Error: {}", 
                        appointmentId, error.getMessage()));
    }
}
