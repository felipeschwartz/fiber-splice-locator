package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.ServiceOrderPhotoControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderPhotoService;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/service_order_photos/v1")
@Tag(name = "Service Order Photos", description = "Endpoint for managing Service Order Photos")
public class ServiceOrderPhotoController implements ServiceOrderPhotoControllerDocs {

    private final ServiceOrderPhotoService service;

    public ServiceOrderPhotoController(ServiceOrderPhotoService service) {
        this.service = service;
    }

    @GetMapping(value = "/service-order/{serviceOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<List<ServiceOrderPhotoDTO>> findAllByServiceOrder(@PathVariable("serviceOrderId") Long serviceOrderId) {
        return ResponseEntity.ok(service.findAllByServiceOrder(serviceOrderId));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ServiceOrderPhotoDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> content(@PathVariable("id") Long id) {
        ServiceOrderPhotoService.LoadedPhoto loaded = service.loadContent(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loaded.contentType()))
                .body(loaded.resource());
    }

    @PostMapping(
            value = "/service-order/{serviceOrderId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ServiceOrderPhotoDTO> upload(
            @PathVariable("serviceOrderId") Long serviceOrderId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        ServiceOrderPhotoDTO created = service.savePhoto(serviceOrderId, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/service_order_photos/v1/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ServiceOrderPhotoDTO> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ServiceOrderPhotoDTO serviceOrderPhotoDTO
    ) {
        return ResponseEntity.ok(service.update(id, serviceOrderPhotoDTO));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}