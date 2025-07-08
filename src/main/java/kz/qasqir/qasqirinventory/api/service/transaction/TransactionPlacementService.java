package kz.qasqir.qasqirinventory.api.service.transaction;

import kz.qasqir.qasqirinventory.api.mapper.WarehouseZoneMapper;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import kz.qasqir.qasqirinventory.api.model.entity.TransactionPlacement;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.repository.TransactionPlacementRepository;
import kz.qasqir.qasqirinventory.api.service.inventory.InventoryService;
import kz.qasqir.qasqirinventory.api.service.process.InventoryAuditSystemService;
import kz.qasqir.qasqirinventory.api.service.warehouse.WarehouseContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionPlacementService {
    private final TransactionPlacementRepository transactionPlacementRepository;
    private final TransactionService transactionService;
    private final WarehouseZoneMapper warehouseZoneMapper;
    private final WarehouseContainerService warehouseContainerService;
    private final InventoryService inventoryService;
    private final InventoryAuditSystemService inventoryAuditSystemService;

    @Transactional
    public void saveTransactionPlacement(Transaction transaction,
                                         WarehouseZone warehouseZone,
                                         WarehouseContainer warehouseContainer,
                                         BigDecimal quantity, String placementType) {
        TransactionPlacement transactionPlacement = new TransactionPlacement(transaction, warehouseZone, warehouseContainer, quantity, placementType);
        transactionPlacementRepository.save(transactionPlacement);
    }

    public TransactionPlacementResultDTO getAllTransactionPlacementByNomenclatureCode(
            String nomenclatureCode, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        // Конец дня включительно, поэтому добавляем 1 день
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        List<TransactionPlacementDTO> transactionPlacementDTOS = transactionPlacementRepository
                .findAllByTransactionNomenclatureCodeAndCreatedAtBetween(nomenclatureCode, startDateTime, endDateTime)
                .stream()
                .map(this::convertToTransactionPlacementDto)
                .toList();

        BigDecimal totalQuantity = totalQuantity(nomenclatureCode);
        LocalDateTime lastInventoryDate = getLastInventoryDate(nomenclatureCode);
        return new TransactionPlacementResultDTO(transactionPlacementDTOS, totalQuantity, lastInventoryDate);
    }

    private BigDecimal totalQuantity(String code) {
        return inventoryService.getTotalCountByNomenclatureCode(code);
    }

    private LocalDateTime getLastInventoryDate(String code) {
        return inventoryAuditSystemService.getLastInventoryDate(code);
    }

    private TransactionPlacementDTO convertToTransactionPlacementDto(TransactionPlacement transactionPlacement) {
        TransactionDTO transactionDTO = transactionService.convertToDto(transactionPlacement.getTransaction());
        WarehouseZoneDTO warehouseZoneDTO = warehouseZoneMapper.toDto(transactionPlacement.getWarehouseZone());

        WarehouseContainerDTO warehouseContainerDTO = transactionPlacement.getWarehouseContainer() != null
                ? warehouseContainerService.convertToDto(transactionPlacement.getWarehouseContainer())
                : null;

        return new TransactionPlacementDTO(transactionDTO, warehouseZoneDTO, warehouseContainerDTO, transactionPlacement.getQuantity(), transactionPlacement.getPlacementType());
    }
}
