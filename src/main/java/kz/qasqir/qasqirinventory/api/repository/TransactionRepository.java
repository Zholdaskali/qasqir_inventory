package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByDocumentId(Long documentId);

    @Query("SELECT t FROM Transaction t WHERE t.document.id IN :documentIds")
    List<Transaction> findByDocumentIdIn(@Param("documentIds") List<Long> documentIds);

    // Поиск транзакций за период
    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Подсчет транзакций за период
    long countByDateBetween(LocalDate startDate, LocalDate endDate);

    // Поиск транзакций по типу и периоду
    List<Transaction> findByTransactionTypeAndDateBetween(String transactionType, LocalDate startDate, LocalDate endDate);

    // Поиск транзакций по номенклатуре
    List<Transaction> findByNomenclatureId(Long nomenclatureId);
}
