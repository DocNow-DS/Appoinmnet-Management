package com.healthcare.appointment.appointment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.appointment.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.appointment.dto.DoctorActionRequest;
import com.healthcare.appointment.appointment.model.AppointmentStatus;
import com.healthcare.appointment.appointment.service.AppointmentService;

@RestController
@RequestMapping("/api/doctor/appointments")
public class DoctorAppointmentController {

    public static final String X_DOCTOR_ID = "X-Doctor-Id";

    private final AppointmentService appointmentService;

    public DoctorAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentResponse> list(
            @RequestHeader(X_DOCTOR_ID) String doctorId,
            @RequestParam(required = false) AppointmentStatus status) {
        return appointmentService.listForDoctor(doctorId, status);
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(
            @RequestHeader(X_DOCTOR_ID) String doctorId,
            @PathVariable String id) {
        return appointmentService.getForDoctor(doctorId, id);
    }

    @PostMapping("/{id}/action")
    public AppointmentResponse action(
            @RequestHeader(X_DOCTOR_ID) String doctorId,
            @PathVariable String id,
            @RequestBody DoctorActionRequest request) {
        return appointmentService.doctorAct(doctorId, id, request);
    }

    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(
            @RequestHeader(X_DOCTOR_ID) String doctorId,
            @PathVariable String id) {
        return appointmentService.markCompletedForDoctor(doctorId, id);
    }
}
