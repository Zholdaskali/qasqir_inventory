package kz.qasqir.qasqirinventory.api.service.defaultservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.MailVerification;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ImageService imageService;
    private final ValidateUserDataService validateUserDataService;
    private final MailVerificationService mailVerificationService;

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<UserDTO> getUserAll() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .toList();
    }

    public User getByUserEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public boolean deleteUserById(Long userId) {
            return userRepository.deleteUserById(userId) > 0;
    }

    @Transactional
    public UserDTO updateUser(UpdateUserRequest updateUserRequest, Long userId) {
        User updateUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        updateUser.setUserName(updateUserRequest.getUserName());

        validateUserDataService.validateUserPhoneNumber(updateUserRequest.getUserNumber(), userId);
        updateUser.setPhoneNumber(updateUserRequest.getUserNumber());

        userRepository.save(updateUser);

        return convertToUserDTO(updateUser);
    }

    @Transactional
    public UserDTO updateEmailCheck(MailVerificationCheckRequest mailVerificationCheckRequest, Long userId) {
        if (mailVerificationService.verifyNewEmail(mailVerificationCheckRequest)) {
            User user = getByUserId(userId);
            user.setEmail(mailVerificationCheckRequest.getEmail());
            userRepository.save(user);
            return convertToUserDTO(user);
        } else {
            throw new RuntimeException("Код не верный");
        }
    }

    @Transactional
    public String updateEmailGenerate(UpdateEmailGenerateRequest updateEmailRequest) {
        User user = getByUserId(updateEmailRequest.getUserId());
        validateUserDataService.validatePassword(updateEmailRequest.getPassword(), user.getPassword());
        MailVerificationSendRequest mailVerificationSendRequest = new MailVerificationSendRequest(updateEmailRequest.getEmail());
        mailVerificationService.generate(mailVerificationSendRequest);
        return "Успешно отправлено";
    }

    @Transactional
    public UserDTO updateRole(Long userId, UserRoleResetRequest userRoleResetRequest) {
        List<Long> newRoles = userRoleResetRequest.getNewRoles();
        List<Role> currentRoles = roleService.getAllForUserId(userId);
        List<Long> currentRoleIds = currentRoles.stream()
                .map(Role::getId)
                .toList();
        List<Long> rolesToDelete = currentRoleIds.stream()
                .filter(roleId -> !newRoles.contains(roleId))
                .toList();
        List<Long> rolesToAdd = newRoles.stream()
                .filter(roleId -> !currentRoleIds.contains(roleId))
                .toList();
        rolesToDelete.forEach(roleId -> roleService.removeRoleFromUser(userId, roleId));
        rolesToAdd.forEach(roleId -> roleService.addForUser(userId, roleId));
        User user = getByUserId(userId);
        return convertToUserDTO(user);
    }

    public UserDTO getUserProfileByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException :: new);
        return convertToUserDTO(user);
    }

    private UserDTO convertToUserDTO(User user) {
        List<Role> roles = roleService.getAllForUserId(user.getId());
        List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        String imagePath = user.getImageId() != null
                ? imageService.getImagePath(user.getImageId())
                : null;
        return new UserDTO(user.getId(), user.getUserName(), user.getEmail(), user.getPhoneNumber(),
                user.isEmailVerified(), roleNames, user.getRegistrationDate(), imagePath);
    }


    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        Map<Long, List<String>> userRolesMap = new HashMap<>();
        List<Object[]> rolesData = roleService.getRolesForUsers();

        for (Object[] row : rolesData) {
            Long userId = (Long) row[0];
            String roleName = (String) row[1];
            userRolesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(roleName);
        }

        return users.stream()
                .map(user -> {
                    List<String> roles = userRolesMap.getOrDefault(user.getId(), Collections.emptyList());
                    String imagePath = (user.getImageId() != null) ? "/images/" + user.getImageId() : null;

                    return new UserDTO(
                            user.getId(),
                            user.getUserName(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            user.isEmailVerified(),
                            roles,
                            user.getRegistrationDate(),
                            imagePath
                    );
                })
                .collect(Collectors.toList());

    }
}
