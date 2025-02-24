package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseContainerRepository extends JpaRepository<WarehouseContainer, Long> {
    List<WarehouseContainer> findAllByWarehouseZoneId(Long zoneId);
}
