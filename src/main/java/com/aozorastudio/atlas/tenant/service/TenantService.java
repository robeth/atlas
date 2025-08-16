package com.aozorastudio.atlas.tenant.service;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import com.aozorastudio.atlas.tenant.dto.CreateTenantRequest;
import com.aozorastudio.atlas.tenant.dto.TenantResponse;
import com.aozorastudio.atlas.tenant.dto.TenantSearchRequest;
import com.aozorastudio.atlas.tenant.dto.UpdateTenantRequest;
import com.aozorastudio.atlas.tenant.exception.TenantCodeAlreadyExistsException;
import com.aozorastudio.atlas.tenant.exception.TenantNotFoundException;
import com.aozorastudio.atlas.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Tenant operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;

    /**
     * Create a new tenant
     */
    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        log.info("Creating tenant with code: {}", request.getCode());

        // Check if tenant code already exists
        if (tenantRepository.existsByCode(request.getCode())) {
            throw new TenantCodeAlreadyExistsException(request.getCode());
        }

        Tenant tenant = Tenant.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(request.getStatus() != null ? request.getStatus() : Tenant.TenantStatus.ACTIVE)
                .createdBy("system") // TODO: Get from security context
                .updatedBy("system") // TODO: Get from security context
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Successfully created tenant with id: {}", savedTenant.getId());

        return TenantResponse.from(savedTenant);
    }

    /**
     * Update an existing tenant
     */
    @Transactional
    public TenantResponse updateTenant(Long id, UpdateTenantRequest request) {
        log.info("Updating tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException(id));

        tenant.setName(request.getName());
        tenant.setDescription(request.getDescription());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());
        tenant.setAddress(request.getAddress());

        if (request.getStatus() != null) {
            tenant.setStatus(request.getStatus());
        }

        tenant.setUpdatedBy("system"); // TODO: Get from security context

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Successfully updated tenant with id: {}", updatedTenant.getId());

        return TenantResponse.from(updatedTenant);
    }

    /**
     * Get tenant by id
     */
    public TenantResponse getTenantById(Long id) {
        log.info("Fetching tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException(id));

        return TenantResponse.from(tenant);
    }

    /**
     * Get tenant by code
     */
    public TenantResponse getTenantByCode(String code) {
        log.info("Fetching tenant with code: {}", code);

        Tenant tenant = tenantRepository.findByCode(code)
                .orElseThrow(() -> new TenantNotFoundException("code", code));

        return TenantResponse.from(tenant);
    }

    /**
     * Search tenants with pagination
     */
    public Page<TenantResponse> searchTenants(TenantSearchRequest searchRequest) {
        log.info("Searching tenants with term: {}, status: {}, page: {}, size: {}",
                searchRequest.getSearchTerm(), searchRequest.getStatus(),
                searchRequest.getPage(), searchRequest.getSize());

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(searchRequest.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                searchRequest.getSortBy());

        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                sort);

        Page<Tenant> tenantPage = tenantRepository.searchTenants(
                searchRequest.getSearchTerm(),
                searchRequest.getStatus(),
                pageable);

        return tenantPage.map(TenantResponse::from);
    }

    /**
     * Soft delete tenant
     */
    @Transactional
    public void deleteTenant(Long id) {
        log.info("Deleting tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException(id));

        tenantRepository.delete(tenant);
        log.info("Successfully deleted tenant with id: {}", id);
    }

    /**
     * Check if tenant exists by code
     */
    public boolean existsByCode(String code) {
        return tenantRepository.existsByCode(code);
    }
}
