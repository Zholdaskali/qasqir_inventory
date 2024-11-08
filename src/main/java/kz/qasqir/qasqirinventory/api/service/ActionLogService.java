package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


@Service
public class ActionLogService {

    @Autowired
    private ActionLogRepository actionLogRepository;

    public void logAction(Long userId, String action, String endpoint, String data) {
        ActionLog actionLog = new ActionLog();
        actionLog.setUserId(userId);
        actionLog.setAction(action);
        actionLog.setEndpoint(endpoint);
        actionLog.setData(data);
        actionLog.setTimestamp(new Timestamp(System.currentTimeMillis()));

        actionLogRepository.save(actionLog);
    }
}

