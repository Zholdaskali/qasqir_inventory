package kz.qasqir.qasqirinventory.api.service.log;

import kz.qasqir.qasqirinventory.api.exception.LogsNotFoundException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.LoginLogDTO;
import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.LoginLogRepository;
import kz.qasqir.qasqirinventory.api.service.user.UserService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoginLogService {
    private final LoginLogRepository loginLogRepository;
    private final UserService userService;

    public LoginLogService(LoginLogRepository loginLogRepository, UserService userService) {
        this.loginLogRepository = loginLogRepository;
        this.userService = userService;
    }

    public LoginLog save(Long userId) {
        return loginLogRepository.save(LoginLog.of(userId));
    }

    public List<LoginLogDTO> getLoginLogs(LocalDate startDate, LocalDate endDate) {

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusSeconds(1));
        List<LoginLogDTO> loginLogs = loginLogRepository.findAllByTimestampBetween(startTimestamp, endTimestamp).stream().map(this::convertToLoginLogDTO).toList();

        if (!loginLogs.isEmpty()) {
            return loginLogs;
        }
        throw new LogsNotFoundException();
    }

    private LoginLogDTO convertToLoginLogDTO(LoginLog loginLog) {
        try {
            User user = userService.getByUserId(loginLog.getUserId());
            return new LoginLogDTO(
                    loginLog.getId(),
                    loginLog.getUserId(),
                    user.getUserName(),
                    user.getEmail(),
                    loginLog.getTimestamp()
            );
        } catch (UserNotFoundException e) {
            // Возвращаем DTO с "Unknown User" и "Unknown Email"
            return new LoginLogDTO(
                    loginLog.getId(),
                    loginLog.getUserId(),
                    "Unknown User",
                    "Unknown Email",
                    loginLog.getTimestamp()
            );
        }
    }

}
