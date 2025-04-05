package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface NomenclatureRepository extends JpaRepository<Nomenclature, Long> {
    @Query("SELECT n FROM Nomenclature n JOIN FETCH n.category WHERE n.category.id = :categoryId")
    List<Nomenclature> findByCategoryId(@Param("categoryId") Long categoryId);
}
