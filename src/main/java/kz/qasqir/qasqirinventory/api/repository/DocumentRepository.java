package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByDocumentNumber(String documentNumber);

    @Query("SELECT DISTINCT d FROM Document d " +
            "LEFT JOIN FETCH d.supplier " + // Загружаем поставщика сразу
            "LEFT JOIN FETCH d.customer " + // Загружаем клиента (если нужно)
            "WHERE d.documentDate BETWEEN :startDate AND :endDate")
    List<Document> findByDocumentDateBetweenWithJoins(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByDocumentNumber(String documentNumber);
}
