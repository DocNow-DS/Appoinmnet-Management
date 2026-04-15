# Appoinmnet-Management

## Setup (Appointment Management Service)

### 1) Prerequisites
- Java 17
- Maven 3.9+ (or use the included Maven Wrapper)
- Access to MongoDB (local or Atlas)

### 2) Configure environment variables (recommended)
The service reads these values from `application.properties`:

- `APPOINTMENT_MANAGEMENT_PORT` (default: `8080`)
- `PATIENT_SERVICE_BASE_URL` (default: `http://localhost:8081`)
- `DOCTOR_SERVICE_BASE_URL` (default: `http://localhost:8082`)
- `NOTIFICATION_SERVICE_URL` (default: `http://localhost:8084`)
- `JWT_SECRET` (default: `dev-secret-change-me`)
- `JWT_EXPIRATION_MS` (default: `86400000`)
- `APPOINTMENT_DEFAULT_DURATION_MINUTES` (default: `30`)

MongoDB is configured through:
- `spring.data.mongodb.uri` in `src/main/resources/application.properties`

### 3) Run the service

#### Windows (PowerShell)
```powershell
cd Appoinmnet-Management
.\mvnw.cmd spring-boot:run
```

#### macOS / Linux
```bash
cd Appoinmnet-Management
./mvnw spring-boot:run
```

### 4) Verify it is running
- Health check: `http://localhost:8080/actuator/health`

### 5) Optional: import Postman collection
- File: `postman/appointment-management.postman_collection.json`
- Set `baseUrl` to your running service, e.g. `http://localhost:8080`