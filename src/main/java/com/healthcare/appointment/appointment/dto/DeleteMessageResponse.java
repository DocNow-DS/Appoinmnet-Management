package com.healthcare.appointment.appointment.dto;

public class DeleteMessageResponse {

    private String message;

    public DeleteMessageResponse() {
    }

    public DeleteMessageResponse(String message) {
        this.message = message;
    }

    public static DeleteMessageResponse appointmentDeleted() {
        return new DeleteMessageResponse("Successfully deleted");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
