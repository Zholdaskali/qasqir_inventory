package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Query(value = "SELECT * FROM t_password_reset_tokens WHERE token = :token", nativeQuery = true)
    Optional<PasswordResetToken> findByPasswordResetToken(@Param("token") String token);

}
