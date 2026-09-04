package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByCeo_IdOrderByCreatedAtDesc(Long ceoId);
}
