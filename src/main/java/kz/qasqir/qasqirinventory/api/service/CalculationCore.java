package kz.qasqir.qasqirinventory.api.service;
import kz.qasqir.qasqirinventory.api.repository.WarehouseContainerRepository;
import org.springframework.stereotype.Component;

@Component
public class CalculationCore {

    private final WarehouseContainerRepository warehouseContainerRepository;

    public CalculationCore(WarehouseContainerRepository warehouseContainerRepository) {
        this.warehouseContainerRepository = warehouseContainerRepository;
    }
}
