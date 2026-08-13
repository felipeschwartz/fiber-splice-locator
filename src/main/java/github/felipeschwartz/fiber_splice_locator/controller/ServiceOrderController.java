package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.ServiceOrderControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/service_orders/v1")
@Tag(name = "Service Orders", description = "Endpoint for managing Service Orders")
public class ServiceOrderController implements ServiceOrderControllerDocs {

    private final ServiceOrderService service;

    public ServiceOrderController(ServiceOrderService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<List<ServiceOrderDTO>> findAll() {
        List<ServiceOrderDTO> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderDTO> findById(@PathVariable("id") Long id) {
        ServiceOrderDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ServiceOrderDTO> create(@RequestBody @Valid ServiceOrderDTO serviceOrderDTO) {
        ServiceOrderDTO createdServiceOrder = service.create(serviceOrderDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(createdServiceOrder.getServiceOrderId())
                .toUri();
        return ResponseEntity.created(location).body(createdServiceOrder);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ServiceOrderDTO> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ServiceOrderDTO serviceOrderDTO
    ) {
        ServiceOrderDTO updated = service.update(id, serviceOrderDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
