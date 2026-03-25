package com.healthcare.appointment.appointment.dto;

import java.time.LocalDateTime;

import com.healthcare.appointment.appointment.model.Appointment;
import com.healthcare.appointment.appointment.model.AppointmentStatus;

public record AppointmentResponse(
        String id,
        String patientId,
        String doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String consultationType,
        String notes,
        String doctorMessage,
        LocalDateTime proposedStartTime,
        LocalDateTime proposedEndTime,
        int progressPercent,
        String progressLabel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatientId(),
                a.getDoctorId(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getConsultationType(),
                a.getNotes(),
                a.getDoctorMessage(),
                a.getProposedStartTime(),
                a.getProposedEndTime(),
                progressPercent(a.getStatus()),
                progressLabel(a.getStatus()),
                a.getCreatedAt(),
                a.getUpdatedAt());
    }

    public static int progressPercent(AppointmentStatus status) {
        if (status == null) {
            return 0;
        }
        return switch (status) {
            case PENDING -> 25;
            case RESCHEDULE_REQUESTED -> 45;
            case ACCEPTED -> 70;
            case COMPLETED -> 100;
            case DECLINED, CANCELLED -> 0;
        };
    }

    public static String progressLabel(AppointmentStatus status) {
        if (status == null) {
            return "Unknown";
        }
        return switch (status) {
            case PENDING -> "Waiting for doctor";
            case RESCHEDULE_REQUESTED -> "Doctor suggested another time";
            case ACCEPTED -> "Confirmed";
            case COMPLETED -> "Completed";
            case DECLINED -> "Declined by doctor";
            case CANCELLED -> "Cancelled";
        };
    }
}
