package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.model.dto.CeoRecurrenceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.TechnicianServiceOrderCountDTO;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReportService {
    private final ServiceOrderRepository serviceOrderRepository;

    public ReportService(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public List<TechnicianServiceOrderCountDTO> serviceOrdersByTechnician(LocalDate from, LocalDate to) {
        return serviceOrderRepository.countByTechnicianBetween(from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public List<CeoRecurrenceDTO> ceoRecurrence(LocalDate from, LocalDate to) {
        return serviceOrderRepository.countByCeoBetween(from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }
}
