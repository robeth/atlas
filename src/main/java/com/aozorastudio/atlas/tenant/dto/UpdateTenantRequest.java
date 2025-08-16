package com.aozorastudio.atlas.tenant.dto;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for updating an existing tenant
 */
@Data
@Builder
public class UpdateTenantRequest {

    @NotBlank(message = "Tenant name is required")
    @Size(max = 255, message = "Tenant name must not exceed 255 characters")
    private String name;

    private String description;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    private String address;

    private Tenant.TenantStatus status;
}
