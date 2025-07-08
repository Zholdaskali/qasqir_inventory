package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.dto.TopNomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByDocumentId(Long documentId);

    @Query("SELECT t FROM Transaction t WHERE t.document.id IN :documentIds")
    List<Transaction> findByDocumentIdIn(@Param("documentIds") List<Long> documentIds);

    @Query("SELECT t FROM Transaction t " +
            "LEFT JOIN FETCH t.nomenclature " + // Загружаем номенклатуру
            "LEFT JOIN FETCH t.createdBy " +    // Загружаем пользователя
            "LEFT JOIN FETCH t.document " +     // Документ (хотя он уже есть в `documentIds`)
            "WHERE t.document.id IN :documentIds")
    List<Transaction> findByDocumentIdInWithJoins(@Param("documentIds") List<Long> documentIds);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate")
    long countByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT NEW kz.qasqir.qasqirinventory.api.model.dto.TopNomenclatureDTO(n.id, n.name, SUM(t.quantity)) " +
            "FROM Transaction t JOIN t.nomenclature n " +
            "WHERE t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY n.id, n.name ORDER BY SUM(t.quantity) DESC")
    List<TopNomenclatureDTO> findTopNomenclatures(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  Pageable pageable);

    @Query("SELECT NEW kz.qasqir.qasqirinventory.api.model.dto.TopNomenclatureDTO(n.id, n.name, SUM(t.quantity)) " +
            "FROM Transaction t JOIN t.nomenclature n " +
            "WHERE t.date BETWEEN :startDate AND :endDate AND t.transactionType = 'OUTBOUND' " +
            "GROUP BY n.id, n.name ORDER BY SUM(t.quantity) DESC")
    List<TopNomenclatureDTO> findTrendingItems(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               Pageable pageable);

}
