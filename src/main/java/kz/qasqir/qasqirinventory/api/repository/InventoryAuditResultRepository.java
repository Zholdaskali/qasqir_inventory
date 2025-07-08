package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryAuditResultRepository extends JpaRepository<InventoryAuditResult, Long> {
    List<InventoryAuditResult> findByAuditId(Long auditId);
    long countByAuditId(Long auditId);
    @Query("""
    SELECT iar.createdAt 
    FROM InventoryAuditResult iar 
    WHERE iar.nomenclature.code = :nomenclatureCode 
    ORDER BY iar.createdAt DESC
    """)
    List<LocalDateTime> findTop10ByNomenclatureCodeOrderByCreatedAtDesc(@Param("nomenclatureCode") String nomenclatureCode, Pageable pageable);

}
