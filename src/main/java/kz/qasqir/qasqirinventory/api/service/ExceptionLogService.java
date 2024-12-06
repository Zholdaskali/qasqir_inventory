package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.ExceptionLogSaveFailedException;
import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.ExceptionLogDTO;
import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
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
        try {
            return exceptionLogRepository.save(ExceptionLog.of(cause, message));
        } catch (Exception e) {
            throw new ExceptionLogSaveFailedException();
        }
    }


    public List<ExceptionLogDTO> getExceptionLogs(LocalDate startDate, LocalDate endDate) {
        try {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Дата начала и конца не могут быть null.");
            }

            Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1));

            List<ExceptionLogDTO> exceptionLogs = exceptionLogRepository
                    .findAllByTimestampBetween(startTimestamp, endTimestamp)
                    .stream()
                    .map(this::convertToExceptionLogDTO)
                    .toList();

            if (exceptionLogs.isEmpty()) {
                throw new LogsNotFoundException();
            }

            return exceptionLogs;
        } catch (LogsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить логи исключений. Попробуйте позже.");
        }
    }

    private ExceptionLogDTO convertToExceptionLogDTO(ExceptionLog exceptionLog) {

//        Long exceptionId, String cause, String message, Timestamp timestamp
        return new ExceptionLogDTO(exceptionLog.getId(), exceptionLog.getCause(), exceptionLog.getMessage(), exceptionLog.getTimestamp());
    }
}
