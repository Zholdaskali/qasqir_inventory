package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryItemDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
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

    Optional<Inventory> findByNomenclatureIdAndWarehouseZoneIdAndWarehouseContainerId(Long id, Long id1, Long containerId);

    @Query("SELECT SUM(i.quantity) FROM Inventory i")
    Optional<BigDecimal> getTotalQuantity();

    List<Inventory> findByWarehouseContainerId(@Param("id") Long id);

    List<Inventory> findByNomenclatureCode(String nomenclatureCode);

    long countByWarehouseZoneWarehouseId(Long warehouseId);

    List<Inventory> findAllByNomenclatureId(Long id);

    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.nomenclature.code = :code")
    BigDecimal getTotalQuantityByNomenclatureCode(@Param("code") String code);

    @Query("SELECT SUM(CASE WHEN n.volume IS NOT NULL THEN n.volume * i.quantity " +
            "WHEN n.height IS NOT NULL AND n.width IS NOT NULL AND n.length IS NOT NULL " +
            "THEN n.height * n.width * n.length * i.quantity ELSE 0 END) " +
            "FROM Inventory i JOIN i.nomenclature n WHERE i.warehouseZone.id = :zoneId")
    Optional<BigDecimal> calculateUsedCapacityForZone(@Param("zoneId") Long zoneId);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.nomenclature n " +
            "WHERE i.quantity < :threshold")
    List<Inventory> findAllByQuantityLessThan(@Param("threshold") BigDecimal threshold);

    @Query("SELECT SUM(CASE WHEN n.volume IS NOT NULL THEN n.volume * i.quantity " +
            "WHEN n.height IS NOT NULL AND n.width IS NOT NULL AND n.length IS NOT NULL " +
            "THEN n.height * n.width * n.length * i.quantity ELSE 0 END) " +
            "FROM Inventory i JOIN i.nomenclature n " +
            "WHERE i.warehouseZone.canStoreItems = true")
    Optional<BigDecimal> calculateUsedCapacity();

    List<Inventory> findByNomenclatureName(String nomenclatureName);

    List<Inventory> findByNomenclatureNameStartingWithIgnoreCase(String nomenclatureName);

    List<Inventory> findByNomenclatureCodeStartingWithIgnoreCase(String nomenclatureCode);
}


