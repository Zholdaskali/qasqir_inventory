package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.exception.InvalidPasswordException;
import kz.qasqir.qasqirinventory.api.exception.NumberIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import kz.qasqir.qasqirinventory.api.util.encoder.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class ValidateDataService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ValidateDataService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Проверка уникальности email
    public void ensureEmailIsUnique(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new EmailIsAlreadyRegisteredException();
        });
    }

    // Проверка уникальности номера
    public void ensurePhoneNumberIsUnique(String phoneNumber) {
        userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
            throw new NumberIsAlreadyRegisteredException();
        });
    }

    // Проверка уникальности email для конкретного пользователя
    public void validateUserEmail(String email, Long userId) {
        validateUniqueness(email, userId, userRepository::findByEmail, EmailIsAlreadyRegisteredException::new);
    }

    // Проверка уникальности номера для конкретного пользователя
    public void validateUserPhoneNumber(String phoneNumber, Long userId) {
        validateUniqueness(phoneNumber, userId, userRepository::findByPhoneNumber, NumberIsAlreadyRegisteredException::new);
    }

    private void validateUniqueness(String value, Long userId,
                                    Function<String, Optional<User>> findByField,
                                    Supplier<RuntimeException> exceptionSupplier) {
        boolean exists = findByField.apply(value)
                .filter(user -> !user.getId().equals(userId)) // Исключаем текущего пользователя
                .isPresent();
        if (exists) {
            throw exceptionSupplier.get();
        }
    }

    public void validatePassword(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.check(rawPassword, hashedPassword)) {
            throw new InvalidPasswordException();
        }
    }
}

