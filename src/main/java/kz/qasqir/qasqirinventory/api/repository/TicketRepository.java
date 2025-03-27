package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
    void deleteByInventoryId(Long inventoryId);}
