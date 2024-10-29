package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.InvalidVerificationCodeException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.MailVerification;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.MailVerificationCheckRequest;
import kz.qasqir.qasqirinventory.api.model.request.MailVerificationSendRequest;
import kz.qasqir.qasqirinventory.api.repository.MailVerificationRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.verification.MailVerificationCodeGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MailVerificationService {
        private final MailService mailService;
        private final MailVerificationRepository mailVerificationRepository;
        private final MailVerificationCodeGenerate codeGenerate;
        private final UserRepository userRepository;

        @Autowired
        public MailVerificationService(MailService mailService,
                                       MailVerificationRepository mailVerificationRepository,
                                       MailVerificationCodeGenerate codeGenerate,
                                       UserRepository userRepository)
        {
                this.mailService = mailService;
                this.mailVerificationRepository = mailVerificationRepository;
                this.codeGenerate = codeGenerate;
                this.userRepository = userRepository;
        }



        @Transactional
        public boolean generate(MailVerificationSendRequest request) {
                MailVerification mailVerification = new  MailVerification(request.getEmail(), codeGenerate.generate());
                mailVerificationRepository.save(mailVerification);
                mailService.send(request.getEmail(), "Подтверждение регистрации аккаунта", "Для подтверждения регистрации введите 6-значный код в поле ввода в приложении: " + mailVerification.getCode());
                return true;
        }


        @Transactional
        public boolean verify(MailVerificationCheckRequest request) {
                System.out.println(request.getEmail() + "  " + request.getCode());
                MailVerification mailVerification = mailVerificationRepository.findByCodeAndEmail(request.getCode(), request.getEmail()).orElseThrow(InvalidVerificationCodeException::new);
                mailVerificationRepository.delete(mailVerification);
                User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);
                user.setEmailVerified(true);
                userRepository.save(user);
                return true;
        }
}
