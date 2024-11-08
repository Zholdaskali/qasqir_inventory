package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.entity.ExceptionLog;
import kz.qasqir.qasqirinventory.api.repository.ExceptionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
