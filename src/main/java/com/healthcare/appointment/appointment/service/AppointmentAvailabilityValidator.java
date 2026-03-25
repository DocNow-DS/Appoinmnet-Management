package com.healthcare.appointment.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.healthcare.appointment.doctor.client.DoctorAvailabilityResponse;
import com.healthcare.appointment.shared.exception.BadRequestException;

@Component
public class AppointmentAvailabilityValidator {

    private static final DateTimeFormatter[] TIME_FORMATS =
            new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_TIME,
                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("H:mm:ss"),
            };

    public void ensureSlotInAvailability(
            List<DoctorAvailabilityResponse> availability,
            LocalDateTime start,
            LocalDateTime end,
            String requestedConsultationType) {
        // if (availability == null || availability.isEmpty()) {
        //     throw new BadRequestException("Doctor has no availability configured");
        // }

        DayOfWeek day = start.getDayOfWeek();
        String dayName = day.name();
        LocalTime reqStart = start.toLocalTime();
        LocalTime reqEnd = end.toLocalTime();

        if (!end.isAfter(start)) {
            throw new BadRequestException("End time must be after start time");
        }

        boolean matchesSlot = false;
        for (DoctorAvailabilityResponse slot : availability) {
            if (!Boolean.TRUE.equals(slot.isActive())) {
                continue;
            }
            if (slot.dayOfWeek() == null || !dayName.equalsIgnoreCase(slot.dayOfWeek().trim())) {
                continue;
            }
            LocalTime wStart = parseTime(slot.startTime());
            LocalTime wEnd = parseTime(slot.endTime());
            if (reqStart.isBefore(wStart) || reqEnd.isAfter(wEnd) || !reqEnd.isAfter(reqStart)) {
                continue;
            }
            if (!consultationMatches(slot.consultationType(), requestedConsultationType)) {
                continue;
            }
            matchesSlot = true;
            break;
        }

        // if (!matchesSlot) {
        //     throw new BadRequestException(
        //             "Requested time is outside doctor availability or consultation type does not match");
        // }
    }

    private static boolean consultationMatches(String slotType, String requestType) {
        if (slotType == null || slotType.isBlank()) {
            return true;
        }
        if ("BOTH".equalsIgnoreCase(slotType.trim())) {
            return true;
        }
        if (requestType == null || requestType.isBlank()) {
            return true;
        }
        String r = requestType.trim().toUpperCase();
        String s = slotType.trim().toUpperCase();
        if (s.equals(r)) {
            return true;
        }
        if ("IN_PERSON".equals(s) && ("OFFLINE".equals(r) || "IN_PERSON".equals(r))) {
            return true;
        }
        if ("OFFLINE".equals(s) && ("OFFLINE".equals(r) || "IN_PERSON".equals(r))) {
            return true;
        }
        return false;
    }

    private static LocalTime parseTime(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException("Invalid doctor availability: empty time");
        }
        String t = raw.trim();
        for (DateTimeFormatter f : TIME_FORMATS) {
            try {
                return LocalTime.parse(t, f);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new BadRequestException("Invalid doctor availability time format: " + raw);
    }
}
