package kz.qasqir.qasqirinventory.api.model.dto;

import kz.qasqir.qasqirinventory.api.model.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DocumentWithTransactionsDTO {
    private DocumentDTO document;
    private List<TransactionDTO> transactions;
}
