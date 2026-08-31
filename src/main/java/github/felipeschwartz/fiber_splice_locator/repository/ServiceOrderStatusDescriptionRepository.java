package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderStatusDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderStatusDescriptionRepository extends JpaRepository<ServiceOrderStatusDescription, Long> {
    List<ServiceOrderStatusDescription> findByServiceOrder_ServiceOrderIdOrderByCreatedAtAsc(Long serviceOrderId);
}
