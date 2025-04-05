package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DocumentWithTransactionsDTO {
    private DocumentDTO document;
    private List<TransactionDTO> transactions;

}
