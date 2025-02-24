package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface InventoryAuditRepository extends JpaRepository<InventoryAudit, Long> {
    List<InventoryAudit> findByStatus(String status); // Поиск по статусу
}
