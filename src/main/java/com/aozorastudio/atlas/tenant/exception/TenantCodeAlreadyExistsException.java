package com.aozorastudio.atlas.tenant.exception;

/**
 * Exception thrown when a tenant code already exists
 */
public class TenantCodeAlreadyExistsException extends RuntimeException {

    public TenantCodeAlreadyExistsException(String code) {
        super("Tenant with code '" + code + "' already exists");
    }
}
