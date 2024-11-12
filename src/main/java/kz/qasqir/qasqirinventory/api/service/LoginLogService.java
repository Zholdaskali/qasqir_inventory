package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import kz.qasqir.qasqirinventory.api.repository.LoginLogRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoginLogService {
    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    public LoginLog save(Long userId) {
        return loginLogRepository.save(LoginLog.of(userId));
    }

    public List<LoginLog> getLoginLogs(LocalDate startDate, LocalDate endDate) {

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1));
        List<LoginLog> loginLogs = loginLogRepository.findAllByTimestampBetween(startTimestamp, endTimestamp);

        if (!loginLogs.isEmpty()) {
            return loginLogs;
        }
        throw new LogsNotFoundException();
    }
}
