package github.felipeschwartz.fiber_splice_locator.service;

import java.util.Set;
import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.controller.UserController;
import github.felipeschwartz.fiber_splice_locator.mapper.UserMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.ChangePasswordDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserSearchResultDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.InvalidCurrentPasswordException;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.ObjectNotFoundException;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.UserRoleOperationNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public List<UserDTO> findAll() {
        logger.info("Finding all Users!");
        List<UserDTO> userDTOS = userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        userDTOS.forEach(this::addHateoasLinks);
        return userDTOS;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public UserDTO findById(Long id) {
        logger.info("Finding one User by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User", id));
        UserDTO userDTO = userMapper.toDTO(user);
        addHateoasLinks(userDTO);
        return userDTO;
    }

    @Transactional(readOnly = true)
    public List<UserSearchResultDTO> search(String query) {
        logger.info("Searching for Users by query: {}", query);
        String value = query == null ? "" : query.trim();
        List<User> results = value.matches("\\d+")
                ? userRepository.findById(Long.parseLong(value)).map(List::of).orElseGet(List::of)
                : userRepository.findByNameContainingIgnoreCase(value);
        return results.stream()
                .map(u -> new UserSearchResultDTO(u.getId(), u.getName(), u.getEmail(), u.getRoles(), u.getActive()))
                .collect(Collectors.toList());
    }



    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public UserDTO create(UserDTO requestDTO, CustomUserDetails principal) {
        logger.info("Creating a User: {}", requestDTO.getName());
        if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRoles(resolveRolesForCreation(requestDTO.getRoles(), principal));
        User savedUser = userRepository.save(user);
        UserDTO createdUserDTO = userMapper.toDTO(savedUser);
        addHateoasLinks(createdUserDTO);
        return createdUserDTO;
    }

    private Set<String> resolveRolesForCreation(Set<String> requestedRoles, CustomUserDetails principal) {
        if (principal.getRoles().contains("GOD_ADMIN")) {
            return requestedRoles;
        }
        return Set.of("FIELD_TECHNICIAN");
    }



    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public UserDTO update(UserDTO updatedDTO) {
        logger.info("Updating User with ID: {}", updatedDTO.getId());

        User existingUser = userRepository.findById(updatedDTO.getId())
                .orElseThrow(() -> new ObjectNotFoundException("User with ID: " + updatedDTO.getId()));
        userMapper.updateEntityFromDTO(updatedDTO, existingUser);
        UserDTO updatedUserDTO = userMapper.toDTO(userRepository.save(existingUser));
        addHateoasLinks(updatedUserDTO);
        return updatedUserDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public UserDTO disableUser(Long id, CustomUserDetails principal) {
        logger.info("Disabling User with ID: {}", id);
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User not found with ID: " + id));

        ensureCanDisable(principal, target);

        userRepository.disableUserById(id);
        var entity = userRepository.findById(id).get();
        var updatedUserDTO = userMapper.toDTO(entity);
        addHateoasLinks(updatedUserDTO);
        return updatedUserDTO;
    }

    @Transactional
    public void changeOwnPassword(Long id, ChangePasswordDTO dto) {
        logger.info("Changing password for User with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User", id));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }


    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN')")
    public void delete(Long id) {
        logger.info("Deleting one User!");
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }


    private void ensureCanDisable(CustomUserDetails principal, User target) {
        if (principal.getId().equals(target.getId())) {
            throw new UserRoleOperationNotAllowedException("You cannot disable your own account");
        }

        boolean targetIsPrivileged = target.getRoles().contains("GOD_ADMIN") || target.getRoles().contains("ADMIN");
        boolean actorIsGodAdmin = principal.getRoles().contains("GOD_ADMIN");

        if (targetIsPrivileged && !actorIsGodAdmin) {
            throw new UserRoleOperationNotAllowedException("Only a GOD_ADMIN can disable an ADMIN or GOD_ADMIN account");
        }
    }

    private void addHateoasLinks(UserDTO dto) {
        dto.add(linkTo(methodOn(UserController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(UserController.class).findAll()).withRel("findAllUsers").withType("GET"));
        dto.add(linkTo(methodOn(UserController.class).create(dto, null)).withRel("createUser").withType("POST"));
        dto.add(linkTo(methodOn(UserController.class).update(dto.getId(), dto)).withRel("updateUser").withType("PUT"));
        dto.add(linkTo(methodOn(UserController.class).disableUser(dto.getId(), null)).withRel("disableUser").withType("PATCH"));
        dto.add(linkTo(methodOn(UserController.class).delete(dto.getId())).withRel("deleteUser").withType("DELETE"));
    }
}