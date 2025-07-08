package kz.qasqir.qasqirinventory.api.service.mail;

public interface MailSenderService {
    void send(String to, String subject, String text);
}
