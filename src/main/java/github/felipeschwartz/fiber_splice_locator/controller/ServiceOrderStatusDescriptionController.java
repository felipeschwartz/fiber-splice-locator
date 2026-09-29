package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.ServiceOrderStatusDescriptionControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderStatusDescriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service_orders_status_descriptions/v1")
@Tag(name = "Service Orders Status Descriptions", description = "Endpoint for reading the status history of Service Orders")
public class ServiceOrderStatusDescriptionController implements ServiceOrderStatusDescriptionControllerDocs {

    private final ServiceOrderStatusDescriptionService service;

    public ServiceOrderStatusDescriptionController(ServiceOrderStatusDescriptionService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<List<ServiceOrderStatusDescriptionDTO>> findAll() {
        List<ServiceOrderStatusDescriptionDTO> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/service-order/{serviceOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<List<ServiceOrderStatusDescriptionDTO>> findByServiceOrder(@PathVariable Long serviceOrderId) {
        return ResponseEntity.ok(service.findByServiceOrderId(serviceOrderId));
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderStatusDescriptionDTO> findById(@PathVariable("id") Long id) {
        ServiceOrderStatusDescriptionDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
