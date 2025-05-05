package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByType(String type);

    List<Ticket> findAllByTypeAndCreateAtBetween(String type, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    @Modifying
    @Query(value = "DELETE FROM t_ticket WHERE inventory_id = :inventoryId", nativeQuery = true)
    void deleteByInventoryId(Long inventoryId);

    @Query("SELECT t FROM Ticket t " +
            "LEFT JOIN FETCH t.inventory i " +
            "LEFT JOIN FETCH i.nomenclature n " +
            "LEFT JOIN FETCH n.category " +
            "LEFT JOIN FETCH t.document d " +
            "LEFT JOIN FETCH d.customer " +
            "LEFT JOIN FETCH t.createdBy " +
            "LEFT JOIN FETCH t.manager " +
            "WHERE t.type = :type AND t.createAt BETWEEN :start AND :end")
    List<Ticket> findAllTicketsWithJoins(
            @Param("type") String type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Ticket> findAllByDocumentId(Long documentId);
}
