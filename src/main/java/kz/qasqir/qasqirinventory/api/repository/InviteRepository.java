package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {

    Optional<Invite> findByToken(String token);

    int deleteByToken(String token);

//    @Query(value = """
//        SELECT i.id AS id, u.user_name AS userName, u.email AS email
//        FROM t_invites i
//        JOIN t_users u ON i.user_id = u.id
//        """, nativeQuery = true)
//    List<InviteUserDTO> findInviteIdAndUserNameAndEmail();

    @Query("SELECT new kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO" +
            "(i.id, u.userName, u.email) " +
            "FROM Invite i JOIN User u ON i.userId = u.id")
    List<InviteUserDTO> findInviteIdAndUserNameAndEmail();

    int deleteInviteById(Long inviteId);

}

