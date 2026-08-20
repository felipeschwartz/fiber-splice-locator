package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CEORepository extends JpaRepository<CEO,Long> {
}
