package com.healthcare.appointment.appointment.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointments")
@CompoundIndex(name = "doctor_start_idx", def = "{'doctorId': 1, 'startTime': 1}")
@CompoundIndex(name = "patient_start_idx", def = "{'patientId': 1, 'startTime': 1}")
public class Appointment {

    @Id
    private String id;
    private String patientId;
    private String doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private String consultationType;
    private String notes;

    /** Doctor decline reason or note when requesting reschedule. */
    private String doctorMessage;

    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDoctorMessage() {
        return doctorMessage;
    }

    public void setDoctorMessage(String doctorMessage) {
        this.doctorMessage = doctorMessage;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
