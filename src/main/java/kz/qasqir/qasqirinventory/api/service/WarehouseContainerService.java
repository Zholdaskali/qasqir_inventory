    package kz.qasqir.qasqirinventory.api.service;

    import kz.qasqir.qasqirinventory.api.model.dto.WarehouseContainerDTO;
    import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
    import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
    import kz.qasqir.qasqirinventory.api.model.request.WarehouseContainersRequest;
    import kz.qasqir.qasqirinventory.api.repository.WarehouseContainerRepository;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.util.List;

    @Service
    public class WarehouseContainerService {

        private final WarehouseContainerRepository warehouseContainerRepository;
        private final WarehouseZoneService warehouseZoneService;

        public WarehouseContainerService(WarehouseContainerRepository warehouseContainerRepository, WarehouseZoneService warehouseZoneService) {
            this.warehouseContainerRepository = warehouseContainerRepository;
            this.warehouseZoneService = warehouseZoneService;
        }

        public String addWarehouseContainer(WarehouseContainersRequest warehouseContainersRequest) {
            if (warehouseContainersRequest == null) {
                return "Запрос не может быть null";
            }

            WarehouseZone warehouseZone = warehouseZoneService.getById(warehouseContainersRequest.getWarehouseZoneId());
            if (warehouseZone == null) {
                return "Зона не найдена";
            }

            if (warehouseContainersRequest.getHeight() <= 0 || warehouseContainersRequest.getLength() <= 0 || warehouseContainersRequest.getWidth() <= 0) {
                return "Размеры контейнера должны быть положительными";
            }

            if (warehouseContainersRequest.getHeight() > warehouseZone.getHeight() ||
                    warehouseContainersRequest.getLength() > warehouseZone.getLength() ||
                    warehouseContainersRequest.getWidth() > warehouseZone.getWidth()) {
                return "Размер контейнера не подходит под указанную зону";
            }

            BigDecimal height = BigDecimal.valueOf(warehouseContainersRequest.getHeight());
            BigDecimal length = BigDecimal.valueOf(warehouseContainersRequest.getLength());
            BigDecimal width = BigDecimal.valueOf(warehouseContainersRequest.getWidth());

            BigDecimal containerVolume = height.multiply(length).multiply(width);

            if (warehouseZone.getCapacity().compareTo(containerVolume) < 0) {
                return "Недостаточно свободного места в зоне для добавления контейнера";
            }

            WarehouseContainer warehouseContainer = new WarehouseContainer();
            warehouseContainer.setWarehouseZone(warehouseZone);
            warehouseContainer.setCapacity(containerVolume);
            warehouseContainer.setSerialNumber(warehouseContainersRequest.getSerialNumber());
            warehouseContainer.setHeight(warehouseContainersRequest.getHeight());
            warehouseContainer.setLength(warehouseContainersRequest.getLength());
            warehouseContainer.setWidth(warehouseContainersRequest.getWidth());

            warehouseContainerRepository.save(warehouseContainer);

            warehouseZone.setCapacity(warehouseZone.getCapacity().subtract(containerVolume));
            warehouseZoneService.save(warehouseZone);

            return "Контейнер успешно создан";
        }

        public String deleteByWarehouseContainerId(Long warehouseContainerId) {
            WarehouseContainer warehouseContainer = warehouseContainerRepository.findById(warehouseContainerId)
                    .orElseThrow(() -> new RuntimeException("Контейнер с таким айди не найден"));
            WarehouseZone warehouseZone = warehouseContainer.getWarehouseZone();
            warehouseZone.setCapacity(warehouseZone.getCapacity().add(warehouseContainer.getCapacity()));
            warehouseZoneService.save(warehouseZone);
            warehouseContainerRepository.deleteById(warehouseContainerId);
            return "Контейнер успешно удален";
        }

        public WarehouseContainer getById(Long warehouseContainerId) {
            return warehouseContainerRepository.findById(warehouseContainerId).orElseThrow(() -> new RuntimeException("Контейнер с таким айди не найдено"));
        }

        public List<WarehouseContainerDTO> getAllByZoneId(Long zoneId) {
            return warehouseContainerRepository.findAllByWarehouseZoneId(zoneId).stream().map(this::convertToDto).toList();
        }

        private WarehouseContainerDTO convertToDto(WarehouseContainer warehouseContainer) {
            return new WarehouseContainerDTO();
        }

    }
