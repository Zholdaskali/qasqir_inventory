package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseContainersRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseContainerRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseContainerService {

    private final WarehouseContainerRepository warehouseContainerRepository;
    private final WarehouseZoneService warehouseZoneService;

    public WarehouseContainerService(WarehouseContainerRepository warehouseContainerRepository, WarehouseZoneService warehouseZoneService) {
        this.warehouseContainerRepository = warehouseContainerRepository;
        this.warehouseZoneService = warehouseZoneService;
    }

    public String addWarehouseContainer(WarehouseContainersRequest warehouseContainersRequest) {
        WarehouseContainer warehouseContainer = new WarehouseContainer();

        WarehouseZone warehouseZone = warehouseZoneService.getById(warehouseContainersRequest.getWarehouseZoneId());

        if (warehouseZone.getHeight() > warehouseContainersRequest.getHeight() || warehouseZone.getLength() > warehouseContainersRequest.getLength() || warehouseZone.getWidth() > warehouseContainersRequest.getWidth()) {
            warehouseContainer.setWarehouseZone(warehouseZoneService.getById(warehouseContainersRequest.getWarehouseZoneId()));
            warehouseContainer.setCapacity(warehouseContainersRequest.getCapacity());
            warehouseContainer.setSerialNumber(warehouseContainersRequest.getSerialNumber());
            warehouseContainer.setHeight(warehouseContainersRequest.getHeight());
            warehouseContainer.setLength(warehouseContainersRequest.getLength());
            warehouseContainer.setWidth(warehouseContainersRequest.getWidth());

            warehouseContainerRepository.save(warehouseContainer);
            return "Контейнер успешно создан";
        }else {
            return "Размер контейнера не подходит под указанную зону";
        }
    }

    public String deleteByWarehouseContainerId(Long warehouseContainerId) {
        warehouseContainerRepository.deleteById(warehouseContainerId);
        return "Контайнер успешно удален";
    }

    public WarehouseContainer getById(Long warehouseContainerId) {
        return warehouseContainerRepository.findById(warehouseContainerId).orElseThrow(() -> new RuntimeException("Контейнер с таким айди не найдено"));
    }
}
