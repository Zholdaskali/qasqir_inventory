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
import kz.qasqir.qasqirinventory.api.repository.RoleRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ImageService imageService;
    private final ValidateDataService validateDataService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, ImageService imageService, ValidateDataService validateDataService)
    {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.imageService = imageService;
        this.validateDataService = validateDataService;
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

        validateDataService.validateUserEmail(updateUserRequest.getUserEmail(), userId);
        updateUser.setEmail(updateUserRequest.getUserEmail());

        validateDataService.validateUserPhoneNumber(updateUserRequest.getUserNumber(), userId);
        updateUser.setPhoneNumber(updateUserRequest.getUserNumber());

        userRepository.save(updateUser);

        return convertToUserDTO(updateUser);
    }

    @Transactional
    public UserDTO updateRole(Long userId, UserRoleResetRequest userRoleResetRequest) {
        List<Long> newRoles = userRoleResetRequest.getNewRoles();

        // Получаем текущие роли пользователя
        List<Role> currentRoles = roleService.getAllForUserId(userId);

        // Получаем идентификаторы текущих ролей
        List<Long> currentRoleIds = currentRoles.stream()
                .map(Role::getId)
                .toList();

        // Определяем роли, которые нужно удалить (старые роли, которых нет в новых)
        List<Long> rolesToDelete = currentRoleIds.stream()
                .filter(roleId -> !newRoles.contains(roleId))
                .toList();

        // Определяем роли, которые нужно добавить (новые роли, которых нет в текущих)
        List<Long> rolesToAdd = newRoles.stream()
                .filter(roleId -> !currentRoleIds.contains(roleId))
                .toList();

        // Удаляем лишние роли
        rolesToDelete.forEach(roleId -> roleService.removeRoleFromUser(userId, roleId));

        // Добавляем новые роли
        rolesToAdd.forEach(roleId -> roleService.addForUser(userId, roleId));

        // Получаем обновленного пользователя
        User user = getByUserId(userId);

        // Преобразуем пользователя в DTO
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
        // Шаг 1: Получить всех пользователей
        List<User> users = userRepository.findAll();

        // Шаг 2: Получить роли для пользователей
        Map<Long, List<String>> userRolesMap = new HashMap<>();
        List<Object[]> rolesData = roleService.getRolesForUsers();

        for (Object[] row : rolesData) {
            Long userId = (Long) row[0];
            String roleName = (String) row[1];
            userRolesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(roleName);
        }

        // Шаг 3: Преобразовать пользователей в DTO
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
