package kz.qasqir.qasqirinventory.api.repository;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO t_organization_admins (user_id, organization_id)
            VALUES (:userId, :organization_id)
            """, nativeQuery = true)
    int addForAdmin(@Param("userId") Long userId, @Param("organization_id") Long organizationId);

    int deleteOrganizationById(Long organizationId);

}
