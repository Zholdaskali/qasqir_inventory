package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.*;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.RegisterInviteRequest;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final RoleService roleService;
    private final InviteService inviteService;
    private final LoginLogService loginLogService;
    private final InviteMailService inviteMailService;
    private final ValidateDataService validateDataService;

    @Value("${invite.link}")
    private String INVITE_LINK;

    @Value("${company.data.support-email}")
    private String SUPPORT_EMAIL;

    @Value("${company.data.support-phone}")
    private String SUPPORT_PHONE;

    @Value("${company.data.company-contact-info}")
    private String COMPANY_CONTACT_INFO;

    @Autowired
    public AuthenticationService(UserService userService,
                                 PasswordEncoder passwordEncoder,
                                 SessionService sessionService,
                                 RoleService roleService,
                                 InviteService inviteService,
                                 LoginLogService loginLogService,
                                 InviteMailService inviteMailService,
                                 ValidateDataService validateDataService)
    {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.roleService = roleService;
        this.inviteService = inviteService;
        this.loginLogService = loginLogService;
        this.inviteMailService = inviteMailService;
        this.validateDataService = validateDataService;
    }

    @Transactional
    public String registerInvite(RegisterInviteRequest registerInviteRequest) {
        try {
            validateDataService.ensureEmailIsUnique(registerInviteRequest.getEmail());
            validateDataService.ensurePhoneNumberIsUnique(registerInviteRequest.getUserNumber());
            User savedUser = createUser(registerInviteRequest.getUserName(), registerInviteRequest.getEmail(), registerInviteRequest.getUserNumber(), registerInviteRequest.getPassword());
            userService.saveUser(savedUser);
            List<Long> userRoles = registerInviteRequest.getUserRoles();

            for (Long userRole : userRoles) {
                roleService.addForUser(savedUser.getId(), userRole);
            }

            String inviteLink = inviteService.generate(INVITE_LINK, savedUser.getId()).getLink();
            inviteMailService.sendInviteEmail(registerInviteRequest.getUserName(), registerInviteRequest.getEmail(), registerInviteRequest.getPassword(), inviteLink, SUPPORT_EMAIL, SUPPORT_PHONE, COMPANY_CONTACT_INFO);
            System.out.println("Контроллер: " + Thread.currentThread().getName());
            return "Приглашение отправлено";
        } catch (EmailIsAlreadyRegisteredException | NumberIsAlreadyRegisteredException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось зарегистрировать приглашение, попробуйте позже.");
        }
    }

    public Session login(String email, String password) {
        try {
            User user = userService.getByUserEmail(email);
            validateDataService.validatePassword(password, user.getPassword());

            loginLogService.save(user.getId());
            sessionService.manageCountSession(user.getId());
            return sessionService.generateForUser(user.getId());
        } catch (InvalidPasswordException e) {
            throw e;
        } catch (Exception e) {
            throw new LoginFailedException();
        }
    }

    public boolean logout(String token) {
        try {
            return sessionService.invalidate(token);
        } catch (Exception e) {
            throw new LogOutFailedException();
        }
    }

    private User createUser(String userName, String email, String userNumber, String password) {
        return new User(userName, email, userNumber, passwordEncoder.hash(password));
    }

}


