package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.TransactionPlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionPlacementRepository extends JpaRepository<TransactionPlacement, Long> {
    List<TransactionPlacement> findAllByTransactionNomenclatureCode(String nomenclatureCode);
}
