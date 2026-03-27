package com.healthcare.appointment.doctor.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.healthcare.appointment.shared.exception.BadRequestException;
import com.healthcare.appointment.shared.exception.NotFoundException;

@Component
public class DoctorApiClient {

    private final RestTemplate restTemplate;
    private final String doctorServiceBaseUrl;

    public DoctorApiClient(
            RestTemplate restTemplate,
            @Value("${services.doctor.base-url}") String doctorServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.doctorServiceBaseUrl = doctorServiceBaseUrl;
    }

    public DoctorProfileResponse getDoctorById(String doctorId) {
        try {
            return restTemplate.getForObject(
                    doctorServiceBaseUrl + "/api/doctors/{id}",
                    DoctorProfileResponse.class,
                    doctorId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Doctor not found: " + doctorId);
        } catch (RestClientException ex) {
            if (ex instanceof HttpClientErrorException httpEx && httpEx.getStatusCode().value() == 404) {
                throw new NotFoundException("Doctor not found: " + doctorId);
            }
            throw new BadRequestException("Doctor service unavailable for id: " + doctorId);
        }
    }

    public List<DoctorAvailabilityResponse> getAvailabilityByDoctorId(String doctorId) {
        try {
            DoctorAvailabilityResponse[] response = restTemplate.getForObject(
                    doctorServiceBaseUrl + "/api/availability/user/{userId}",
                    DoctorAvailabilityResponse[].class,
                    doctorId);
            return response == null ? List.of() : Arrays.asList(response);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Doctor availability not found for id: " + doctorId);
        } catch (RestClientException ex) {
            if (ex instanceof HttpClientErrorException httpEx && httpEx.getStatusCode().value() == 404) {
                throw new NotFoundException("Doctor availability not found for id: " + doctorId);
            }
            throw new BadRequestException("Doctor availability service unavailable for id: " + doctorId);
        }
    }
}
