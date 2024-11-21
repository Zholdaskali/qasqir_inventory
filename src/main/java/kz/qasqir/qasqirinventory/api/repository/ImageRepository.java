package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO t_images (user_id, role_id)
        VALUES (:userId, :roleId)
        """, nativeQuery = true)
    int addImageForUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
