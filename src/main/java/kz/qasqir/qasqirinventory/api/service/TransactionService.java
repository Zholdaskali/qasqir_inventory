package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.TransactionDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getAllTransaction() {
        return transactionRepository.findAll().stream().map(this::convertToDto).toList();
    }

    public void addTransaction(String typeTransaction, Document document, Nomenclature nomenclature, BigDecimal itemQuantity, LocalDate date, User user) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType("TRANSFER");
        transaction.setDocument(document);
        transaction.setNomenclature(nomenclature);
        transaction.setQuantity(itemQuantity);
        transaction.setDate(date);
        transaction.setCreatedBy(user);

        transactionRepository.save(transaction);
    }

    private TransactionDTO convertToDto(Transaction transaction) {
        String userName;
        if (transaction.getCreatedBy() == null) {
            userName = "Не определен";
        } else {
            userName = transaction.getCreatedBy().getUserName();
        }
        return new TransactionDTO(transaction.getId(), transaction.getTransactionType(), transaction.getNomenclature().getId(), transaction.getNomenclature().getName(), transaction.getQuantity(), transaction.getDate(), userName, transaction.getCreatedAt());
    }


}
