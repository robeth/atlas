# Atlas Project - AI Coding Instructions

Atlas is a Spring Boot 3.5.4 multi-tenant application using **Spring Modulith** architecture for modular boundaries and event-driven communication.

## Architecture Overview

### Modular Structure (Spring Modulith)
- **Package-based modules**: Each business domain is a separate package (e.g., `tenant/`, `common/`)
- **Module boundaries**: Modules communicate via events, not direct dependencies
- **Domain isolation**: Each module contains its own `domain/`, `service/`, `repository/`, `controller/`, `dto/`, `exception/` structure

### Technology Stack
- **Spring Boot 3.5.4** with Java 17
- **Spring Modulith 1.4.1** for modular architecture
- **Flyway** for database migrations (schema-first approach)
- **H2** (dev) / **MySQL** (prod) with JPA/Hibernate
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for API documentation

## Key Patterns & Conventions

### 1. Entity Design Pattern
```java
@Entity
@Table(name = "tenants")
@SQLDelete(sql = "UPDATE tenants SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Tenant {
    // Soft delete, audit fields, optimistic locking
    @Version private Long version;
    private LocalDateTime createdAt, updatedAt, deletedAt;
}
```

### 2. Service Layer Pattern
- `@Transactional(readOnly = true)` on class, `@Transactional` on write methods
- Constructor injection with `@RequiredArgsConstructor`
- Domain exceptions: `TenantNotFoundException`, `TenantCodeAlreadyExistsException`
- Response mapping: `return TenantResponse.from(entity)`

### 3. Controller Conventions
- REST endpoints: `/api/v1/{module}/` pattern
- Request/Response DTOs separate from entities
- Global exception handling in `common/exception/GlobalExceptionHandler`
- Comprehensive logging: `log.info("POST /api/v1/tenants - Creating tenant with code: {}", request.getCode())`

### 4. Database Migrations
- Flyway naming: `V1__Create_tenant_table.sql`
- **Critical**: Include indexes for search fields
- **Required**: Event publication table for Spring Modulith events
- Schema-first approach: `spring.jpa.hibernate.ddl-auto=none`

## Development Workflows

### Running Tests
```bash
./gradlew test                    # All tests
./gradlew test --tests *Service*  # Service layer only
./gradlew test --tests *Integration* # Integration tests only
```

### Database Access
- H2 Console: `http://localhost:8080/h2-console` (dev only)
- Connection: `jdbc:h2:mem:testdb`, user: `sa`, password: `password`

### Build & Run
```bash
./gradlew bootRun    # Start application
./gradlew build      # Full build with tests
```

## Testing Conventions

### Unit Tests (`*Test.java`)
- **MockitoExtension**: `@ExtendWith(MockitoExtension.class)`
- **Pattern**: `@Mock` dependencies, `@InjectMocks` service under test
- **Assertions**: AssertJ (`assertThat().isEqualTo()`)
- **Test structure**: Given/When/Then comments

### Integration Tests (`*IntegrationTest.java`)
- **Full context**: `@SpringBootTest` with `@AutoConfigureWebMvc`
- **Test profiles**: `@ActiveProfiles("test")`
- **Database cleanup**: `@Sql(executionPhase = BEFORE_TEST_METHOD, statements = "DELETE FROM tenants")`
- **MockMvc**: Use `WebApplicationContext` setup, not `@WebMvcTest`

## Multi-Tenant Architecture Notes

### Current State
- **Single tenant setup** with `Tenant` entity representing organizations
- **Future extensibility**: All resources will link to tenants via foreign keys
- **Soft delete**: Use `@SQLDelete` and `@Where` annotations consistently

### Adding New Modules
1. Create package structure: `domain/`, `service/`, `repository/`, `controller/`, `dto/`, `exception/`
2. Add Flyway migration with proper indexes
3. Link to tenant via foreign key: `tenant_id BIGINT NOT NULL`
4. Follow established patterns for DTOs, exceptions, and response mapping

## Common Gotchas

1. **Flyway + Spring Modulith**: Must include `event_publication` table in migration
2. **Test database cleanup**: Use `@Sql` statements, not `repository.deleteAll()`
3. **Builder defaults**: Use `@Builder.Default` for fields with initial values
4. **Security bypass**: Add new endpoints to `SecurityConfiguration` for development
5. **Table naming**: Use plural form (`tenants` not `tenant`) - check entity `@Table` annotation

## API Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI spec**: `http://localhost:8080/v3/api-docs`
