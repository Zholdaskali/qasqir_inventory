package kz.qasqir.qasqirinventory.api.service.defaultservice;

import jakarta.persistence.*;
import kz.qasqir.qasqirinventory.api.mapper.WarehouseZoneMapper;
import kz.qasqir.qasqirinventory.api.model.dto.TransactionDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TransactionPlacementDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseContainerDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import kz.qasqir.qasqirinventory.api.model.entity.TransactionPlacement;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.repository.TransactionPlacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionPlacementService {
    private final TransactionPlacementRepository transactionPlacementRepository;
    private final TransactionService transactionService;
    private final WarehouseZoneMapper warehouseZoneMapper;
    private final WarehouseContainerService warehouseContainerService;

    @Transactional
    public void saveTransactionPlacement(Transaction transaction,
                                         WarehouseZone warehouseZone,
                                         WarehouseContainer warehouseContainer,
                                         BigDecimal quantity) {
        TransactionPlacement transactionPlacement = new TransactionPlacement(transaction, warehouseZone, warehouseContainer, quantity);
        transactionPlacementRepository.save(transactionPlacement);
    }

    public List<TransactionPlacementDTO> getAllTransactionPlacementByNomenclatureCode(String nomenclatureCode) {
        return transactionPlacementRepository.findAllByTransactionNomenclatureCode(nomenclatureCode).stream().map(this::convertToTransactionPlacementDto).toList();
    }

    private TransactionPlacementDTO convertToTransactionPlacementDto(TransactionPlacement transactionPlacement) {
        TransactionDTO transactionDTO = transactionService.convertToDto(transactionPlacement.getTransaction());
        WarehouseZoneDTO warehouseZoneDTO = warehouseZoneMapper.toDto(transactionPlacement.getWarehouseZone());

        WarehouseContainerDTO warehouseContainerDTO = transactionPlacement.getWarehouseContainer() != null
                ? warehouseContainerService.convertToDto(transactionPlacement.getWarehouseContainer())
                : null;

        return new TransactionPlacementDTO(transactionDTO, warehouseZoneDTO, warehouseContainerDTO, transactionPlacement.getQuantity());
    }
}
