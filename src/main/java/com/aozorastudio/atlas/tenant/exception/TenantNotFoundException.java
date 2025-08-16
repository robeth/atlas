package com.aozorastudio.atlas.tenant.exception;

/**
 * Exception thrown when a tenant is not found
 */
public class TenantNotFoundException extends RuntimeException {

    public TenantNotFoundException(String message) {
        super(message);
    }

    public TenantNotFoundException(Long id) {
        super("Tenant not found with id: " + id);
    }

    public TenantNotFoundException(String field, String value) {
        super("Tenant not found with " + field + ": " + value);
    }
}
