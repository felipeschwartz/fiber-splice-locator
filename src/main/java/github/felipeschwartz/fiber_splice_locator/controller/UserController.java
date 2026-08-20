package github.felipeschwartz.fiber_splice_locator.controller;


import github.felipeschwartz.fiber_splice_locator.controller.docs.UserControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.service.UserService;
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
@RequestMapping("/api/user/v1")
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController implements UserControllerDocs {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findAll() {
        List<UserDTO> users = service.findAll();
        List<EntityModel<UserDTO>> userModels = users.stream()
                .map(userDTO -> EntityModel.of(userDTO, userDTO.getLinks().stream().collect(Collectors.toList())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(userModels, linkTo(methodOn(UserController.class).findAll()).withSelfRel())
        );
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<UserDTO>> findById(@PathVariable Long id) {
        UserDTO userDTO = service.findById(id);
        return ResponseEntity.ok(EntityModel.of(userDTO, userDTO.getLinks().stream().collect(Collectors.toList())));
    }



    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<EntityModel<UserDTO>> create(@RequestBody @Valid UserDTO userDTO) {
        UserDTO createdUser = service.create(userDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(EntityModel.of(createdUser, createdUser.getLinks().stream().collect(Collectors.toList())));
    }

    @PutMapping(
            value = "/id/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<EntityModel<UserDTO>> update(
            @PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO
    ) {
        userDTO.setId(id);

        UserDTO updatedUser = service.update(userDTO);

        return ResponseEntity.ok(
                EntityModel.of(
                        updatedUser,
                        updatedUser.getLinks().stream().collect(Collectors.toList())
                )
        );
    }


    @PatchMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public UserDTO disableUser(Long id) {
        return service.disableUser(id);
    }

    @DeleteMapping(value = "/id/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}