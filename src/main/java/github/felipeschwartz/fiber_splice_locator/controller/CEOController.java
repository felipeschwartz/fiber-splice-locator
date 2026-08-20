package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.CEOControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.service.CEOService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ceo/v1")
@Tag(name = "CEO", description = "Endpoints for managing Caixas de Emendas Opticas")
public class CEOController implements CEOControllerDocs {

    private final CEOService service;

    public CEOController(CEOService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<CollectionModel<EntityModel<CEODTO>>> findAll() {
        List<CEODTO> users = service.findAll();
        List<EntityModel<CEODTO>> userModels = users.stream()
                .map(ceoDTO -> EntityModel.of(ceoDTO, ceoDTO.getLinks().stream().collect(Collectors.toList())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(userModels, linkTo(methodOn(CEOController.class).findAll()).withSelfRel())
        );
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<CEODTO>> findById(@PathVariable Long id) {
        CEODTO ceoDTO = service.findById(id);
        return ResponseEntity.ok(EntityModel.of(ceoDTO, ceoDTO.getLinks().stream().collect(Collectors.toList())));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<EntityModel<CEODTO>> create(@RequestBody @Valid CEODTO ceoDTO) {
        CEODTO createdCEO = service.create(ceoDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(createdCEO.getId())
                .toUri();
        return ResponseEntity.created(location).body(EntityModel.of(createdCEO, createdCEO.getLinks().stream().collect(Collectors.toList())));
    }


    @PutMapping(
            value = "/id/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<EntityModel<CEODTO>> update(
            @PathVariable Long id,
            @RequestBody @Valid CEODTO ceoDTO
    ) {
        ceoDTO.setId(id);

        CEODTO updatedCEO = service.update(ceoDTO);

        return ResponseEntity.ok(
                EntityModel.of(
                        updatedCEO,
                        updatedCEO.getLinks()
                )
        );
    }


    @DeleteMapping(value = "/id/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
