package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     Optional<User> findByEmail(String email);
     Optional<User> findByPhoneNumber(String phoneNumber);
     int deleteUserById(Long userId);
}
