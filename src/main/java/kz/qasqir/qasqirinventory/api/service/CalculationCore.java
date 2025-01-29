package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculationCore {
    public int warehouseZoneByWarehouseId(List<WarehouseZone> warehouseZones) {
        return warehouseZones.size();
    }
}
