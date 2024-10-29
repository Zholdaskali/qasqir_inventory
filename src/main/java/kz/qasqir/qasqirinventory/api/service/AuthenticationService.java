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
    private final UserRepository userRepository;
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
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 SessionService sessionService,
                                 RoleService roleService,
                                 InviteService inviteService,
                                 OrganizationService organizationService)
    {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.roleService = roleService;
        this.inviteService = inviteService;
        this.organizationService = organizationService;
    }

    @Transactional
    public Invite registerInvite(HttpServletRequest request, String userName, String email, String password) {
        validateEmail(email);

        String token = request.getHeader("Auth-token");
        User authorUser = sessionService.getTokenForUser(token);
        User savedUser = createUser(userName, email, password, authorUser.getOrganizationId());

        userService.saveUser(savedUser);
        roleService.addForUser(savedUser.getId(), EMPLOYEE_ROLE_ID);
        return inviteService.generate(INVITE_LINK, savedUser.getId());
    }

    @Transactional
    public String register(String userName, String email, String password, Long organizationId) {
        validateEmail(email);

        User user = createUser(userName, email, password, organizationId);
        userService.saveUser(user);
        roleService.addForUser(user.getId(), ADMIN_ROLE_ID);
        organizationService.addForAdmin(user.getId(), organizationId);
        return "Пользователь успешно создан";
    }

    public Session login(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(AuthenticationErrorException::new);

        validatePassword(password, user.getPassword());

        sessionService.manageCountSession(user.getId());
        return sessionService.generateForUser(user.getId());
    }

    public boolean logout(String token) {
        return sessionService.invalidate(token);
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailIsAlreadyRegisteredException();
        }
    }

    private User createUser(String userName, String email, String password, Long organizationId) {
        return new User(userName, email, passwordEncoder.hash(password), organizationId);
    }

    private void validatePassword(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.check(rawPassword, hashedPassword)) {
            throw new InvalidPasswordException();
        }
    }
}


