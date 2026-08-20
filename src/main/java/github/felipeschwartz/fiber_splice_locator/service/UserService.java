package github.felipeschwartz.fiber_splice_locator.service;


import github.felipeschwartz.fiber_splice_locator.controller.UserController;
import github.felipeschwartz.fiber_splice_locator.mapper.UserMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.ObjectNotFoundException;
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



    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN')")
    public UserDTO create(UserDTO requestDTO) {
        logger.info("Creating a User: {}", requestDTO.getName());
        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO createdUserDTO = userMapper.toDTO(savedUser);
        addHateoasLinks(createdUserDTO);
        return createdUserDTO;
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
    public UserDTO disableUser(Long id) {
        logger.info("Disabling User with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException("User not found with ID: " + id);
        }
        userRepository.disableUserById(id);
        var entity = userRepository.findById(id).get();
        var updatedUserDTO = userMapper.toDTO(entity);
        addHateoasLinks(updatedUserDTO);
        return updatedUserDTO;
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



    private void addHateoasLinks(UserDTO dto) {
        dto.add(linkTo(methodOn(UserController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(UserController.class).findAll()).withRel("findAllUsers").withType("GET"));
        dto.add(linkTo(methodOn(UserController.class).create(null)).withRel("createUser").withType("POST"));
        dto.add(linkTo(methodOn(UserController.class).update(dto.getId(), dto)).withRel("updateUser").withType("PUT"));
        dto.add(linkTo(methodOn(UserController.class).disableUser(dto.getId())).withRel("disableUser").withType("PATCH"));
        dto.add(linkTo(methodOn(UserController.class).delete(dto.getId())).withRel("deleteUser").withType("DELETE"));
    }
}