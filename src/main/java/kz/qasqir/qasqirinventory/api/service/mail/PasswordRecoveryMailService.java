package kz.qasqir.qasqirinventory.api.service.mail;

import org.springframework.stereotype.Service;

@Service
public class PasswordRecoveryMailService {
    private final MailService mailService;


    public PasswordRecoveryMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendPasswordRecoveryEmail(String email, String passwordRecoveryLink) {
        String subject = "Восстановление пароля";
        String body = String.format("Здравствуйте!\n\nДля восстановления пароля перейдите по следующей ссылке:\n%s\n\n" +
                        "Если вы не запрашивали восстановление пароля, проигнорируйте это письмо.\n\nС уважением, команда Qasqir Inventory.",
                passwordRecoveryLink);

        mailService.send(email, subject, body);
    }
}