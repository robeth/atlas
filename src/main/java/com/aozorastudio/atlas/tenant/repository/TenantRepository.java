package com.aozorastudio.atlas.tenant.repository;

import com.aozorastudio.atlas.tenant.domain.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Tenant entity
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Find tenant by code
     */
    Optional<Tenant> findByCode(String code);

    /**
     * Check if tenant exists by code
     */
    boolean existsByCode(String code);

    /**
     * Search tenants by name or code with pagination
     */
    @Query("SELECT t FROM Tenant t WHERE " +
            "(:searchTerm IS NULL OR :searchTerm = '' OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:status IS NULL OR t.status = :status)")
    Page<Tenant> searchTenants(@Param("searchTerm") String searchTerm,
            @Param("status") Tenant.TenantStatus status,
            Pageable pageable);

    /**
     * Find all active tenants
     */
    @Query("SELECT t FROM Tenant t WHERE t.status = 'ACTIVE'")
    Page<Tenant> findAllActive(Pageable pageable);
}
