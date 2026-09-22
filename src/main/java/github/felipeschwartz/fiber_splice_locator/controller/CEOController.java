package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.controller.docs.CEOControllerDocs;
import github.felipeschwartz.fiber_splice_locator.file.exporter.MediaTypes;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import github.felipeschwartz.fiber_splice_locator.service.CEOService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ceo/v1")
@Tag(name = "CEO", description = "Endpoints for managing Caixas de Emendas Opticas")
public class CEOController implements CEOControllerDocs {

    private final CEOService service;
    private final PagedResourcesAssembler<CEODTO> pagedResourcesAssembler;

    public CEOController(CEOService service, PagedResourcesAssembler<CEODTO> pagedResourcesAssembler) {
        this.service = service;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<CEODTO>>> findAll(
            @PageableDefault(size = 20, sort = "boxNumber") Pageable pageable,
            @RequestParam(required = false) List<CEOStatus> status
    ) {
        Page<CEODTO> page = service.findAll(pageable, status);
        PagedModel<EntityModel<CEODTO>> model = pagedResourcesAssembler.toModel(
                page,
                ceoDTO -> EntityModel.of(ceoDTO, ceoDTO.getLinks().stream().collect(Collectors.toList()))
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/exportPage", produces = {
            MediaTypes.APPLICATION_XLSX_VALUE,
            MediaTypes.APPLICATION_CSV_VALUE})
    @Override
    public ResponseEntity<Resource> exportPage(
            @PageableDefault(sort = "boxNumber") Pageable pageable,
            @RequestParam(required = false) List<CEOStatus> status,
            HttpServletRequest request
    ) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource file = service.exportPage(pageable, status, acceptHeader);
        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        var fileExtension = MediaTypes.APPLICATION_XLSX_VALUE.equalsIgnoreCase(acceptHeader) ? ".xlsx" : ".csv";
        var filename = "ceos_exported" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(file);
    }


    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<CEODTO>> findById(@PathVariable Long id) {
        CEODTO ceoDTO = service.findById(id);
        return ResponseEntity.ok(EntityModel.of(ceoDTO, ceoDTO.getLinks().stream().collect(Collectors.toList())));
    }

    @GetMapping("/box-number/{boxNumber}")
    @Override
    public ResponseEntity<List<CEODTO>> findByBoxNumber(
            @PathVariable String boxNumber) {
        return ResponseEntity.ok(service.findByBoxNumber(boxNumber));
    }

    @GetMapping("/search")
    @Override
    public ResponseEntity<List<CEODTO>> search(
            @RequestParam("q") String query) {
        return ResponseEntity.ok(service.search(query));
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
