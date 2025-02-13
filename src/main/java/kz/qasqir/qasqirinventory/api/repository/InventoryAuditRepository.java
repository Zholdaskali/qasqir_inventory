package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAuditRepository extends JpaRepository<InventoryAudit, Long> {
}
