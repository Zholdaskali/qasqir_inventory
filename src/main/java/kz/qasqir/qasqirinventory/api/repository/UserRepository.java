package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.MailVerification;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
     Optional<User> findByUserName(String userName);
     Optional<User> findByEmail(String email);
}
