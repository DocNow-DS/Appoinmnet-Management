package com.healthcare.appointment.notification.dto;

public record AppointmentNotificationRequest(
    String patientId,
    String appointmentId,
    String notificationType,
    String startTime
) {}
