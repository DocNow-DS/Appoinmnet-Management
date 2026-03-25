package com.healthcare.appointment.appointment.dto;

import java.time.LocalDateTime;

public record PatientRescheduleRequest(
        LocalDateTime startTime,
        Integer durationMinutes,
        String notes) {
}
