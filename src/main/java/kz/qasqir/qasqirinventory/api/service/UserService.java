package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.exception.NumberIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.request.UpdateUserRequest;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ImageService imageService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, ImageService imageService)
    {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.imageService = imageService;
    }

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

    public boolean checkIfEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkIfNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
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

        if (!Objects.equals(updateUser.getEmail(), updateUserRequest.getUserEmail())) {
            updateUser.setEmailVerified(false);
        }

        updateUser.setUserName(updateUserRequest.getUserName());

        validateEmail(updateUserRequest.getUserEmail(), userId);
        updateUser.setEmail(updateUserRequest.getUserEmail());

        validatePhoneNumber(updateUserRequest.getUserNumber(), userId);
        updateUser.setPhoneNumber(updateUserRequest.getUserNumber());

        userRepository.save(updateUser);

        return convertToUserDTO(updateUser);
    }

    private void validateEmail(String email, Long userId) {
        boolean emailExists = userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(userId)) // Исключаем текущего пользователя
                .isPresent();
        if (emailExists) {
            throw new EmailIsAlreadyRegisteredException();
        }
    }

    private void validatePhoneNumber(String phoneNumber, Long userId) {
        boolean phoneExists = userRepository.findByPhoneNumber(phoneNumber)
                .filter(user -> !user.getId().equals(userId)) // Исключаем текущего пользователя
                .isPresent();
        if (phoneExists) {
            throw new NumberIsAlreadyRegisteredException();
        }
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
}
