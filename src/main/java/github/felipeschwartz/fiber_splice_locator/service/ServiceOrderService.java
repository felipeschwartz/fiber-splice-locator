package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.controller.ServiceOrderController;
import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderAttendanceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderStatusDescription;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository,
                               ServiceOrderMapper serviceOrderMapper,
                               CEORepository ceoRepository,
                               UserRepository userRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.serviceOrderMapper = serviceOrderMapper;
        this.ceoRepository = ceoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public List<ServiceOrderDTO> findAll() {
        List<ServiceOrderDTO> serviceOrders = serviceOrderRepository.findAll().stream()
                .map(serviceOrderMapper::toDTO)
                .collect(Collectors.toList());
        serviceOrders.forEach(this::addHateoasLinks);
        return serviceOrders;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO findById(Long id) {
        ServiceOrderDTO dto = serviceOrderRepository.findById(id)
                .map(serviceOrderMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + id));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO create(ServiceOrderDTO request) {
        logger.info("Creating one Service Order!");
        CEO ceo = findCeo(request);
        User user = findUser(request);
        ServiceOrder entity = buildOpenServiceOrder(ceo, user);
        addInitialDescription(request, entity);
        return saveAndMap(entity);
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO open(ServiceOrderDTO request) {
        logger.info("Opening a Service Order and changing CEO status");
        if (request == null || request.getCeo() == null || request.getCeo().getId() == null) {
            throw new IllegalArgumentException("CEO is required");
        }
        if (request.getCeoStatus() == null) {
            throw new IllegalArgumentException("CEO status is required");
        }

        CEO ceo = findCeo(request);
        ceo.changeStatus(request.getCeoStatus());
        User user = findUser(request);
        ServiceOrder entity = buildOpenServiceOrder(ceo, user);
        addInitialDescriptionRequired(request, entity);
        ceoRepository.save(ceo);
        return saveAndMap(entity);
    }

    private CEO findCeo(ServiceOrderDTO request) {
        if (request == null || request.getCeo() == null || request.getCeo().getId() == null) {
            throw new IllegalArgumentException("CEO is required");
        }
        Long id = request.getCeo().getId();
        return ceoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CEO not found: " + id));
    }

    private User findUser(ServiceOrderDTO request) {
        if (request == null || request.getUser() == null || request.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required");
        }
        Long id = request.getUser().getId();
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private ServiceOrder buildOpenServiceOrder(CEO ceo, User user) {
        LocalDateTime now = LocalDateTime.now();
        ServiceOrder entity = new ServiceOrder();
        entity.setCeo(ceo);
        entity.setUser(user);
        entity.setStatus(ServiceOrderStatus.OPEN);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    private void addInitialDescription(ServiceOrderDTO request, ServiceOrder entity) {
        if (request != null && request.getServiceOrderStatusDescriptions() != null
                && !request.getServiceOrderStatusDescriptions().isEmpty()) {
            ServiceOrderStatusDescriptionDTO dto = request.getServiceOrderStatusDescriptions().iterator().next();
            if (dto != null && dto.getStatusDescription() != null
                    && !dto.getStatusDescription().trim().isEmpty()) {
                addDescription(dto.getStatusDescription(), entity);
            }
        }
    }

    private void addInitialDescriptionRequired(ServiceOrderDTO request, ServiceOrder entity) {
        if (request.getServiceOrderStatusDescriptions() == null
                || request.getServiceOrderStatusDescriptions().isEmpty()) {
            throw new IllegalArgumentException("Initial status description is required");
        }
        ServiceOrderStatusDescriptionDTO dto = request.getServiceOrderStatusDescriptions().iterator().next();
        if (dto == null || dto.getStatusDescription() == null || dto.getStatusDescription().isBlank()) {
            throw new IllegalArgumentException("Initial status description is required");
        }
        addDescription(dto.getStatusDescription(), entity);
    }

    private void addDescription(String text, ServiceOrder entity) {
        ServiceOrderStatusDescription description = new ServiceOrderStatusDescription();
        description.setServiceOrder(entity);
        description.setStatusDescription(text.trim());
        description.setCreatedAt(LocalDateTime.now());
        entity.getServiceOrderStatusDescriptions().add(description);
    }

    private ServiceOrderDTO saveAndMap(ServiceOrder entity) {
        ServiceOrderDTO result = serviceOrderMapper.toDTO(serviceOrderRepository.save(entity));
        addHateoasLinks(result);
        return result;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO update(Long id, ServiceOrderDTO dto) {
        ServiceOrder entity = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + id));
        serviceOrderMapper.updateEntityFromDTO(dto, entity);
        ServiceOrderDTO result = serviceOrderMapper.toDTO(serviceOrderRepository.save(entity));
        addHateoasLinks(result);
        return result;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderDTO attend(Long id, ServiceOrderAttendanceDTO request) {
        ServiceOrder entity = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + id));
        if (request.getStatus() == null) throw new IllegalArgumentException("Status is required");
        if (request.getStatusDescription() == null || request.getStatusDescription().isBlank()) {
            throw new IllegalArgumentException("Status description is required");
        }
        entity.setStatus(request.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        addDescription(request.getStatusDescription(), entity);
        if (request.getGeoLocation() != null && !request.getGeoLocation().isBlank()
                && entity.getCeo() != null && entity.getCeo().getAddress() != null) {
            entity.getCeo().getAddress().setGeoLocation(request.getGeoLocation().trim());
        }
        return saveAndMap(entity);
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN')")
    public void delete(Long id) {
        if (!serviceOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Service order not found: " + id);
        }
        serviceOrderRepository.deleteById(id);
    }

    private void addHateoasLinks(ServiceOrderDTO dto) {
        dto.add(linkTo(methodOn(ServiceOrderController.class).findById(dto.getServiceOrderId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).open(dto)).withRel("open").withType("POST"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).update(dto.getServiceOrderId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ServiceOrderController.class).delete(dto.getServiceOrderId())).withRel("delete").withType("DELETE"));
    }
}
