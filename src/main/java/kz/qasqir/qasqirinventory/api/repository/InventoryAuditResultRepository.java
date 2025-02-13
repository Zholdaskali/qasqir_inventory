package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAuditResultRepository extends JpaRepository<InventoryAuditResult, Long> {
}
