package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryAuditSystemRepository extends JpaRepository<InventoryAuditSystem, Long> {
    List<InventoryAuditSystem> findAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String completed);

    @Query("SELECT MAX(iar.createdAt) FROM InventoryAuditResult iar WHERE iar.nomenclature.code = :nomenclatureCode")
    Optional<LocalDateTime> findLastInventoryDateByNomenclatureCode(String nomenclatureCode);
}
