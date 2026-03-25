package com.healthcare.appointment.appointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.oauth2.jwt.Jwt;

import com.healthcare.appointment.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.appointment.dto.CreateAppointmentRequest;
import com.healthcare.appointment.appointment.dto.PatientRescheduleRequest;
import com.healthcare.appointment.appointment.service.AppointmentService;

@RestController
@RequestMapping("/api/patient/appointments")
public class PatientAppointmentController {

    private final AppointmentService appointmentService;

    public PatientAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateAppointmentRequest request) {
        String patientId = requirePatientId(jwt);
        return appointmentService.createForPatient(patientId, request);
    }

    private static String requirePatientId(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT token");
        }
        String patientId = jwt.getClaimAsString("patientId");
        if (patientId == null || patientId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT missing required claim: patientId");
        }
        return patientId;
    }

    @GetMapping
    public List<AppointmentResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return appointmentService.listForPatient(requirePatientId(jwt));
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        return appointmentService.getForPatient(requirePatientId(jwt), id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        return appointmentService.cancelForPatient(requirePatientId(jwt), id);
    }

    @PatchMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id,
            @RequestBody PatientRescheduleRequest request) {
        return appointmentService.rescheduleForPatient(requirePatientId(jwt), id, request);
    }

    @PostMapping("/{id}/accept-doctor-proposal")
    public AppointmentResponse acceptDoctorProposal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        return appointmentService.acceptDoctorProposal(requirePatientId(jwt), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        appointmentService.deleteForPatient(requirePatientId(jwt), id);
    }
}
