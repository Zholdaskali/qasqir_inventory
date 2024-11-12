package kz.qasqir.qasqirinventory.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UpdateUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public boolean deleteUserById(Long userId, Long organizationId) {
        if (Objects.equals(getByUserId(userId).getOrganizationId(), organizationId)) {
            return userRepository.deleteUserById(userId) > 0;
        }
        throw new UserNotFoundException();
    }

    @Transactional
    public User updateUser(UpdateUserDTO updateUserDTO) {
        User updateUser = userRepository.findById(updateUserDTO.getUserId()).orElseThrow(UserNotFoundException::new);
        updateUser.setUserName(updateUserDTO.getUserName());
        updateUser.setEmail(updateUserDTO.getUserEmail());
        return userRepository.save(updateUser);
    }

    @Transactional
    public User updateRole(UserRoleResetRequest userRoleResetRequest) {
        User updateUser = userRepository.findById(userRoleResetRequest.getUserId())
                .orElseThrow(UserNotFoundException::new);
        List<Role> roles = roleService.getAllForUserId(userRoleResetRequest.getUserId());
        boolean roleExists = roles.stream()
                .anyMatch(role -> Objects.equals(role.getId(), userRoleResetRequest.getNewRoleId()));

        if (!roleExists) {
            roleService.addForUser(updateUser.getId(), userRoleResetRequest.getNewRoleId());
        }
        return updateUser;
    }

}
