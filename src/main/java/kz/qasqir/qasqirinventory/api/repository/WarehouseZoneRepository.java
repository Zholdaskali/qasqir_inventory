package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, Long> {
    List<WarehouseZone> findAllByWarehouseId(Long warehouseId);

    boolean existsByParentId(Long id);

    List<WarehouseZone> findByParentId(Long id);

    List<WarehouseZone> findRootZonesByWarehouseId(Long warehouseId);

    @Query("SELECT wz.canStoreItems, " +
            "(wz.length * wz.width * wz.height) AS totalVolume, " +
            "SUM(CASE WHEN n.volume IS NOT NULL THEN n.volume * i.quantity " +
            "        WHEN n.height IS NOT NULL AND n.width IS NOT NULL AND n.length IS NOT NULL THEN n.height * n.width * n.length * i.quantity " +
            "        ELSE 0 END) AS usedVolume, " +
            "wz.id, wz.name, w.id, w.name " +
            "FROM WarehouseZone wz " +
            "LEFT JOIN Inventory i ON i.warehouseZone.id = wz.id " +
            "LEFT JOIN Nomenclature n ON n.id = i.nomenclature.id " +
            "LEFT JOIN Warehouse w ON w.id = wz.warehouse.id " +
            "GROUP BY wz.id, wz.name, wz.canStoreItems, wz.length, wz.width, wz.height, w.id, w.name")
    List<Object[]> getZoneCapacitiesAndUsedVolumes();

    List<WarehouseZone> findAllByCanStoreItemsTrue();

}
