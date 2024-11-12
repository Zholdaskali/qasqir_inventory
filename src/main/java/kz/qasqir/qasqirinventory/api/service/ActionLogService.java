package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;


@Service
public class ActionLogService {

    @Autowired
    private ActionLogRepository actionLogRepository;

    public void logAction(Long userId, String action, String endpoint) {
        ActionLog actionLog = new ActionLog();
        actionLog.setUserId(userId);
        actionLog.setAction(action);
        actionLog.setEndpoint(endpoint);
        actionLog.setTimestamp(new Timestamp(System.currentTimeMillis()));

        actionLogRepository.save(actionLog);
    }

    public List<ActionLog> getActionLogs(LocalDate startDate, LocalDate endDate) {

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1)); // последний момент времени в этот день

        List<ActionLog> actionLogs = actionLogRepository.findAllByTimestampBetween(startTimestamp, endTimestamp);

        if (!actionLogs.isEmpty()) {
            return actionLogs;
        }
        throw new LogsNotFoundException();
    }
}

