package com.healthcare.appointment.appointment.dto;

import java.time.LocalDateTime;

public record DoctorActionRequest(
        DoctorAppointmentAction action,
        String message,
        LocalDateTime proposedStartTime,
        LocalDateTime proposedEndTime) {

    public enum DoctorAppointmentAction {
        ACCEPT,
        DECLINE,
        REQUEST_RESCHEDULE
    }
}
