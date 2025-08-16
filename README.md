# Atlas Project

Atlas is a Spring Boot 3.5.4 multi-tenant application using Spring Modulith architecture for modular boundaries and event-driven communication.

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17 (for local development)
- Git

### Running with Docker Compose

**Development Services Only** (MySQL, Redis, MailHog)
```bash
# Start supporting services
docker-compose -f docker-compose.yml up -d

# Run the application locally
./gradlew bootRun
```


### Available Services

| Service | Local Port | Docker Port | Purpose |
|---------|------------|-------------|---------|
| Atlas App | 8080 | 8080 | Main application |
| MySQL | 3306 | 3306 | Primary database |
| Redis | 6379 | 6379 | Cache & sessions |
| MailHog SMTP | 1025 | 1025 | Email testing |
| MailHog UI | 8025 | 8025 | Email web interface |

### Important Endpoints

- **Health Check**: `GET /actuator/health`
- **API Documentation**: `GET /swagger-ui.html``

### Build

```bash
# Build image
docker build -t atlas:latest .


## Development

### Project Structure

```
src/
├── main/java/com/aozorastudio/atlas/
│   ├── tenant/          # Tenant module
│   │   ├── controller/  # REST controllers
│   │   ├── service/     # Business logic
│   │   ├── repository/  # Data access
│   │   ├── domain/      # Entities
│   │   ├── dto/         # Data transfer objects
│   │   └── exception/   # Custom exceptions
│   └── common/          # Shared components
│       ├── configuration/
│       └── exception/
└── test/               # Test classes
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests TenantServiceTest

# Run with coverage
./gradlew test jacocoTestReport

# Run integration tests only
./gradlew test --tests *IntegrationTest
```

### Code Quality

```bash
# Run security check
./gradlew dependencyCheckAnalyze

# Check code coverage
./gradlew jacocoTestCoverageVerification
```

## License

This project is licensed under the MIT License.
