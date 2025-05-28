package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryAuditRepository extends JpaRepository<InventoryAudit, Long> {

    Optional<InventoryAudit> findByWarehouseId(Long warehouseId);
    List<InventoryAudit> findAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<InventoryAudit> findAllByInventoryAuditSystemId(Long id);

    boolean existsByStatusAndInventoryAuditSystemId(String inProgress, Long id);

    long countByInventoryAuditSystemId(Long id);
}
