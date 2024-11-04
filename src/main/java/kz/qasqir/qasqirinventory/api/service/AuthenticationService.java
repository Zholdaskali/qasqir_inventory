package kz.qasqir.qasqirinventory.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.AuthenticationErrorException;
import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.MailVerificationRepository;
import kz.qasqir.qasqirinventory.api.repository.OrganizationRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final RoleService roleService;
    private final InviteService inviteService;
    private final OrganizationService organizationService;

    @Value("${roleIds.employee}")
    private Long EMPLOYEE_ROLE_ID;

    @Value("${roleIds.admin}")
    private Long ADMIN_ROLE_ID;

    @Value("${invite.link}")
    private String INVITE_LINK;

    @Autowired
    public AuthenticationService(UserService userService,
                                 PasswordEncoder passwordEncoder,
                                 SessionService sessionService,
                                 RoleService roleService,
                                 InviteService inviteService,
                                 OrganizationService organizationService)
    {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.roleService = roleService;
        this.inviteService = inviteService;
        this.organizationService = organizationService;
    }

    @Transactional
    public Invite registerInvite(HttpServletRequest request, String userName, String email, String userNumber, String password) {
        if (validateEmail(email)) {
            String token = request.getHeader("Auth-token");
            User authorUser = sessionService.getTokenForUser(token);
            User savedUser = createUser(userName, email, userNumber, password, authorUser.getOrganizationId());

            userService.saveUser(savedUser);
            roleService.addForUser(savedUser.getId(), EMPLOYEE_ROLE_ID);
            return inviteService.generate(INVITE_LINK, savedUser.getId());
        }
        throw new EmailIsAlreadyRegisteredException();
    }

    @Transactional
    public String register(String userName, String email, String userNumber, String password, Long organizationId) {
        if (validateEmail(email)) {
            User user = createUser(userName, email,userNumber, password, organizationId);
            userService.saveUser(user);
            roleService.addForUser(user.getId(), ADMIN_ROLE_ID);
            organizationService.addForAdmin(user.getId(), organizationId);
            return "Пользователь успешно создан";
        }
        throw new EmailIsAlreadyRegisteredException();
    }

    public Session login(String userName, String password) {
        User user = userService.getByUserName(userName);
        validatePassword(password, user.getPassword());

        sessionService.manageCountSession(user.getId());
        return sessionService.generateForUser(user.getId());
    }

    public boolean logout(String token) {
        return sessionService.invalidate(token);
    }

    private boolean validateEmail(String email) {
        if (!userService.checkIfEmailExists(email)) {
            return true;
        }
        throw new EmailIsAlreadyRegisteredException();
    }

    private User createUser(String userName, String email, String userNumber, String password, Long organizationId) {
        return new User(userName, email, userNumber, passwordEncoder.hash(password), organizationId);
    }

    private void validatePassword(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.check(rawPassword, hashedPassword)) {
            throw new InvalidPasswordException();
        }
    }
}


