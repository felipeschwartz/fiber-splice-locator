package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.ServiceOrderStatusDescriptionControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderStatusDescriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/service_orders_status_descriptions/v1")
@Tag(name = "Service Orders Status Descriptions", description = "Endpoint for managing Service Orders Status Descriptions")
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
    public ResponseEntity<List<ServiceOrderStatusDescriptionDTO>> findByServiceOrder(@PathVariable Long serviceOrderId) {
        return ResponseEntity.ok(service.findByServiceOrderId(serviceOrderId));
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderStatusDescriptionDTO> findById(@PathVariable("id") Long id) {
        ServiceOrderStatusDescriptionDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ServiceOrderStatusDescriptionDTO> create(@RequestBody @Valid ServiceOrderStatusDescriptionDTO serviceOrderStatusDescriptionDTO) {
        ServiceOrderStatusDescriptionDTO createdServiceOrderStatusDescription = service.create(serviceOrderStatusDescriptionDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(createdServiceOrderStatusDescription.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdServiceOrderStatusDescription);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ServiceOrderStatusDescriptionDTO> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ServiceOrderStatusDescriptionDTO serviceOrderStatusDescriptionDTO
    ) {
        ServiceOrderStatusDescriptionDTO updated = service.update(id, serviceOrderStatusDescriptionDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
