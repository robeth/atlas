package com.aozorastudio.atlas.tenant.service;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import com.aozorastudio.atlas.tenant.dto.CreateTenantRequest;
import com.aozorastudio.atlas.tenant.dto.TenantResponse;
import com.aozorastudio.atlas.tenant.dto.TenantSearchRequest;
import com.aozorastudio.atlas.tenant.dto.UpdateTenantRequest;
import com.aozorastudio.atlas.tenant.exception.TenantCodeAlreadyExistsException;
import com.aozorastudio.atlas.tenant.exception.TenantNotFoundException;
import com.aozorastudio.atlas.tenant.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TenantService Tests")
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    private Tenant mockTenant;
    private CreateTenantRequest createRequest;
    private UpdateTenantRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockTenant = Tenant.builder()
                .id(1L)
                .code("TEST001")
                .name("Test Tenant")
                .description("Test Description")
                .email("test@example.com")
                .phone("1234567890")
                .address("Test Address")
                .status(Tenant.TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .version(0L)
                .build();

        createRequest = CreateTenantRequest.builder()
                .code("TEST001")
                .name("Test Tenant")
                .description("Test Description")
                .email("test@example.com")
                .phone("1234567890")
                .address("Test Address")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        updateRequest = UpdateTenantRequest.builder()
                .name("Updated Tenant")
                .description("Updated Description")
                .email("updated@example.com")
                .phone("0987654321")
                .address("Updated Address")
                .status(Tenant.TenantStatus.INACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should create tenant successfully")
    void shouldCreateTenantSuccessfully() {
        // Given
        when(tenantRepository.existsByCode(createRequest.getCode())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(mockTenant);

        // When
        TenantResponse response = tenantService.createTenant(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(createRequest.getCode());
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        assertThat(response.getEmail()).isEqualTo(createRequest.getEmail());

        verify(tenantRepository).existsByCode(createRequest.getCode());
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should throw exception when creating tenant with existing code")
    void shouldThrowExceptionWhenCreatingTenantWithExistingCode() {
        // Given
        when(tenantRepository.existsByCode(createRequest.getCode())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> tenantService.createTenant(createRequest))
                .isInstanceOf(TenantCodeAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(tenantRepository).existsByCode(createRequest.getCode());
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should update tenant successfully")
    void shouldUpdateTenantSuccessfully() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(mockTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(mockTenant);

        // When
        TenantResponse response = tenantService.updateTenant(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(tenantRepository).findById(1L);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent tenant")
    void shouldThrowExceptionWhenUpdatingNonExistentTenant() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.updateTenant(1L, updateRequest))
                .isInstanceOf(TenantNotFoundException.class)
                .hasMessageContaining("not found");

        verify(tenantRepository).findById(1L);
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should get tenant by id successfully")
    void shouldGetTenantByIdSuccessfully() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(mockTenant));

        // When
        TenantResponse response = tenantService.getTenantById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCode()).isEqualTo("TEST001");

        verify(tenantRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent tenant by id")
    void shouldThrowExceptionWhenGettingNonExistentTenantById() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.getTenantById(1L))
                .isInstanceOf(TenantNotFoundException.class)
                .hasMessageContaining("not found");

        verify(tenantRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get tenant by code successfully")
    void shouldGetTenantByCodeSuccessfully() {
        // Given
        when(tenantRepository.findByCode("TEST001")).thenReturn(Optional.of(mockTenant));

        // When
        TenantResponse response = tenantService.getTenantByCode("TEST001");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("TEST001");

        verify(tenantRepository).findByCode("TEST001");
    }

    @Test
    @DisplayName("Should search tenants with pagination")
    void shouldSearchTenantsWithPagination() {
        // Given
        List<Tenant> tenants = List.of(mockTenant);
        Page<Tenant> tenantPage = new PageImpl<>(tenants, PageRequest.of(0, 20), 1);

        TenantSearchRequest searchRequest = TenantSearchRequest.builder()
                .searchTerm("Test")
                .status(Tenant.TenantStatus.ACTIVE)
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .sortDirection("desc")
                .build();

        when(tenantRepository.searchTenants(eq("Test"), eq(Tenant.TenantStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(tenantPage);

        // When
        Page<TenantResponse> response = tenantService.searchTenants(searchRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getCode()).isEqualTo("TEST001");

        verify(tenantRepository).searchTenants(eq("Test"), eq(Tenant.TenantStatus.ACTIVE), any(Pageable.class));
    }

    @Test
    @DisplayName("Should delete tenant successfully")
    void shouldDeleteTenantSuccessfully() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(mockTenant));

        // When
        tenantService.deleteTenant(1L);

        // Then
        verify(tenantRepository).findById(1L);
        verify(tenantRepository).delete(mockTenant);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent tenant")
    void shouldThrowExceptionWhenDeletingNonExistentTenant() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.deleteTenant(1L))
                .isInstanceOf(TenantNotFoundException.class)
                .hasMessageContaining("not found");

        verify(tenantRepository).findById(1L);
        verify(tenantRepository, never()).delete(any(Tenant.class));
    }

    @Test
    @DisplayName("Should check if tenant exists by code")
    void shouldCheckIfTenantExistsByCode() {
        // Given
        when(tenantRepository.existsByCode("TEST001")).thenReturn(true);

        // When
        boolean exists = tenantService.existsByCode("TEST001");

        // Then
        assertThat(exists).isTrue();
        verify(tenantRepository).existsByCode("TEST001");
    }
}
