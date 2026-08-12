package github.felipeschwartz.fiber_splice_locator.controller;

import com.github.felipeschwartz.parkingsystem.controller.docs.UserControllerDocs;
import com.github.felipeschwartz.parkingsystem.model.dto.CreateUserRequestDTO;
import com.github.felipeschwartz.parkingsystem.model.dto.UserDTO;
import com.github.felipeschwartz.parkingsystem.model.dto.UserEntityDTO;
import com.github.felipeschwartz.parkingsystem.model.dto.UserIndividualDTO;
import com.github.felipeschwartz.parkingsystem.service.UserService;
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

    @GetMapping(value = "/cpf/{cpf}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<UserIndividualDTO>> findByCpf(@PathVariable String cpf) {
        UserIndividualDTO userIndividualDTO = service.findByCpf(cpf);
        return ResponseEntity.ok(EntityModel.of(userIndividualDTO, userIndividualDTO.getLinks().stream().collect(Collectors.toList())));
    }

    @GetMapping(value = "/cnpj/{cnpj}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<EntityModel<UserEntityDTO>> findByCnpj(@PathVariable String cnpj) {
        UserEntityDTO userEntityDTO = service.findByCnpj(cnpj);
        return ResponseEntity.ok(EntityModel.of(userEntityDTO, userEntityDTO.getLinks().stream().collect(Collectors.toList())));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<EntityModel<UserDTO>> create(@RequestBody @Valid CreateUserRequestDTO requestDTO) {
        UserDTO createdUser = service.create(requestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(EntityModel.of(createdUser, createdUser.getLinks().stream().collect(Collectors.toList())));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<EntityModel<UserDTO>> update(@PathVariable Long id, @RequestBody @Valid CreateUserRequestDTO requestDTO) {
        UserDTO updatedUser = service.update(id, requestDTO);
        return ResponseEntity.ok(EntityModel.of(updatedUser, updatedUser.getLinks().stream().collect(Collectors.toList())));
    }

    @DeleteMapping(value = "/id/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}