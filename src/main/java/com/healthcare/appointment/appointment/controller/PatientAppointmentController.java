package com.healthcare.appointment.appointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import com.healthcare.appointment.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.appointment.dto.CreateAppointmentRequest;
import com.healthcare.appointment.appointment.dto.DeleteMessageResponse;
import com.healthcare.appointment.appointment.dto.PatientRescheduleRequest;
import com.healthcare.appointment.appointment.service.AppointmentService;
@RestController
@RequestMapping("/api/patient/appointments")
public class PatientAppointmentController {

    public static final String X_PATIENT_ID = "X-Patient-Id";

    private final AppointmentService appointmentService;

    public PatientAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@RequestBody CreateAppointmentRequest request, Authentication authentication) {
        String patientId = authentication.getName();
        return appointmentService.createForPatient(patientId, request);
    }

    @GetMapping
    public List<AppointmentResponse> list(Authentication authentication) {
        String patientId = authentication.getName();
        return appointmentService.listForPatient(patientId);
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(
            @PathVariable String id,
            Authentication authentication) {
        String patientId = authentication.getName();
        return appointmentService.getForPatient(patientId, id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @PathVariable String id,
            Authentication authentication) {
        String patientId = authentication.getName();
        return appointmentService.cancelForPatient(patientId, id);
    }

    @PatchMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @PathVariable String id,
            @RequestBody PatientRescheduleRequest request,
            Authentication authentication) {
        String patientId = authentication.getName();
        return appointmentService.rescheduleForPatient(patientId, id, request);
    }

    @PostMapping("/{id}/accept-doctor-proposal")
    public AppointmentResponse acceptDoctorProposal(
            @PathVariable String id,
            Authentication authentication) {
        String patientId = authentication.getName();
        return appointmentService.acceptDoctorProposal(patientId, id);
    }

    @DeleteMapping("/{id}")
    public DeleteMessageResponse delete(@PathVariable String id, Authentication authentication) {
        String patientId = authentication.getName();
        appointmentService.deleteForPatient(patientId, id);
        return DeleteMessageResponse.appointmentDeleted();
    }
}
