package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NomenclatureRepository extends JpaRepository<Nomenclature, Long> {
    List<Nomenclature> findAllByCategoryId(Long categoryId);
}
