package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditResultDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAuditResultRepository extends JpaRepository<InventoryAuditResult, Long> {
    List<InventoryAuditResult> findByAuditId(Long auditId);
}
