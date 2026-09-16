package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CEORepository extends JpaRepository<CEO,Long> {
    List<CEO> findByBoxNumberContainingIgnoreCase(String boxNumber);
    Page<CEO> findByStatusIn(List<CEOStatus> statuses, Pageable pageable);

    // Ordenação alfabética do enum não reflete gravidade (Danificada deveria
    // vir antes de Padronizada, por exemplo), então usamos um CASE nativo pra
    // ranquear por severidade. "* :direction" (1 ou -1) cobre asc/desc com
    // uma query só, em vez de duplicar pra cada sentido.
    @Query(value = """
            SELECT * FROM ceos
            ORDER BY (CASE status
                WHEN 'DAMAGED' THEN 0
                WHEN 'UNDER_MAINTENANCE' THEN 1
                WHEN 'STANDARDIZED' THEN 2
                WHEN 'CANCELLED' THEN 3
                ELSE 4
            END) * :direction ASC
            """,
            countQuery = "SELECT count(*) FROM ceos",
            nativeQuery = true)
    Page<CEO> findAllOrderByStatusSeverity(@Param("direction") int direction, Pageable pageable);

    @Query(value = """
            SELECT * FROM ceos WHERE status IN (:statuses)
            ORDER BY (CASE status
                WHEN 'DAMAGED' THEN 0
                WHEN 'UNDER_MAINTENANCE' THEN 1
                WHEN 'STANDARDIZED' THEN 2
                WHEN 'CANCELLED' THEN 3
                ELSE 4
            END) * :direction ASC
            """,
            countQuery = "SELECT count(*) FROM ceos WHERE status IN (:statuses)",
            nativeQuery = true)
    Page<CEO> findByStatusInOrderByStatusSeverity(
            @Param("statuses") List<String> statuses, @Param("direction") int direction, Pageable pageable);
}
