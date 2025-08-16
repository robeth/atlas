package com.aozorastudio.atlas.tenant.dto;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for tenant response
 */
@Data
@Builder
public class TenantResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String address;
    private Tenant.TenantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    public static TenantResponse from(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .code(tenant.getCode())
                .name(tenant.getName())
                .description(tenant.getDescription())
                .email(tenant.getEmail())
                .phone(tenant.getPhone())
                .address(tenant.getAddress())
                .status(tenant.getStatus())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .createdBy(tenant.getCreatedBy())
                .updatedBy(tenant.getUpdatedBy())
                .version(tenant.getVersion())
                .build();
    }
}
