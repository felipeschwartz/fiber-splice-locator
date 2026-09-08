package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.ReportControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.CeoRecurrenceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.TechnicianServiceOrderCountDTO;
import github.felipeschwartz.fiber_splice_locator.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports/v1")
@Tag(name = "Reports", description = "Aggregated indicators for service orders: technician productivity and CEO recurrence")
public class ReportController implements ReportControllerDocs {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping(value = "/service-orders-by-technician", produces = "application/json")
    @Override
    public ResponseEntity<List<TechnicianServiceOrderCountDTO>> serviceOrdersByTechnician(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(service.serviceOrdersByTechnician(from, to));
    }

    @GetMapping(value = "/ceo-recurrence", produces = "application/json")
    @Override
    public ResponseEntity<List<CeoRecurrenceDTO>> ceoRecurrence(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(service.ceoRecurrence(from, to));
    }
}
