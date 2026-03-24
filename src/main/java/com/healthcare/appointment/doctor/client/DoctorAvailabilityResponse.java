package com.healthcare.appointment.doctor.client;

public record DoctorAvailabilityResponse(
        String id,
        String doctorId,
        String dayOfWeek,
        String startTime,
        String endTime,
        String consultationType,
        Integer maxAppointments,
        Boolean isActive,
        String notes) {
}
