package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.controller.ServiceOrderController;
import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderAttendanceDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderStatusDescription;
import java.time.LocalDateTime;

import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ServiceOrderService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderService.class);

    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CEORepository ceoRepository;
    private final UserRepository userRepository;

    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository, ServiceOrderMapper serviceOrderMapper, CEORepository ceoRepository, UserRepository userRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.serviceOrderMapper = serviceOrderMapper;
        this.ceoRepository = ceoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public List<ServiceOrderDTO> findAll() {
        logger.info("Finding all Service Orders!");
        List<ServiceOrderDTO> serviceOrders = serviceOrderRepository.findAll().stream()
                .map(serviceOrderMapper::toDTO)
                .collect(Collectors.toList());
        serviceOrders.forEach(this::addHateoasLinks);
        return serviceOrders;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO findById(Long id) {
        logger.info("Finding Service Order with id {}", id);
        ServiceOrderDTO serviceOrderDTO = serviceOrderRepository.findById(id)
                .map(serviceOrderMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + id));
        addHateoasLinks(serviceOrderDTO);
        return serviceOrderDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO create(ServiceOrderDTO serviceOrderDTO) {
        logger.info("Creating one Service Order!");
        ServiceOrder entity = serviceOrderMapper.toEntity(serviceOrderDTO);
        Long ceoId = serviceOrderDTO.getCeo() == null ? null : serviceOrderDTO.getCeo().getId();
        Long userId = serviceOrderDTO.getUser() == null ? null : serviceOrderDTO.getUser().getId();
        if (ceoId == null || userId == null) throw new IllegalArgumentException("CEO and user are required");
        CEO ceo = ceoRepository.findById(ceoId).orElseThrow(() -> new EntityNotFoundException("CEO not found: " + ceoId));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        entity.setCeo(ceo);
        entity.setUser(user);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        ServiceOrderDTO createdServiceOrderDTO = serviceOrderMapper.toDTO(serviceOrderRepository.save(entity));
        addHateoasLinks(createdServiceOrderDTO);
        return createdServiceOrderDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO update(Long id, ServiceOrderDTO serviceOrderDTO) {
        logger.info("Updating Service Order with id {}", id);
        var entity = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + id));

        serviceOrderMapper.updateEntityFromDTO(serviceOrderDTO, entity);
        ServiceOrderDTO updatedServiceOrderDTO = serviceOrderMapper.toDTO(serviceOrderRepository.save(entity));
        addHateoasLinks(updatedServiceOrderDTO);
        return updatedServiceOrderDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO attend(Long id, ServiceOrderAttendanceDTO request) {
        var entity = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + id));
        if (request.getStatus() == null) throw new IllegalArgumentException("Status is required");
        if (request.getStatusDescription() == null || request.getStatusDescription().isBlank()) {
            throw new IllegalArgumentException("Status description is required");
        }
        entity.setStatus(request.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        ServiceOrderStatusDescription description = new ServiceOrderStatusDescription();
        description.setServiceOrder(entity);
        description.setStatusDescription(request.getStatusDescription().trim());
        description.setCreatedAt(LocalDateTime.now());
        entity.getServiceOrderStatusDescriptions().add(description);
        if (request.getGeoLocation() != null && !request.getGeoLocation().isBlank()
                && entity.getCeo() != null && entity.getCeo().getAddress() != null) {
            entity.getCeo().getAddress().setGeoLocation(request.getGeoLocation().trim());
        }
        ServiceOrderDTO result = serviceOrderMapper.toDTO(serviceOrderRepository.save(entity));
        addHateoasLinks(result);
        return result;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN')")
    public void delete(Long id) {
        logger.info("Deleting Service Order with id {}", id);
        if (!serviceOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Service order not found: " + id);
        }
        serviceOrderRepository.deleteById(id);
    }

    private void addHateoasLinks(ServiceOrderDTO dto) {
        dto.add(linkTo(methodOn(ServiceOrderController.class).findById(dto.getServiceOrderId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).update(dto.getServiceOrderId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).delete(dto.getServiceOrderId())).withRel("delete").withType("DELETE"));
    }
}