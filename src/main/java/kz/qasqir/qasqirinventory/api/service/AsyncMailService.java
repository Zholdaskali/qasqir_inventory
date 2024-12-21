package kz.qasqir.qasqirinventory.api.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Async
@Service
    public class AsyncMailService {
    private final MailService mailService;

    public AsyncMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void send(String to, String subject, String text) {
        try {
            mailService.send(to, subject, text);
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при отправление ассихронного письма");
        }
    }
}
