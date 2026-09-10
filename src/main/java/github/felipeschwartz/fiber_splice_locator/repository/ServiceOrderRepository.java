package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.dto.CeoRecurrenceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.TechnicianServiceOrderCountDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByCeo_IdOrderByCreatedAtDesc(Long ceoId);

    List<ServiceOrder> findByUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("""
        SELECT new github.felipeschwartz.fiber_splice_locator.model.dto.TechnicianServiceOrderCountDTO(
                u.id, u.name, COUNT(so)
                ) 
                FROM ServiceOrder so
                JOIN so.user u
                WHERE so.createdAt BETWEEN :start AND :end
                GROUP BY u.id, u.name
                ORDER BY COUNT(so) DESC
    """)
    List<TechnicianServiceOrderCountDTO> countByTechnicianBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
        SELECT new github.felipeschwartz.fiber_splice_locator.model.dto.CeoRecurrenceDTO(
            c.id, c.boxNumber, COUNT(so), MAX(so.createdAt)
        )
        FROM ServiceOrder so
        JOIN so.ceo c
        WHERE so.createdAt BETWEEN :start AND :end
        GROUP BY c.id, c.boxNumber
        ORDER BY COUNT(so) DESC
    """)
    List<CeoRecurrenceDTO> countByCeoBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
