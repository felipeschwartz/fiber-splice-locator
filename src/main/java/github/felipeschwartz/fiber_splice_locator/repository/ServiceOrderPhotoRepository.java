package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOrderPhotoRepository extends JpaRepository<ServiceOrder, Long> {
}
