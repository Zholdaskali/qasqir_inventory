package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.entity.LoginLog;
import kz.qasqir.qasqirinventory.api.repository.LoginLogRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginLogService {
    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    public LoginLog save(Long userId) {
        return loginLogRepository.save(LoginLog.of(userId));
    }
}
