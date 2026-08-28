package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CEORepository extends JpaRepository<CEO,Long> {
    List<CEO> findByBoxNumberContainingIgnoreCase(String boxNumber);
}
