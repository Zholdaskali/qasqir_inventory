package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.stylesheets.LinkStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPlacementResultDTO {
    private List<TransactionPlacementDTO> transactionPlacementDTOS;
    private BigDecimal TotalQuantity;
    private LocalDateTime lastInventoryDate;
}
