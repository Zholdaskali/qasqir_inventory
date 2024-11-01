package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.MailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailVerificationRepository extends JpaRepository<MailVerification, Long> {
    Optional<MailVerification> findByCodeAndEmail(String code, String email);
}
