package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UpdateUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    @Transactional
    public boolean deleteUserById(Long userId, Long organizationId) {
        if (Objects.equals(getByUserId(userId).getOrganizationId(), organizationId)) {
            return userRepository.deleteUserById(userId) > 0;
        }
        throw new UserNotFoundException();
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        User updateUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        updateUser.setUserName(updateUserDTO.getUserName());
        updateUser.setEmail(updateUserDTO.getUserEmail());
        return userRepository.save(updateUser);
    }
}
