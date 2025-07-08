package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, Long> {
    List<WarehouseZone> findAllByWarehouseId(Long warehouseId);

    boolean existsByParentId(Long id);

    List<WarehouseZone> findByParentId(Long id);

    List<WarehouseZone> findRootZonesByWarehouseId(Long warehouseId);

    @Query("SELECT SUM(wz.length * wz.width * wz.height) " +
            "FROM WarehouseZone wz WHERE wz.canStoreItems = true")
    Optional<BigDecimal> calculateTotalVolumeForStorableZones();

    @Query("SELECT wz FROM WarehouseZone wz JOIN FETCH wz.warehouse " +
            "WHERE wz.canStoreItems = true")
    List<WarehouseZone> findAllStorableZonesWithWarehouse();

    @Query("SELECT wz.id AS id, wz.name AS name, w.name AS warehouseName, " +
            "SUM(CASE WHEN n.volume IS NOT NULL THEN n.volume * i.quantity " +
            "WHEN n.height IS NOT NULL AND n.width IS NOT NULL AND n.length IS NOT NULL " +
            "THEN (n.height * n.width * n.length) * i.quantity ELSE 0 END) AS usedCapacity, " +
            "wz.length * wz.width * wz.height AS totalVolume " +
            "FROM WarehouseZone wz " +
            "JOIN wz.warehouse w " +
            "LEFT JOIN Inventory i ON i.warehouseZone.id = wz.id " +
            "LEFT JOIN i.nomenclature n " +
            "WHERE wz.canStoreItems = true " +
            "GROUP BY wz.id, wz.name, w.name, wz.length, wz.width, wz.height")
    List<Object[]> findZoneStats();
}
