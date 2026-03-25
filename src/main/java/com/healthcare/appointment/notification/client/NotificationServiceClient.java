package com.healthcare.appointment.notification.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.healthcare.appointment.notification.dto.AppointmentNotificationRequest;

import reactor.core.publisher.Mono;

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

    public Mono<Void> sendAppointmentApprovedNotification(String patientId, String appointmentId) {
        AppointmentNotificationRequest request = new AppointmentNotificationRequest(
            patientId, 
            appointmentId, 
            "APPOINTMENT_APPROVED"
        );
        
        String url = notificationServiceUrl + "/api/notifications/appointment";
        log.info("Sending appointment approval notification to: {} for patient: {}, appointment: {}", 
                url, patientId, appointmentId);
        
        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(success -> log.info("Successfully sent notification for appointment: {}", appointmentId))
                .doOnError(error -> log.error("Failed to send notification for appointment: {}. Error: {}", 
                        appointmentId, error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Error in notification service call: ", error);
                    return Mono.empty();
                });
    }
}
