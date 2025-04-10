package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT i FROM Inventory i " +
            "JOIN FETCH i.nomenclature n " +
            "JOIN FETCH i.warehouseZone wz " +
            "JOIN FETCH wz.warehouse w " +
            "WHERE wz.id = :warehouseZoneId")
    List<Inventory> findAllByWarehouseZoneId(@Param("warehouseZoneId") Long warehouseZoneId);

    Optional<Inventory> findByNomenclatureIdAndWarehouseZoneId(Long nomenclatureId, Long warehouseZoneId);

    Optional<Inventory> findByNomenclatureId(Long nomenclatureId);

    Optional<Inventory> findByNomenclatureIdAndWarehouseZoneIdAndWarehouseContainerId(Long id, Long id1, Long containerId);

    @Query("SELECT SUM(i.quantity) FROM Inventory i")
    Optional<BigDecimal> getTotalQuantity();

    @Query("SELECT i.nomenclature.id, i.nomenclature.name, i.quantity " +
            "FROM Inventory i " +
            "WHERE i.quantity < :threshold")
    List<Object[]> findLowStockItems(@Param("threshold") BigDecimal threshold);

    List<Inventory> findByWarehouseContainerId(@Param("id") Long id);
}


