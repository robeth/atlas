package com.aozorastudio.atlas.tenant.controller;

import com.aozorastudio.atlas.tenant.dto.CreateTenantRequest;
import com.aozorastudio.atlas.tenant.dto.TenantResponse;
import com.aozorastudio.atlas.tenant.dto.TenantSearchRequest;
import com.aozorastudio.atlas.tenant.dto.UpdateTenantRequest;
import com.aozorastudio.atlas.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Tenant operations
 */
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        log.info("POST /api/v1/tenants - Creating tenant with code: {}", request.getCode());

        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantResponse> updateTenant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request) {
        log.info("PUT /api/v1/tenants/{} - Updating tenant", id);

        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable Long id) {
        log.info("GET /api/v1/tenants/{} - Fetching tenant", id);

        TenantResponse response = tenantService.getTenantById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TenantResponse> getTenantByCode(@PathVariable String code) {
        log.info("GET /api/v1/tenants/code/{} - Fetching tenant", code);

        TenantResponse response = tenantService.getTenantByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TenantResponse>> searchTenants(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("GET /api/v1/tenants V2 V2- Searching tenants with term: {}, status: {}, page: {}, size: {}",
                searchTerm, status, page, size);

        TenantSearchRequest searchRequest = TenantSearchRequest.builder()
                .searchTerm(searchTerm)
                .status(status != null
                        ? com.aozorastudio.atlas.tenant.domain.Tenant.TenantStatus.valueOf(status.toUpperCase())
                        : null)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<TenantResponse> response = tenantService.searchTenants(searchRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        log.info("DELETE /api/v1/tenants/{} - Deleting tenant", id);

        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{code}")
    public ResponseEntity<Boolean> existsByCode(@PathVariable String code) {
        log.info("GET /api/v1/tenants/exists/{} - Checking tenant existence", code);

        boolean exists = tenantService.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
}
