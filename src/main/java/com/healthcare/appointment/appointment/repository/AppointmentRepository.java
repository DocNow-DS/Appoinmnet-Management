package com.healthcare.appointment.appointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.healthcare.appointment.appointment.model.Appointment;
import com.healthcare.appointment.appointment.model.AppointmentStatus;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByDoctorId(String doctorId);

    List<Appointment> findByDoctorIdAndStatus(String doctorId, AppointmentStatus status);

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByDoctorIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            String doctorId,
            List<AppointmentStatus> statuses,
            LocalDateTime endExclusive,
            LocalDateTime startExclusive);

    List<Appointment> findByPatientIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            String patientId,
            List<AppointmentStatus> statuses,
            LocalDateTime endExclusive,
            LocalDateTime startExclusive);
}
