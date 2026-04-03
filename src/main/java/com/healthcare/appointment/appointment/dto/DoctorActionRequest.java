package com.healthcare.appointment.appointment.dto;

import java.time.LocalDateTime;

public class DoctorActionRequest {

    private DoctorAppointmentAction action;
    private String message;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;

    public DoctorAppointmentAction getAction() {
        return action;
    }

    public void setAction(DoctorAppointmentAction action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getProposedStartTime() {
        return proposedStartTime;
    }

    public void setProposedStartTime(LocalDateTime proposedStartTime) {
        this.proposedStartTime = proposedStartTime;
    }

    public LocalDateTime getProposedEndTime() {
        return proposedEndTime;
    }

    public void setProposedEndTime(LocalDateTime proposedEndTime) {
        this.proposedEndTime = proposedEndTime;
    }

    public enum DoctorAppointmentAction {
        ACCEPT,
        DECLINE,
        REQUEST_RESCHEDULE
    }
}
