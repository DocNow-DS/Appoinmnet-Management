package com.healthcare.appointment.appointment.dto;

import java.time.LocalDateTime;

import com.healthcare.appointment.appointment.model.Appointment;
import com.healthcare.appointment.appointment.model.AppointmentStatus;

public final class AppointmentResponse {

    private final String id;
    private final String patientId;
    private final String doctorId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final AppointmentStatus status;
    private final String consultationType;
    private final String notes;
    private final String doctorMessage;
    private final LocalDateTime proposedStartTime;
    private final LocalDateTime proposedEndTime;
    private final int progressPercent;
    private final String progressLabel;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AppointmentResponse(
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
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.consultationType = consultationType;
        this.notes = notes;
        this.doctorMessage = doctorMessage;
        this.proposedStartTime = proposedStartTime;
        this.proposedEndTime = proposedEndTime;
        this.progressPercent = progressPercent;
        this.progressLabel = progressLabel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public String getNotes() {
        return notes;
    }

    public String getDoctorMessage() {
        return doctorMessage;
    }

    public LocalDateTime getProposedStartTime() {
        return proposedStartTime;
    }

    public LocalDateTime getProposedEndTime() {
        return proposedEndTime;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public String getProgressLabel() {
        return progressLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
