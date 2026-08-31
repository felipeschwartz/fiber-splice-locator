package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.ServiceOrderControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderAttendanceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
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
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<ServiceOrderDTO>> create(@RequestBody @Valid ServiceOrderDTO request) {
        ServiceOrderDTO created = service.create(request);
        return createdResponse(created);
    }

    @PostMapping(value = "/open", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<ServiceOrderDTO>> open(@RequestBody @Valid ServiceOrderDTO request) {
        ServiceOrderDTO created = service.open(request);
        return createdResponse(created);
    }

    private ResponseEntity<EntityModel<ServiceOrderDTO>> createdResponse(ServiceOrderDTO created) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/service_orders/v1/id/{id}")
                .buildAndExpand(created.getServiceOrderId())
                .toUri();
        return ResponseEntity.created(location)
                .body(EntityModel.of(created, created.getLinks()));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderDTO> update(@PathVariable("id") Long id,
                                                  @RequestBody @Valid ServiceOrderDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping(value = "/{id}/attendance", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderDTO> attend(@PathVariable("id") Long id,
                                                  @RequestBody @Valid ServiceOrderAttendanceDTO request) {
        return ResponseEntity.ok(service.attend(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
