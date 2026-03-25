package com.healthcare.appointment.appointment.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.appointment.appointment.model.Appointment;
import com.healthcare.appointment.appointment.model.AppointmentStatus;
import com.healthcare.appointment.appointment.repository.AppointmentRepository;

@RestController
@RequestMapping("/api/dev/appointments")
public class AppointmentSeedController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentSeedController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping("/seed")
    public Map<String, Object> seedOneAppointment() {
        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = new Appointment();
        appointment.setPatientId("sample-patient-id");
        appointment.setDoctorId("sample-doctor-id");
        appointment.setStartTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        appointment.setEndTime(now.plusDays(1).withHour(10).withMinute(30).withSecond(0).withNano(0));
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setConsultationType("OFFLINE");
        appointment.setNotes("Seed appointment for Mongo collection creation");
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);

        Appointment saved = appointmentRepository.save(appointment);

        return Map.of(
                "message", "Sample appointment inserted",
                "appointmentId", saved.getId());
    }
}
