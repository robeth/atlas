# Atlas Project

Atlas is a Spring Boot 3.5.4 multi-tenant application using Spring Modulith architecture for modular boundaries and event-driven communication.

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17 (for local development)
- Git

### Running with Docker Compose

1. **Development Services Only** (MySQL, Redis, MailHog)
```bash
# Start supporting services
docker-compose -f docker-compose.yml up -d

# Run the application locally
./gradlew bootRun --args='--spring.profiles.active=dev'
```

2. **Full Stack with Docker**
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f atlas-app

# Stop services
docker-compose down
```

### Local Development

```bash
# Start supporting services
docker-compose -f docker-compose.dev.yml up -d

# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Access services
# Application: http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
# Swagger UI: http://localhost:8080/swagger-ui.html
# MailHog UI: http://localhost:8025
```

### Available Services

| Service | Local Port | Docker Port | Purpose |
|---------|------------|-------------|---------|
| Atlas App | 8080 | 8080 | Main application |
| MySQL | 3306 | 3306 | Primary database |
| Redis | 6379 | 6379 | Cache & sessions |
| MailHog SMTP | 1025 | 1025 | Email testing |
| MailHog UI | 8025 | 8025 | Email web interface |

### API Endpoints

- **Health Check**: `GET /actuator/health`
- **API Documentation**: `GET /swagger-ui.html`
- **Tenants**: `GET|POST|PUT|DELETE /api/v1/tenants`

### Database Access

**H2 (Development)**
- Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

**MySQL (Production)**
- Host: `localhost:3306`
- Database: `atlas`
- Username: `atlas`
- Password: `atlas123`

## Deployment

### Docker Build

```bash
# Build image
docker build -t atlas:latest .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:mysql://mysql:3306/atlas \
  atlas:latest
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile |
| `DATABASE_URL` | H2 in-memory | Database JDBC URL |
| `DATABASE_USERNAME` | `atlas` | Database username |
| `DATABASE_PASSWORD` | `atlas123` | Database password |
| `REDIS_HOST` | `redis` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `MAIL_HOST` | `mailhog` | SMTP hostname |
| `MAIL_PORT` | `1025` | SMTP port |

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

## Monitoring

### Health Checks

- **Application**: `GET /actuator/health`
- **Database**: `GET /actuator/health/db`
- **Redis**: `GET /actuator/health/redis`

### Metrics

- **Prometheus**: `GET /actuator/prometheus`
- **Metrics**: `GET /actuator/metrics`


## License

This project is licensed under the MIT License.
