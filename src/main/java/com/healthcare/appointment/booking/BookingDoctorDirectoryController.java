package com.healthcare.appointment.booking;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.appointment.doctor.client.DoctorProfileResponse;
import com.healthcare.appointment.shared.exception.NotFoundException;

/**
 * Appointment-domain API: discover doctors for booking (by optional specialty filter).
 * Data is sourced from patient-management {@code /api/public/doctors}.
 */
@RestController
@RequestMapping("/api/patient/booking")
public class BookingDoctorDirectoryController {

    private final PatientPublicDoctorClient patientPublicDoctorClient;

    public BookingDoctorDirectoryController(PatientPublicDoctorClient patientPublicDoctorClient) {
        this.patientPublicDoctorClient = patientPublicDoctorClient;
    }

    @GetMapping("/doctors")
    public List<DoctorProfileResponse> listDoctorsForBooking(@RequestParam(required = false) String specialty) {
        List<DoctorProfileResponse> list = patientPublicDoctorClient.listDoctors(specialty);
        if (specialty != null && !specialty.isBlank() && list.isEmpty()) {
            throw new NotFoundException(
                    "No doctors found for specialty \"" + specialty.trim()
                            + "\". Try another specialty or choose All.");
        }
        if ((specialty == null || specialty.isBlank()) && list.isEmpty()) {
            throw new NotFoundException("No doctors are available to book right now.");
        }
        return list;
    }
}
