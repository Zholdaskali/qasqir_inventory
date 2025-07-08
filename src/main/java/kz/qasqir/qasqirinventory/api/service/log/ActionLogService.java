package kz.qasqir.qasqirinventory.api.service.log;

import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.ActionLogDTO;
import kz.qasqir.qasqirinventory.api.model.entity.ActionLog;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.ActionLogRepository;
import kz.qasqir.qasqirinventory.api.service.user.UserService;
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
            System.out.println();
            return actionLogs;
        }
        throw new LogsNotFoundException();
    }

    private ActionLogDTO convertToActionLogDTO(ActionLog actionLog) {
        try {
            // Пытаемся получить пользователя
            User user = userService.getByUserId(actionLog.getUserId());
            return new ActionLogDTO(
                    actionLog.getId(),
                    actionLog.getUserId(),
                    user.getUserName(),
                    user.getEmail(),
                    actionLog.getAction(),
                    actionLog.getEndpoint(),
                    actionLog.getTimestamp()
            );
        } catch (UserNotFoundException e) {
            // Обрабатываем случай, когда пользователь не найден
            return new ActionLogDTO(
                    actionLog.getId(),
                    actionLog.getUserId(),
                    "Unknown User",
                    "Unknown Email",
                    actionLog.getAction(),
                    actionLog.getEndpoint(),
                    actionLog.getTimestamp()
            );
        }
    }

}

