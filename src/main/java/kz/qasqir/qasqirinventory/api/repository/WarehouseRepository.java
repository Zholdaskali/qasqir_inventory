package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query(value = "SELECT ROUND(" +
            "    CASE " +
            "        WHEN SUM(CASE WHEN wz.can_store_items = 'f' AND wz.parent_id IS NULL " +
            "                     THEN wz.width * wz.height * wz.length ELSE 0 END) = 0 THEN 0 " +
            "        ELSE (COALESCE(SUM(CASE WHEN wz.can_store_items = 't' THEN wz.capacity ELSE 0 END), 0) + " +
            "              COALESCE(SUM(wc.capacity), 0)) * 100.0 / " +
            "              SUM(CASE WHEN wz.can_store_items = 'f' AND wz.parent_id IS NULL " +
            "                       THEN wz.width * wz.height * wz.length ELSE 0 END) " +
            "    END, " +
            "    2) " +
            "FROM t_warehouses w " +
            "LEFT JOIN t_warehouse_zones wz ON w.id = wz.warehouse_id " +
            "LEFT JOIN t_warehouse_containers wc ON wz.id = wc.warehouse_zone_id " +
            "WHERE w.id = :warehouseId " +
            "GROUP BY w.id", nativeQuery = true)
    Double getUsedVolumePercent(@Param("warehouseId") Long warehouseId);
}

