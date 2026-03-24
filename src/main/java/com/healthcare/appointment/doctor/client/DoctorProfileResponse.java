package com.healthcare.appointment.doctor.client;

public record DoctorProfileResponse(
        String id,
        Boolean isActive,
        Boolean isVerified,
        String specialization) {
}
