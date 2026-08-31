package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.controller.ServiceOrderStatusDescriptionController;
import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderStatusDescriptionMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderStatusDescriptionRepository;
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
public class ServiceOrderStatusDescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderStatusDescriptionService.class);

    private final ServiceOrderStatusDescriptionRepository service;
    private final ServiceOrderStatusDescriptionMapper mapper;

    public ServiceOrderStatusDescriptionService(ServiceOrderStatusDescriptionRepository service, ServiceOrderStatusDescriptionMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public List<ServiceOrderStatusDescriptionDTO> findAll() {
        logger.info("Finding all Service Orders Status Descriptions!");
        List<ServiceOrderStatusDescriptionDTO> serviceOrders = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        serviceOrders.forEach(this::addHateoasLinks);
        return serviceOrders;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public List<ServiceOrderStatusDescriptionDTO> findByServiceOrderId(Long serviceOrderId) {
        logger.info("Finding Service Order Status Descriptions for service order {}", serviceOrderId);
        List<ServiceOrderStatusDescriptionDTO> list = service.findByServiceOrder_ServiceOrderIdOrderByCreatedAtAsc(serviceOrderId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        list.forEach(this::addHateoasLinks);
        return list;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderStatusDescriptionDTO findById(Long id) {
        logger.info("Finding Service Order Status Description with id {}", id);
        ServiceOrderStatusDescriptionDTO serviceOrderDTO = service.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Service order status description not found: " + id));
        addHateoasLinks(serviceOrderDTO);
        return serviceOrderDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderStatusDescriptionDTO create(ServiceOrderStatusDescriptionDTO serviceOrderDTO) {
        logger.info("Creating one Service Order Status Description!");
        ServiceOrderStatusDescriptionDTO createdServiceOrderDTO = mapper.toDTO(
                service.save(mapper.toEntity(serviceOrderDTO))
        );
        addHateoasLinks(createdServiceOrderDTO);
        return createdServiceOrderDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderStatusDescriptionDTO update(Long id, ServiceOrderStatusDescriptionDTO serviceOrderDTO) {
        logger.info("Updating Service Order Status Description with id {}", id);
        var entity = service.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order status description not found: " + id));

        mapper.updateEntityFromDTO(serviceOrderDTO, entity);
        ServiceOrderStatusDescriptionDTO updatedServiceOrderDTO = mapper.toDTO(service.save(entity));
        addHateoasLinks(updatedServiceOrderDTO);
        return updatedServiceOrderDTO;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN')")
    public void delete(Long id) {
        logger.info("Deleting Service Order Status Description with id {}", id);
        if (!service.existsById(id)) {
            throw new EntityNotFoundException("Service order status description not found: " + id);
        }
        service.deleteById(id);
    }

    private void addHateoasLinks(ServiceOrderStatusDescriptionDTO dto) {
        dto.add(linkTo(methodOn(ServiceOrderStatusDescriptionController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ServiceOrderStatusDescriptionController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ServiceOrderStatusDescriptionController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ServiceOrderStatusDescriptionController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ServiceOrderStatusDescriptionController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}