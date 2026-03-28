package com.healthcare.appointment.booking;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.healthcare.appointment.doctor.client.DoctorProfileResponse;
import com.healthcare.appointment.shared.exception.BadRequestException;
import com.healthcare.appointment.shared.exception.NotFoundException;

/**
 * Loads doctor directory data from patient-management (source of user/doctor profiles).
 */
@Component
public class PatientPublicDoctorClient {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final String patientServiceBaseUrl;

    public PatientPublicDoctorClient(
            RestTemplate restTemplate,
            @Value("${services.patient.base-url}") String patientServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.patientServiceBaseUrl = patientServiceBaseUrl;
    }

    public List<DoctorProfileResponse> listDoctors(String specialtyFilter) {
        try {
            String url = patientServiceBaseUrl + "/api/public/doctors";
            if (specialtyFilter != null && !specialtyFilter.isBlank()) {
                url += "?specialty=" + URLEncoder.encode(specialtyFilter.trim(), StandardCharsets.UTF_8);
            }
            DoctorProfileResponse[] body = restTemplate.getForObject(url, DoctorProfileResponse[].class);
            return body == null ? List.of() : Arrays.asList(body);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 404) {
                String msg = parseJsonMessage(ex.getResponseBodyAsString());
                throw new NotFoundException(
                        msg != null
                                ? msg
                                : "No doctors found for this search. Try another specialty or All.");
            }
            throw new BadRequestException(
                    "Patient service returned an error ("
                            + ex.getStatusCode().value()
                            + "): "
                            + ex.getMessage());
        } catch (RestClientException ex) {
            throw new BadRequestException(
                    "Patient service doctor directory unavailable. Is patient-management running on "
                            + patientServiceBaseUrl
                            + "? "
                            + ex.getMessage());
        }
    }

    private static String parseJsonMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            JsonNode n = JSON.readTree(responseBody);
            if (n.has("message") && n.get("message").isTextual()) {
                return n.get("message").asText();
            }
            if (n.has("error") && n.get("error").isTextual()) {
                return n.get("error").asText();
            }
        } catch (Exception ignored) {
            // fall through
        }
        return null;
    }
}
