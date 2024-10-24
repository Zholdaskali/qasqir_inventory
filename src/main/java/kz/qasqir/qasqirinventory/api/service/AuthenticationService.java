package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.AuthenticationErrorException;
import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.exception.UserAlreadyExistsException;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.MailVerificationRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionService sessionService;
    @Autowired
    private MailVerificationRepository mailVerificationRepository;
    @Autowired
    private RoleService roleService;

    @Transactional
    public String register(String userName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailIsAlreadyRegisteredException();
        }

        User user = new User(userName, email, passwordEncoder.hash(password), 1);
        userService.saveUser(user);
        roleService.addForUser(user.getId(), 1L);
        return "Пользователь успешно создан";
    }


    public Session login(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(AuthenticationErrorException::new);

        if (passwordEncoder.check(password, user.getPassword())) {
            sessionService.manageCountSession(user.getId());
            return sessionService.generateForUser(user.getId());
        } else {
            throw new InvalidPasswordException();
        }
    }


    public boolean logout(String token)  {
        return sessionService.invalidate(token);
    }
}

