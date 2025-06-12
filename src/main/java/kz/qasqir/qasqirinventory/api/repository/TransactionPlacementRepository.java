package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.TransactionPlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface TransactionPlacementRepository extends JpaRepository<TransactionPlacement, Long> {

    @Query("SELECT t FROM TransactionPlacement t JOIN t.transaction tr JOIN tr.nomenclature n WHERE n.code = :nomenclatureCode AND t.createdAt >= :startDate AND t.createdAt < :endDate")
    List<TransactionPlacement> findAllByTransactionNomenclatureCodeAndCreatedAtBetween(
            @Param("nomenclatureCode") String nomenclatureCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
