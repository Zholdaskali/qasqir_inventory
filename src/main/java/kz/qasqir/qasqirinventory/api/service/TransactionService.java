package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TransactionDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> getAllTransactionByDocumentId(Long documentId) {
        return transactionRepository.findAllByDocumentId(documentId).stream().map(this::convertToDto).toList();
    }

    public void addTransaction(String typeTransaction, Document document, Nomenclature nomenclature, BigDecimal itemQuantity, LocalDate date, User user) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(typeTransaction);
        transaction.setDocument(document);
        transaction.setNomenclature(nomenclature);
        transaction.setQuantity(itemQuantity);
        transaction.setDate(date);
        transaction.setCreatedBy(user);

        transactionRepository.save(transaction);
    }

    public List<TransactionDTO> findByDocumentIdIn(List<Long> documentIds) {
        return transactionRepository.findByDocumentIdIn(documentIds).stream().map(this::convertToDto).toList();
    }

    protected TransactionDTO convertToDto(Transaction transaction) {
        String userName;
        if (transaction.getCreatedBy().getUserName() == null) {
            userName = "Не определен";
        } else {
            userName = transaction.getCreatedBy().getUserName();
        }
        return new TransactionDTO(transaction.getId(), transaction.getTransactionType(), transaction.getNomenclature().getId(), transaction.getNomenclature().getName(), transaction.getQuantity(), transaction.getDate(), userName, transaction.getCreatedAt(), transaction.getDocument().getId());
    }
}
