package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.view.UserProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM UserProfileView u WHERE u.userId = :userId")
    Optional<UserProfileView> findProfileByUserId(Long userId);

    int deleteUserById(Long userId);
}

