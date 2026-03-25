package com.healthcare.appointment.appointment.model;

public enum AppointmentStatus {
    /** Patient booked; waiting for doctor accept / decline / reschedule. */
    PENDING,
    /** Doctor confirmed the slot. */
    ACCEPTED,
    /** Doctor declined this booking. */
    DECLINED,
    /** Doctor asked patient to pick another time (optional proposal stored on entity). */
    RESCHEDULE_REQUESTED,
    /** Patient cancelled. */
    CANCELLED,
    /** Visit completed. */
    COMPLETED
}
