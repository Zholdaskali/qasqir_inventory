package kz.qasqir.qasqirinventory.api.repository;

import jakarta.persistence.Tuple;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     Optional<User> findByUserName(String userName);
     Optional<User> findByEmail(String email);

     int deleteUserById(Long userId);
}
