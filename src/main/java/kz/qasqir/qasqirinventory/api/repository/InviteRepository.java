package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import kz.qasqir.qasqirinventory.api.model.entity.Session;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Long> {
    Optional<Invite> findByToken(String token);
    int deleteByToken(String token);

    @Modifying
    @Query(value = """
        INSERT INTO t_user_roles (user_id, role_id)
        VALUES (:userId, :roleId)
        """, nativeQuery = true)
    int addForUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
