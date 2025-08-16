package com.aozorastudio.atlas.tenant.dto;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for tenant search parameters
 */
@Data
@Builder
public class TenantSearchRequest {

    private String searchTerm;
    private Tenant.TenantStatus status;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "desc";
}
