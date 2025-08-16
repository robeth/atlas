package com.aozorastudio.atlas.tenant.controller;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import com.aozorastudio.atlas.tenant.dto.CreateTenantRequest;
import com.aozorastudio.atlas.tenant.dto.UpdateTenantRequest;
import com.aozorastudio.atlas.tenant.repository.TenantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = "DELETE FROM tenants")
@DisplayName("Tenant Controller Integration Tests")
class TenantControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Don't clear the database, let Flyway handle schema
    }

    @Test
    @DisplayName("Should create tenant successfully")
    void shouldCreateTenantSuccessfully() throws Exception {
        // Given
        CreateTenantRequest request = CreateTenantRequest.builder()
                .code("TEST001")
                .name("Test Tenant")
                .description("Test Description")
                .email("test@example.com")
                .phone("1234567890")
                .address("Test Address")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("TEST001")))
                .andExpect(jsonPath("$.name", is("Test Tenant")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        // Verify in database
        assertThat(tenantRepository.count()).isEqualTo(1);
        assertThat(tenantRepository.existsByCode("TEST001")).isTrue();
    }

    @Test
    @DisplayName("Should return 409 when creating tenant with existing code")
    void shouldReturn409WhenCreatingTenantWithExistingCode() throws Exception {
        // Given
        Tenant existingTenant = Tenant.builder()
                .code("TEST001")
                .name("Existing Tenant")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        tenantRepository.save(existingTenant);

        CreateTenantRequest request = CreateTenantRequest.builder()
                .code("TEST001")
                .name("New Tenant")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Should return 400 for invalid input")
    void shouldReturn400ForInvalidInput() throws Exception {
        // Given
        CreateTenantRequest request = CreateTenantRequest.builder()
                .code("") // Invalid empty code
                .name("") // Invalid empty name
                .email("invalid-email") // Invalid email format
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("Should update tenant successfully")
    void shouldUpdateTenantSuccessfully() throws Exception {
        // Given
        Tenant tenant = Tenant.builder()
                .code("TEST001")
                .name("Original Name")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        Tenant savedTenant = tenantRepository.save(tenant);

        UpdateTenantRequest request = UpdateTenantRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .email("updated@example.com")
                .status(Tenant.TenantStatus.INACTIVE)
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/tenants/{id}", savedTenant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent tenant")
    void shouldReturn404WhenUpdatingNonExistentTenant() throws Exception {
        // Given
        UpdateTenantRequest request = UpdateTenantRequest.builder()
                .name("Updated Name")
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/tenants/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @DisplayName("Should get tenant by id successfully")
    void shouldGetTenantByIdSuccessfully() throws Exception {
        // Given
        Tenant tenant = Tenant.builder()
                .code("TEST001")
                .name("Test Tenant")
                .email("test@example.com")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        Tenant savedTenant = tenantRepository.save(tenant);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/{id}", savedTenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedTenant.getId().intValue())))
                .andExpect(jsonPath("$.code", is("TEST001")))
                .andExpect(jsonPath("$.name", is("Test Tenant")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    @DisplayName("Should get tenant by code successfully")
    void shouldGetTenantByCodeSuccessfully() throws Exception {
        // Given
        Tenant tenant = Tenant.builder()
                .code("TEST001")
                .name("Test Tenant")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        tenantRepository.save(tenant);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/code/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("TEST001")))
                .andExpect(jsonPath("$.name", is("Test Tenant")));
    }

    @Test
    @DisplayName("Should search tenants with pagination")
    void shouldSearchTenantsWithPagination() throws Exception {
        // Given
        Tenant tenant1 = Tenant.builder()
                .code("TEST001")
                .name("First Tenant")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        Tenant tenant2 = Tenant.builder()
                .code("TEST002")
                .name("Second Tenant")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        tenantRepository.save(tenant1);
        tenantRepository.save(tenant2);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants")
                .param("searchTerm", "Tenant")
                .param("status", "ACTIVE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[*].status", everyItem(is("ACTIVE"))));
    }

    @Test
    @DisplayName("Should delete tenant successfully")
    void shouldDeleteTenantSuccessfully() throws Exception {
        // Given
        Tenant tenant = Tenant.builder()
                .code("TEST001")
                .name("Test Tenant")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        Tenant savedTenant = tenantRepository.save(tenant);

        // When & Then
        mockMvc.perform(delete("/api/v1/tenants/{id}", savedTenant.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete
        assertThat(tenantRepository.findById(savedTenant.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should check tenant existence by code")
    void shouldCheckTenantExistenceByCode() throws Exception {
        // Given
        Tenant tenant = Tenant.builder()
                .code("TEST001")
                .name("Test Tenant")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        tenantRepository.save(tenant);

        // When & Then - exists
        mockMvc.perform(get("/api/v1/tenants/exists/TEST001"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // When & Then - does not exist
        mockMvc.perform(get("/api/v1/tenants/exists/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
