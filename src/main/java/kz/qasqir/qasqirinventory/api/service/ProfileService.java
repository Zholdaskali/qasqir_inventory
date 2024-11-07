package kz.qasqir.qasqirinventory.api.service;

import jakarta.persistence.Tuple;
import kz.qasqir.qasqirinventory.api.exception.SessionNotFoundException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UserProfileDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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



    public UserProfileDTO getUserProfileByUserId(String token) {
        User user = sessionService.getTokenForUser(token);

        List<Tuple> tuples = userProfileRepository.findProfileByUserId(user.getId());

        Tuple first = tuples.get(0);
        Long id = first.get("user_id", Long.class);
        String userName = first.get("user_name", String.class);
        String email = first.get("email", String.class);
        String userNumber = first.get("phone_number", String.class);
        boolean emailVerified = first.get("email_verified", Boolean.class);
        String organization = first.get("organization_name", String.class);

        List<String> roles = tuples.stream()
                .map(tuple -> tuple.get("role_name", String.class))
                .collect(Collectors.toList());

        UserProfileDTO userProfile = new UserProfileDTO(id, userName, email, userNumber, organization, emailVerified, roles);
        return userProfile;
    }

}
