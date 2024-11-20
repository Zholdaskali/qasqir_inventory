package kz.qasqir.qasqirinventory.api.service;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.request.UpdateUserRequest;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService)
    {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<UserDTO> getUserAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    private UserDTO convertToDTO(User user) {

        List<Role> roles = roleService.getAllForUserId(user.getId());
        List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        return new UserDTO(user.getId(), user.getUserName(), user.getEmail(), user.getUserNumber(), user.isEmailVerified(), roleNames, user.getRegistrationDate());
    }

    public User getByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean checkIfEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public boolean deleteUserById(Long userId) {
            return userRepository.deleteUserById(userId) > 0;
    }

    @Transactional
    public UserDTO updateUser(UpdateUserRequest updateUserRequest) {
        User updateUser = userRepository.findById(updateUserRequest.getUserId()).orElseThrow(UserNotFoundException::new);
        updateUser.setUserName(updateUserRequest.getUserName());
        updateUser.setEmail(updateUserRequest.getUserEmail());
        userRepository.save(updateUser);
        return getUserProfileByUserId(updateUserRequest.getUserId());
    }

    @Transactional
    public UserDTO updateRole(Long userId, UserRoleResetRequest userRoleResetRequest) {
        User updateUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        List<Role> roles = roleService.getAllForUserId(userId);
        boolean roleExists = roles.stream()
                .anyMatch(role -> Objects.equals(role.getId(), userRoleResetRequest.getNewRoleId()));

        if (!roleExists) {
            roleService.addForUser(updateUser.getId(), userRoleResetRequest.getNewRoleId());
        }
        return getUserProfileByUserId(updateUser.getId());
    }
    public UserDTO getUserProfileByUserId(Long userId) {
        List<Tuple> tuples = userRepository.findProfileByUserId(userId);

        Tuple first = tuples.get(0);
        Long id = first.get("user_id", Long.class);
        String userName = first.get("user_name", String.class);
        String email = first.get("email", String.class);
        String userNumber = first.get("phone_number", String.class);
        Timestamp registrationTimestamp = first.get("registration_date", Timestamp.class);
        LocalDateTime registrationDate = registrationTimestamp != null ? registrationTimestamp.toLocalDateTime() : null;
        boolean emailVerified = first.get("email_verified", Boolean.class);

        List<String> roles = tuples.stream()
                .map(tuple -> tuple.get("role_name", String.class))
                .collect(Collectors.toList());

        UserDTO userProfile = new UserDTO(id, userName, email, userNumber, emailVerified, roles, registrationDate);
        return userProfile;
    }

}
