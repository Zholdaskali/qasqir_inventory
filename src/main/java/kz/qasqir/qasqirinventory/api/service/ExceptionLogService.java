package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import kz.qasqir.qasqirinventory.api.repository.ExceptionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExceptionLogService {
    private final ExceptionLogRepository exceptionLogRepository;

    @Autowired
    public ExceptionLogService(ExceptionLogRepository exceptionLogRepository) {
        this.exceptionLogRepository = exceptionLogRepository;
    }

    public ExceptionLog save(String cause, String message) {
        return exceptionLogRepository.save(ExceptionLog.of(cause, message));
    }

    public List<ExceptionLog> getExceptionLogs(LocalDate startDate, LocalDate endDate) {

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1));
        List<ExceptionLog> exceptionLogs = exceptionLogRepository.findAllByTimestampBetween(startTimestamp, endTimestamp);

        if (!exceptionLogs.isEmpty()) {
            return exceptionLogs;
        }
        throw new LogsNotFoundException();
    }
}
