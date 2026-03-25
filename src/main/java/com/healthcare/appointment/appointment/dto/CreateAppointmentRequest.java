package com.healthcare.appointment.appointment.dto;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        String patientId,
        String doctorId,
        LocalDateTime startTime,
        Integer durationMinutes,
        String consultationType,
        String notes) {
}
