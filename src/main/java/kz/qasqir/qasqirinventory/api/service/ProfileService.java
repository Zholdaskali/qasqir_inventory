package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.SessionNotFoundException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UpdateUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.view.UserProfileView;
import kz.qasqir.qasqirinventory.api.repository.UserProfileRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final SessionService sessionService;

    @Autowired
    public ProfileService(UserProfileRepository userProfileRepository,
                          SessionService sessionService)
    {
        this.userProfileRepository = userProfileRepository;
        this.sessionService = sessionService;
    }

    public Optional<UserProfileView> getUserProfile(String token) {
        if (token == null || token.isEmpty()) {
            System.out.println("Токен не предоставлен или пустой");
            throw new SessionNotFoundException();
        }

        User user = sessionService.getTokenForUser(token);
        if (user == null) {
            System.out.println("Пользователь не найден по токену: " + token);
            throw new UserNotFoundException();
        }
        return userProfileRepository.findProfileByUserId(user.getId());
    }
}
