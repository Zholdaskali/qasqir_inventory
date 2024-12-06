package kz.qasqir.qasqirinventory.api.service;

import org.springframework.stereotype.Service;

@Service
public class InviteMailService {

    private final MailService mailService;
    private final OrganizationService organizationService;


    public InviteMailService(MailService mailService, OrganizationService organizationService) {
        this.mailService = mailService;
        this.organizationService = organizationService;
    }

    public void sendInviteEmail(String userName, String email, String password, String INVITE_LINK, String SUPPORT_EMAIL, String SUPPORT_PHONE, String COMPANY_CONTACT_INFO) {
        String emailBody = String.format(
                "Уважаемый(ая) %s,\n\n" +
                        "Мы рады пригласить вас присоединиться к нашей платформе QasqirInventory! Ваша учетная запись была создана, чтобы предоставить вам доступ к современным инструментам управления и учета.\n\n" +
                        "Ваши учетные данные:\n" +
                        "Логин: %s\n" + // Вставляем email или имя пользователя
                        "Временный пароль: %s\n" + // Если пароль передается, вставить его сюда, иначе оставить пустым
                        "(Пожалуйста, смените временный пароль при первом входе для вашей безопасности.)\n\n" +
                        "Как начать:\n" +
                        "Перейдите по ссылке: %s\n" + // Ссылка для активации
                        "Введите ваши учетные данные.\n" +
                        "Ознакомьтесь с основным функционалом и настройте профиль для комфортной работы.\n\n" +
                        "Если у вас возникнут вопросы, наша команда всегда готова помочь. Вы можете связаться с нами по %s или %s.\n\n" + // Контактная информация
                        "Спасибо за ваше доверие и участие!\n\n" +
                        "С уважением,\n" +
                        "Команда Qasqir Inventory\n" +
                        "%s", // Контактная информация компании
                userName, email, password, INVITE_LINK, SUPPORT_EMAIL, SUPPORT_PHONE, COMPANY_CONTACT_INFO
        );

        // Отправка письма
        mailService.send(email, "Приглашение к сотрудничеству в QasqirInventory", emailBody);
    }
}
