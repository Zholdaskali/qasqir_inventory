package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, Long> {
    List<ExceptionLog> findAllByTimestampBetween(Timestamp startDate, Timestamp endDate);
}
