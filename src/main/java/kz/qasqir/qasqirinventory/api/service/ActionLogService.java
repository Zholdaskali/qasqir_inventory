package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.ActionLogDTO;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;


@Service
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final UserService userService;

    @Autowired
    public ActionLogService(ActionLogRepository actionLogRepository, UserService userService) {
        this.actionLogRepository = actionLogRepository;
        this.userService = userService;
    }

    public List<ActionLogDTO> getActionLogs(LocalDate startDate, LocalDate endDate) {

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1)); // последний момент времени в этот день

        List<ActionLogDTO> actionLogs = actionLogRepository.findAllByTimestampBetween(startTimestamp, endTimestamp).stream().map(this::convertToActionLogDTO).toList();

        if (!actionLogs.isEmpty()) {
            return actionLogs;
        }
        throw new LogsNotFoundException();
    }

    private ActionLogDTO convertToActionLogDTO(ActionLog actionLog) {
//        Long actionLoId, Long userId, String userName, String userEmail, String action, String endpoint, Timestamp timestamp
        User user = userService.getByUserId(actionLog.getUserId());
        return new ActionLogDTO(actionLog.getId(), actionLog.getUserId(), user.getEmail(), user.getUserName(), actionLog.getAction(), actionLog.getEndpoint(), actionLog.getTimestamp());
    }
}

