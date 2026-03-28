package com.healthcare.appointment.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Binds the servlet container to {@code appointment.management.port} only.
 * <p>
 * Spring maps env {@code SERVER_PORT} to {@code server.port}; if that variable is set globally in the IDE
 * (e.g. for another microservice), it would otherwise move this app off 8080. This customizer applies after
 * defaults and sets the port explicitly from our property. Override with env {@code APPOINTMENT_MANAGEMENT_PORT}.
 */
@Configuration
public class AppointmentListenPortCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public static final String PROPERTY = "appointment.management.port";

    private final Environment environment;

    public AppointmentListenPortCustomizer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        int port = environment.getProperty(PROPERTY, Integer.class, 8080);
        factory.setPort(port);
    }
}
