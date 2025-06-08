package kz.qasqir.qasqirinventory.api.service.defaultservice;

import kz.qasqir.qasqirinventory.api.model.entity.PasswordResetToken;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.PasswordRecoveryRequest;
import kz.qasqir.qasqirinventory.api.repository.PasswordResetTokenRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import kz.qasqir.qasqirinventory.api.util.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import kz.qasqir.qasqirinventory.api.exception.PasswordResetTokenException;

import java.time.LocalDateTime;

@Service
public class PasswordRecoveryService {

    private final PasswordRecoveryMailService passwordRecoveryMailService;
    private final TokenGenerator tokenGenerator;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${password-recovery.link}")
    private String PASSWORD_RESET_LINK;

    public PasswordRecoveryService(PasswordRecoveryMailService passwordRecoveryMailService, TokenGenerator tokenGenerator, PasswordResetTokenRepository passwordResetTokenRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.passwordRecoveryMailService = passwordRecoveryMailService;
        this.tokenGenerator = tokenGenerator;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String generate(String userEmail) {
        try {
            userService.getByUserEmail(userEmail);
            String token = tokenGenerator.generate();
            String link = PASSWORD_RESET_LINK + "?Password-reset-token=" + token;
            passwordRecoveryMailService.sendPasswordRecoveryEmail(userEmail, link);
            PasswordResetToken passwordResetToken = new PasswordResetToken(userEmail, token, link, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
            passwordResetTokenRepository.save(passwordResetToken);
            return "Ссылка на изменение пароля успешно отправлен";
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String passwordRecovery(String token, PasswordRecoveryRequest passwordRecoveryRequest) {
        if (token == null || token.isEmpty()) {
            throw new PasswordResetTokenException();
        }

        PasswordResetToken passwordResetToken = getPasswordTokenByToken(token);
        if (passwordResetToken.isExpired()) {
            throw new PasswordResetTokenException("Срок действия токена истек");
        }

        String hashNewPassword = passwordEncoder.hash(passwordRecoveryRequest.getNewPassword());

        User user = userService.getByUserEmail(passwordResetToken.getUserEmail());
        user.setPassword(hashNewPassword);
        userService.saveUser(user);
        passwordResetTokenRepository.delete(passwordResetToken);
        return "Пароль успешно изменен";
    }

    public PasswordResetToken getPasswordTokenByToken(String token) {
        System.out.println(token + "==========================================================");
        return passwordResetTokenRepository.findByPasswordResetToken(token).orElseThrow(PasswordResetTokenException::new);
    }

}
