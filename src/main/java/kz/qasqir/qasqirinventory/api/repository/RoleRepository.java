package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Метод для получения всех ролей пользователя по userId
    @Query(value = """
        SELECT r.id, r.role_name
        FROM t_roles r
        JOIN t_user_roles ur ON r.id = ur.role_id
        WHERE ur.user_id = :userId
    """, nativeQuery = true)
    List<Role> getAllForUserId(@Param("userId") Long userId);

    // Метод для добавления роли пользователю
    @Modifying
    @Query(value = """
        INSERT INTO t_user_roles (user_id, role_id)
        VALUES (:userId, :roleId)
    """, nativeQuery = true)
    int addForUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Modifying
    @Query(value = "DELETE FROM t_user_roles WHERE user_id = :userId AND role_id = :roleId", nativeQuery = true)
    void deleteRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

}
