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

//    @Query("SELECT t FROM TransactionPlacement t JOIN t.transaction tr JOIN tr.nomenclature n WHERE n.code = :nomenclatureCode AND t.createdAt >= :startDate AND t.createdAt < :endDate")
//    List<TransactionPlacement> findAllByTransactionNomenclatureCodeAndCreatedAtBetween(
//            @Param("nomenclatureCode") String nomenclatureCode,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT tp FROM TransactionPlacement tp " +
            "JOIN FETCH tp.transaction t " +
            "JOIN FETCH t.document d " +
            "LEFT JOIN FETCH d.customer c " +
            "LEFT JOIN FETCH d.supplier s " +
            "JOIN FETCH t.nomenclature n " +
            "LEFT JOIN FETCH n.category cat " +
            "JOIN FETCH tp.warehouseZone wz " +
            "LEFT JOIN FETCH wz.parent p " +
            "LEFT JOIN FETCH wz.warehouse w " +
            "LEFT JOIN FETCH tp.warehouseContainer wc " +
            "WHERE n.code = :code AND tp.createdAt BETWEEN :startDate AND :endDate")
    List<TransactionPlacement> findAllByTransactionNomenclatureCodeAndCreatedAtBetween(
            @Param("code") String nomenclatureCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
