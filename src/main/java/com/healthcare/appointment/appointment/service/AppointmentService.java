package com.healthcare.appointment.appointment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.healthcare.appointment.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.appointment.dto.CreateAppointmentRequest;
import com.healthcare.appointment.appointment.dto.DoctorActionRequest;
import com.healthcare.appointment.appointment.dto.PatientRescheduleRequest;
import com.healthcare.appointment.appointment.model.Appointment;
import com.healthcare.appointment.appointment.model.AppointmentStatus;
import com.healthcare.appointment.appointment.repository.AppointmentRepository;
import com.healthcare.appointment.doctor.client.DoctorApiClient;
import com.healthcare.appointment.doctor.client.DoctorProfileResponse;
import com.healthcare.appointment.notification.client.NotificationServiceClient;
import com.healthcare.appointment.shared.exception.BadRequestException;
import com.healthcare.appointment.shared.exception.ConflictException;
import com.healthcare.appointment.shared.exception.ForbiddenException;
import com.healthcare.appointment.shared.exception.NotFoundException;

@Service
public class AppointmentService {

    private static final List<AppointmentStatus> BLOCKING_OVERLAP_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.ACCEPTED,
            AppointmentStatus.RESCHEDULE_REQUESTED);

    private final AppointmentRepository appointmentRepository;
    private final DoctorApiClient doctorApiClient;
    private final AppointmentAvailabilityValidator availabilityValidator;
    private final NotificationServiceClient notificationServiceClient;
    private final int defaultDurationMinutes;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorApiClient doctorApiClient,
            AppointmentAvailabilityValidator availabilityValidator,
            NotificationServiceClient notificationServiceClient,
            @Value("${appointment.default-duration-minutes:30}") int defaultDurationMinutes) {
        this.appointmentRepository = appointmentRepository;
        this.doctorApiClient = doctorApiClient;
        this.availabilityValidator = availabilityValidator;
        this.notificationServiceClient = notificationServiceClient;
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public AppointmentResponse createForPatient(String patientId, CreateAppointmentRequest request) {
        if (patientId == null || patientId.isBlank()) {
            throw new BadRequestException("Patient ID could not be extracted from token");
        }
        if (request.doctorId() == null || request.doctorId().isBlank()) {
            throw new BadRequestException("doctorId is required");
        }
        if (request.startTime() == null) {
            throw new BadRequestException("startTime is required");
        }

        LocalDateTime start = request.startTime();
        int duration = request.durationMinutes() != null && request.durationMinutes() > 0
                ? request.durationMinutes()
                : defaultDurationMinutes;
        LocalDateTime end = start.plusMinutes(duration);

        ensureStartInFuture(start);
        DoctorProfileResponse doctor = doctorApiClient.getDoctorById(request.doctorId());
        if (Boolean.FALSE.equals(doctor.isActive())) {
            throw new BadRequestException("Doctor is not active");
        }

        var availability = doctorApiClient.getAvailabilityByDoctorId(request.doctorId());
        availabilityValidator.ensureSlotInAvailability(
                availability, start, end, request.consultationType());

        assertNoOverlapForDoctor(request.doctorId(), start, end, null);
        assertNoOverlapForPatient(patientId, start, end, null);

        LocalDateTime now = LocalDateTime.now();
        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setDoctorId(request.doctorId());
        a.setStartTime(start);
        a.setEndTime(end);
        a.setStatus(AppointmentStatus.PENDING);
        a.setConsultationType(request.consultationType());
        a.setNotes(request.notes());
        a.setCreatedAt(now);
        a.setUpdatedAt(now);

        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    public List<AppointmentResponse> listForPatient(String patientId) {
        requireNonBlank(patientId, "X-Patient-Id");
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    public AppointmentResponse getForPatient(String patientId, String appointmentId) {
        requireNonBlank(patientId, "X-Patient-Id");
        Appointment a = getById(appointmentId);
        assertPatientOwns(a, patientId);
        return AppointmentResponse.from(a);
    }

    public AppointmentResponse cancelForPatient(String patientId, String appointmentId) {
        Appointment a = getById(appointmentId);
        assertPatientOwns(a, patientId);
        if (a.getStatus() == AppointmentStatus.CANCELLED
                || a.getStatus() == AppointmentStatus.DECLINED
                || a.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Appointment cannot be cancelled in current status: " + a.getStatus());
        }
        a.setStatus(AppointmentStatus.CANCELLED);
        a.setUpdatedAt(LocalDateTime.now());
        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    public AppointmentResponse rescheduleForPatient(
            String patientId, String appointmentId, PatientRescheduleRequest request) {
        Appointment a = getById(appointmentId);
        assertPatientOwns(a, patientId);
        if (a.getStatus() == AppointmentStatus.CANCELLED
                || a.getStatus() == AppointmentStatus.DECLINED
                || a.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot reschedule in status: " + a.getStatus());
        }
        if (request.startTime() == null) {
            throw new BadRequestException("startTime is required");
        }
        LocalDateTime start = request.startTime();
        int duration = request.durationMinutes() != null && request.durationMinutes() > 0
                ? request.durationMinutes()
                : (int) java.time.Duration.between(a.getStartTime(), a.getEndTime()).toMinutes();
        if (duration <= 0) {
            duration = defaultDurationMinutes;
        }
        LocalDateTime end = start.plusMinutes(duration);
        ensureStartInFuture(start);

        var availability = doctorApiClient.getAvailabilityByDoctorId(a.getDoctorId());
        availabilityValidator.ensureSlotInAvailability(
                availability, start, end, a.getConsultationType());

        assertNoOverlapForDoctor(a.getDoctorId(), start, end, appointmentId);
        assertNoOverlapForPatient(patientId, start, end, appointmentId);

        a.setStartTime(start);
        a.setEndTime(end);
        if (request.notes() != null) {
            a.setNotes(request.notes());
        }
        a.setProposedStartTime(null);
        a.setProposedEndTime(null);
        a.setDoctorMessage(null);
        a.setStatus(AppointmentStatus.PENDING);
        a.setUpdatedAt(LocalDateTime.now());

        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    public AppointmentResponse acceptDoctorProposal(String patientId, String appointmentId) {
        Appointment a = getById(appointmentId);
        assertPatientOwns(a, patientId);
        if (a.getStatus() != AppointmentStatus.RESCHEDULE_REQUESTED) {
            throw new BadRequestException("No doctor reschedule proposal to accept");
        }
        if (a.getProposedStartTime() == null || a.getProposedEndTime() == null) {
            throw new BadRequestException("Doctor did not include proposed start/end times");
        }
        a.setStartTime(a.getProposedStartTime());
        a.setEndTime(a.getProposedEndTime());
        a.setProposedStartTime(null);
        a.setProposedEndTime(null);
        a.setDoctorMessage(null);
        a.setStatus(AppointmentStatus.ACCEPTED);
        a.setUpdatedAt(LocalDateTime.now());
        
        notificationServiceClient.sendAppointmentApprovedNotification(a.getPatientId(), a.getId(), a.getStartTime().toString(), null)
            .block();
            
        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    public void deleteForPatient(String patientId, String appointmentId) {
        Appointment a = getById(appointmentId);
        assertPatientOwns(a, patientId);
        appointmentRepository.deleteById(appointmentId);
    }

    public List<AppointmentResponse> listForDoctor(String doctorId, AppointmentStatus statusFilter) {
        requireNonBlank(doctorId, "X-Doctor-Id");
        List<Appointment> list = statusFilter == null
                ? appointmentRepository.findByDoctorId(doctorId)
                : appointmentRepository.findByDoctorIdAndStatus(doctorId, statusFilter);
        return list.stream().map(AppointmentResponse::from).toList();
    }

    public AppointmentResponse getForDoctor(String doctorId, String appointmentId) {
        requireNonBlank(doctorId, "X-Doctor-Id");
        Appointment a = getById(appointmentId);
        assertDoctorOwns(a, doctorId);
        return AppointmentResponse.from(a);
    }

    public AppointmentResponse doctorAct(String doctorId, String appointmentId, DoctorActionRequest request, String authorization) {
        requireNonBlank(doctorId, "X-Doctor-Id");
        if (request == null || request.action() == null) {
            throw new BadRequestException("action is required (ACCEPT, DECLINE, REQUEST_RESCHEDULE)");
        }

        Appointment a = getById(appointmentId);
        assertDoctorOwns(a, doctorId);
        LocalDateTime now = LocalDateTime.now();

        switch (request.action()) {
            case ACCEPT -> {
                if (a.getStatus() != AppointmentStatus.PENDING
                        && a.getStatus() != AppointmentStatus.RESCHEDULE_REQUESTED) {
                    throw new BadRequestException("Cannot accept in status: " + a.getStatus());
                }
                a.setStatus(AppointmentStatus.ACCEPTED);
                a.setProposedStartTime(null);
                a.setProposedEndTime(null);
                if (request.message() != null) {
                    a.setDoctorMessage(request.message());
                }
                
                notificationServiceClient.sendAppointmentApprovedNotification(a.getPatientId(), a.getId(), a.getStartTime().toString(), authorization)
                    .block();
            }
            case DECLINE -> {
                if (a.getStatus() != AppointmentStatus.PENDING
                        && a.getStatus() != AppointmentStatus.RESCHEDULE_REQUESTED) {
                    throw new BadRequestException("Cannot decline in status: " + a.getStatus());
                }
                a.setStatus(AppointmentStatus.DECLINED);
                a.setDoctorMessage(request.message());
            }
            case REQUEST_RESCHEDULE -> {
                if (a.getStatus() != AppointmentStatus.PENDING && a.getStatus() != AppointmentStatus.ACCEPTED) {
                    throw new BadRequestException("Cannot request reschedule in status: " + a.getStatus());
                }
                if (request.proposedStartTime() != null && request.proposedEndTime() != null) {
                    if (!request.proposedEndTime().isAfter(request.proposedStartTime())) {
                        throw new BadRequestException("proposedEndTime must be after proposedStartTime");
                    }
                    var availability = doctorApiClient.getAvailabilityByDoctorId(a.getDoctorId());
                    availabilityValidator.ensureSlotInAvailability(
                            availability,
                            request.proposedStartTime(),
                            request.proposedEndTime(),
                            a.getConsultationType());
                }
                a.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED);
                a.setDoctorMessage(request.message());
                a.setProposedStartTime(request.proposedStartTime());
                a.setProposedEndTime(request.proposedEndTime());
            }
        }

        a.setUpdatedAt(now);
        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    public AppointmentResponse markCompletedForDoctor(String doctorId, String appointmentId) {
        Appointment a = getById(appointmentId);
        assertDoctorOwns(a, doctorId);
        if (a.getStatus() != AppointmentStatus.ACCEPTED) {
            throw new BadRequestException("Only accepted appointments can be marked completed");
        }
        a.setStatus(AppointmentStatus.COMPLETED);
        a.setUpdatedAt(LocalDateTime.now());
        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    private Appointment getById(String id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
    }

    private static void assertPatientOwns(Appointment a, String patientId) {
        if (!patientId.equals(a.getPatientId())) {
            throw new ForbiddenException("Not allowed to access this appointment");
        }
    }

    private static void assertDoctorOwns(Appointment a, String doctorId) {
        if (!doctorId.equals(a.getDoctorId())) {
            throw new ForbiddenException("Not allowed to access this appointment");
        }
    }

    private void assertNoOverlapForDoctor(String doctorId, LocalDateTime start, LocalDateTime end, String excludeId) {
        var conflicts = appointmentRepository.findByDoctorIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                doctorId, BLOCKING_OVERLAP_STATUSES, end, start);
        boolean overlaps = conflicts.stream().anyMatch(c -> excludeId == null || !excludeId.equals(c.getId()));
        if (overlaps) {
            throw new ConflictException("Doctor already has a booking in this time range");
        }
    }

    private void assertNoOverlapForPatient(String patientId, LocalDateTime start, LocalDateTime end, String excludeId) {
        var conflicts = appointmentRepository.findByPatientIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                patientId, BLOCKING_OVERLAP_STATUSES, end, start);
        boolean overlaps = conflicts.stream().anyMatch(c -> excludeId == null || !excludeId.equals(c.getId()));
        if (overlaps) {
            throw new ConflictException("You already have another booking in this time range");
        }
    }

    private static void ensureStartInFuture(LocalDateTime start) {
        if (!start.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("startTime must be in the future");
        }
    }

    private static void requireNonBlank(String v, String name) {
        if (v == null || v.isBlank()) {
            throw new BadRequestException("Missing header " + name);
        }
    }
}
