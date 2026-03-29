package com.healthcare.appointment.appointment.dto;

public record DeleteMessageResponse(String message) {

    public static DeleteMessageResponse appointmentDeleted() {
        return new DeleteMessageResponse("Successfully deleted");
    }
}
