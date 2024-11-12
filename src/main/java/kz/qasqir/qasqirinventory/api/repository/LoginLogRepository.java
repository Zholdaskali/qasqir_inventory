package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findAllByTimestampBetween(Timestamp startDate, Timestamp endDate);
}
