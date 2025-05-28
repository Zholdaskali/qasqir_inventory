package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryAuditSystemRepository extends JpaRepository<InventoryAuditSystem, Long> {
    List<InventoryAuditSystem> findAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String completed);
}
