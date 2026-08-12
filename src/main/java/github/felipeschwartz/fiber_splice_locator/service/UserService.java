package github.felipeschwartz.fiber_splice_locator.service;

import com.github.felipeschwartz.parkingsystem.controller.UserController;
import com.github.felipeschwartz.parkingsystem.mapper.UserCreationMapper;
import com.github.felipeschwartz.parkingsystem.mapper.UserEntityMapper;
import com.github.felipeschwartz.parkingsystem.mapper.UserIndividualMapper;
import com.github.felipeschwartz.parkingsystem.model.dto.CreateUserRequestDTO;
import com.github.felipeschwartz.parkingsystem.model.dto.UserDTO;
import com.github.felipeschwartz.parkingsystem.model.dto.UserEntityDTO;
import com.github.felipeschwartz.parkingsystem.model.dto.UserIndividualDTO;
import com.github.felipeschwartz.parkingsystem.model.entity.User;
import com.github.felipeschwartz.parkingsystem.model.entity.UserEntity;
import com.github.felipeschwartz.parkingsystem.model.entity.UserIndividual;
import com.github.felipeschwartz.parkingsystem.model.entity.Vehicle;
import com.github.felipeschwartz.parkingsystem.model.enums.UserType;
import com.github.felipeschwartz.parkingsystem.repository.UserEntityRepository;
import com.github.felipeschwartz.parkingsystem.repository.UserIndividualRepository;
import com.github.felipeschwartz.parkingsystem.repository.UserRepository;
import com.github.felipeschwartz.parkingsystem.service.exceptions.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final UserIndividualRepository userIndividualRepository;
    private final UserEntityRepository userEntityRepository;
    private final UserEntityMapper entityMapper;
    private final UserIndividualMapper individualMapper;
    private final UserCreationMapper userCreationMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserIndividualRepository userIndividualRepository,
                       UserEntityRepository userEntityRepository, UserEntityMapper entityMapper,
                       UserIndividualMapper individualMapper, UserCreationMapper userCreationMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userIndividualRepository = userIndividualRepository;
        this.userEntityRepository = userEntityRepository;
        this.entityMapper = entityMapper;
        this.individualMapper = individualMapper;
        this.userCreationMapper = userCreationMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARKING_MANAGER')")
    public List<UserDTO> findAll() {
        logger.info("Finding all Users!");
        List<UserDTO> userDTOS = userRepository.findAll()
                .stream()
                .map(user -> {
                    if (user instanceof UserIndividual oi) {
                        return (UserDTO) individualMapper.toDTO(oi);
                    } else if (user instanceof UserEntity oe) {
                        return (UserDTO) entityMapper.toDTO(oe);
                    }
                    throw new ObjectNotFoundException("Unknown user type: " + user.getId());
                }).collect(Collectors.toList());
        userDTOS.forEach(this::addHateoasLinks);
        return userDTOS;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARKING_MANAGER') or #id == authentication.principal.id")
    public UserDTO findById(Long id) {
        logger.info("Finding one User by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User", id));
        UserDTO userDTO;
        if (user instanceof UserIndividual oi) {
            userDTO = individualMapper.toDTO(oi);
        } else if (user instanceof UserEntity oe) {
            userDTO = entityMapper.toDTO(oe);
        } else {
            throw new IllegalStateException("Unknown user type: " + user.getClass().getName());
        }
        addHateoasLinks(userDTO);
        return userDTO;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARKING_MANAGER') or #cpf == authentication.principal.cpf")
    public UserIndividualDTO findByCpf(String cpf) {
        logger.info("Finding one User by CPF: {}", cpf);
        UserIndividual userIndividual = userIndividualRepository.findByCpf(cpf)
                .orElseThrow(() -> new ObjectNotFoundException("User with CPF", cpf));
        UserIndividualDTO userDTO = individualMapper.toDTO(userIndividual);
        addHateoasLinks(userDTO);
        return userDTO;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARKING_MANAGER') or #cnpj == authentication.principal.cnpj")
    public UserEntityDTO findByCnpj(String cnpj) {
        logger.info("Finding one User by CNPJ: {}", cnpj);
        UserEntity userEntity = userEntityRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new ObjectNotFoundException("User with CNPJ", cnpj));
        UserEntityDTO userDTO = entityMapper.toDTO(userEntity);
        addHateoasLinks(userDTO);
        return userDTO;
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO create(CreateUserRequestDTO requestDTO) {
        logger.info("Creating a User of type: {}", requestDTO.getUserType());
        Objects.requireNonNull(requestDTO, "User creation request must not be null");
        User user;
        if (requestDTO.getUserType() == UserType.INDIVIDUAL) {
            if (requestDTO.getCpf() == null || requestDTO.getCpf().isBlank()) {
                throw new ObjectNotFoundException("CPF must not be blank for individual users.");
            }
            if (userIndividualRepository.existsByCpf(requestDTO.getCpf())) {
                throw new ObjectNotFoundException("User with CPF already exists.");
            }
            user = userCreationMapper.toIndividualEntity(requestDTO);
        } else if (requestDTO.getUserType() == UserType.ENTITY) {
            if (requestDTO.getCnpj() == null || requestDTO.getCnpj().isBlank()) {
                throw new ObjectNotFoundException("CNPJ must not be blank for individual users.");
            }
            if (userEntityRepository.existsByCnpj(requestDTO.getCnpj())) {
                throw new ObjectNotFoundException("User with CNPJ already exists.");
            }
            user = userCreationMapper.toEntityEntity(requestDTO);
        } else  {
            throw new IllegalArgumentException("Unsupported UserType for creation: " + requestDTO.getUserType());
        }
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        if (requestDTO.getVehicles() != null && !requestDTO.getVehicles().isEmpty()) {
            Set<Vehicle> vehicles = requestDTO.getVehicles().stream()
                    .map(vehicleDTO -> {
                        Vehicle vehicle = new Vehicle();
                        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
                        vehicle.setType(vehicleDTO.getType());
                        vehicle.setUser(user);
                        return vehicle;
                    })
                    .collect(Collectors.toSet());
            user.setVehicles(vehicles);
        }
        User savedUser = userRepository.save(user);
        UserDTO createdUserDTO;
        if (savedUser instanceof UserIndividual oi) {
            createdUserDTO = individualMapper.toDTO(oi);
        } else if (savedUser instanceof UserEntity oe) {
            createdUserDTO = entityMapper.toDTO(oe);
        } else {
            throw new IllegalStateException("Saved user is of an unknown type.");
        }
        addHateoasLinks(createdUserDTO);
        return createdUserDTO;
    }



    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public UserDTO update(Long id, CreateUserRequestDTO updatedDTO) {
        logger.info("Updating User with ID: {}", id);
        Objects.requireNonNull(updatedDTO, "User update request must not be null.");

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with ID: " + id));

        if (existingUser.getUserType() != updatedDTO.getUserType()) {
            throw new IllegalArgumentException("Cannot change user type during update. Existing user is "
                    + existingUser.getUserType() + ", but provided " + updatedDTO.getUserType());
        }
        if (existingUser instanceof UserIndividual individual) {
            if (updatedDTO.getUserType() != UserType.INDIVIDUAL) {
                throw new IllegalArgumentException("Mismatched user type for update. Expected INDIVIDUAL, got "
                        + updatedDTO.getUserType());
            }
            if (updatedDTO.getCpf() != null && !updatedDTO.getCpf().isBlank()
                    && !updatedDTO.getCpf().equals(individual.getCpf())) {
                if (userIndividualRepository.existsByCpf(updatedDTO.getCpf())) {
                    throw new IllegalArgumentException("An UserIndividual with this CPF already exists.");
                }
            }
            individualMapper.updateIndividualFromCreateRequest(updatedDTO, individual);
            individual.setUpdatedAt(LocalDateTime.now());
            UserIndividual updatedIndividual = userIndividualRepository.save(individual);
            UserIndividualDTO resultDTO = individualMapper.toDTO(updatedIndividual);
            addHateoasLinks(resultDTO);
            return resultDTO;


        }  else if (existingUser instanceof UserEntity entity) {
            if  (updatedDTO.getUserType() != UserType.ENTITY) {
                throw new IllegalArgumentException("Mismatched user type for update. Expected ENTITY, got "
                        + updatedDTO.getUserType());
            }
            if (updatedDTO.getCnpj() != null && !updatedDTO.getCnpj().isBlank()
                    && !updatedDTO.getCnpj().equals(entity.getCnpj())) {
                if (userEntityRepository.existsByCnpj(updatedDTO.getCnpj())) {
                    throw new IllegalArgumentException("An User with this CNPJ already exists.");
                }
            }
            entityMapper.updateEntityFromCreateRequest(updatedDTO, entity);
            entity.setUpdatedAt(LocalDateTime.now());
            UserEntity updatedEntity = userRepository.save(entity);
            UserEntityDTO resultDTO = entityMapper.toDTO(updatedEntity);
            addHateoasLinks(resultDTO);
            return resultDTO;
        } else {
            throw new IllegalStateException("Existing user is of an unknown type.");
        }
    }



    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
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
        dto.add(linkTo(methodOn(UserController.class).update(dto.getId(), null)).withRel("updateUser").withType("PUT"));
        dto.add(linkTo(methodOn(UserController.class).delete(dto.getId())).withRel("deleteUser").withType("DELETE"));
        if (dto instanceof UserIndividualDTO individualDTO) {
            if (individualDTO.getCpf() != null) {
                dto.add(linkTo(methodOn(UserController.class).findByCpf(individualDTO.getCpf())).withRel("findByCpf").withType("GET"));
            }
        } else if (dto instanceof UserEntityDTO entityDTO) {
            if (entityDTO.getCnpj() != null) {
                dto.add(linkTo(methodOn(UserController.class).findByCnpj(entityDTO.getCnpj())).withRel("findByCnpj").withType("GET"));
            }
        }
    }
}