-- Create tenant table
CREATE TABLE tenants (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- Create index for searching
CREATE INDEX idx_tenant_code ON tenants(code);
CREATE INDEX idx_tenant_name ON tenants(name);
CREATE INDEX idx_tenant_status ON tenants(status);
CREATE INDEX idx_tenant_deleted_at ON tenants(deleted_at);
CREATE INDEX idx_tenant_created_at ON tenants(created_at);
