package com.healthcare.appointment.doctor.client;

public record DoctorProfileResponse(
        String id,
        String username,
        String email,
        String name,
        String specialty,
        String licenseNumber,
        Integer yearsOfExperience,
        String qualifications,
        String department,
        String hospitalName,
        String education,
        String about,
        String profileImageUrl,
        Boolean isVerified,
        Boolean enabled) {
        
    /**
     * Returns isActive based on enabled status.
     */
    public Boolean isActive() {
        return enabled;
    }
    
    /**
     * Returns specialization which is the same as specialty.
     */
    public String specialization() {
        return specialty;
    }
}
