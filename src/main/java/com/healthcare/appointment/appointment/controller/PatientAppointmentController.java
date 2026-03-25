package com.healthcare.appointment.appointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.appointment.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.appointment.dto.CreateAppointmentRequest;
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
    public AppointmentResponse create(
            @RequestHeader(X_PATIENT_ID) String patientId,
            @RequestBody CreateAppointmentRequest request) {
        return appointmentService.createForPatient(patientId, request);
    }

    @GetMapping
    public List<AppointmentResponse> list(@RequestHeader(X_PATIENT_ID) String patientId) {
        return appointmentService.listForPatient(patientId);
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(
            @RequestHeader(X_PATIENT_ID) String patientId,
            @PathVariable String id) {
        return appointmentService.getForPatient(patientId, id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @RequestHeader(X_PATIENT_ID) String patientId,
            @PathVariable String id) {
        return appointmentService.cancelForPatient(patientId, id);
    }

    @PatchMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @RequestHeader(X_PATIENT_ID) String patientId,
            @PathVariable String id,
            @RequestBody PatientRescheduleRequest request) {
        return appointmentService.rescheduleForPatient(patientId, id, request);
    }

    @PostMapping("/{id}/accept-doctor-proposal")
    public AppointmentResponse acceptDoctorProposal(
            @RequestHeader(X_PATIENT_ID) String patientId,
            @PathVariable String id) {
        return appointmentService.acceptDoctorProposal(patientId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(X_PATIENT_ID) String patientId, @PathVariable String id) {
        appointmentService.deleteForPatient(patientId, id);
    }
}
