package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderPhotoRepository extends JpaRepository<ServiceOrderPhoto, Long> {

    List<ServiceOrderPhoto> findByServiceOrder_ServiceOrderId(Long serviceOrderId);

    int countByServiceOrder_ServiceOrderId(Long serviceOrderId);
}