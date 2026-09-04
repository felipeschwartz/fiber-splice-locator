package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderPhotoMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderPhoto;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderPhotoRepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceOrderPhotoService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderPhotoService.class);

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final Path storageRoot;
    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderPhotoRepository photoRepository;
    private final ServiceOrderPhotoMapper photoMapper;

    public ServiceOrderPhotoService(
            @Value("${app.storage.service-order-photos}") String storagePath,
            ServiceOrderRepository serviceOrderRepository,
            ServiceOrderPhotoRepository photoRepository,
            ServiceOrderPhotoMapper photoMapper
    ) {
        this.storageRoot = Paths.get(storagePath).toAbsolutePath().normalize();
        this.serviceOrderRepository = serviceOrderRepository;
        this.photoRepository = photoRepository;
        this.photoMapper = photoMapper;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public List<ServiceOrderPhotoDTO> findAllByServiceOrder(Long serviceOrderId) {
        logger.info("Finding all photos for Service Order {}", serviceOrderId);
        if (!serviceOrderRepository.existsById(serviceOrderId)) {
            throw new EntityNotFoundException("Service order not found: " + serviceOrderId);
        }
        return photoRepository.findByServiceOrder_ServiceOrderId(serviceOrderId).stream()
                .map(entity -> {
                            ServiceOrderPhotoDTO dto = photoMapper.toDTO(entity);
                            attachContentUrl(dto);
                            return dto;
                        })
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderPhotoDTO findById(Long id) {
        logger.info("Finding photo with id {}", id);
        ServiceOrderPhotoDTO dto = photoRepository.findById(id)
                .map(photoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Service order photo not found: " + id));
        attachContentUrl(dto);
        return dto;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderPhotoDTO savePhoto(Long serviceOrderId, MultipartFile file) throws IOException {
        validateFile(file);

        ServiceOrder serviceOrder = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Service order not found: " + serviceOrderId));

        String extension = EXTENSION_BY_CONTENT_TYPE.getOrDefault(file.getContentType(), ".jpg");
        String storedFileName = UUID.randomUUID() + extension;

        Path orderDirectory = storageRoot.resolve(serviceOrderId.toString());
        Files.createDirectories(orderDirectory);

        Path targetFile = orderDirectory.resolve(storedFileName).normalize();

        if (!targetFile.startsWith(storageRoot)) {
            throw new SecurityException("Invalid file path");
        }

        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        int nextOrder = photoRepository.countByServiceOrder_ServiceOrderId(serviceOrderId) + 1;

        ServiceOrderPhoto photo = new ServiceOrderPhoto();
        photo.setServiceOrder(serviceOrder);
        photo.setStoragePath(serviceOrderId + "/" + storedFileName);
        photo.setStoredFilename(storedFileName);
        photo.setOriginalFilename(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setFileSize(file.getSize());
        photo.setPhotoOrder(nextOrder);
        photo.setCreatedAt(LocalDateTime.now());

        ServiceOrderPhoto saved = photoRepository.save(photo);
        logger.info("Photo {} stored for Service Order {}", saved.getServiceOrderPhotoId(), serviceOrderId);
        ServiceOrderPhotoDTO dto = photoMapper.toDTO(saved);
        attachContentUrl(dto);
        return dto;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public ServiceOrderPhotoDTO update(Long id, ServiceOrderPhotoDTO dto) {
        logger.info("Updating photo with id {}", id);
        ServiceOrderPhoto entity = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order photo not found: " + id));
        photoMapper.updateEntityFromDTO(dto, entity);
        ServiceOrderPhotoDTO saved = photoMapper.toDTO(photoRepository.save(entity));
        attachContentUrl(saved);
        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('GOD_ADMIN')")
    public void delete(Long id) {
        logger.info("Deleting photo with id {}", id);
        ServiceOrderPhoto photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order photo not found: " + id));

        Path fileToDelete = storageRoot.resolve(photo.getStoragePath()).normalize();
        try {
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            logger.warn("Could not delete physical file {}: {}", fileToDelete, e.getMessage());
        }

        photoRepository.delete(photo);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GOD_ADMIN') or hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public LoadedPhoto loadContent(Long id) {
        logger.info("Loading photo content for id {}", id);
        ServiceOrderPhoto photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service order photo not found: " + id));

        Path file = storageRoot.resolve(photo.getStoragePath()).normalize();
        if (!file.startsWith(storageRoot) || !Files.exists(file)) {
            throw new EntityNotFoundException("Photo file not found on disk: " + id);
        }

        return new LoadedPhoto(new FileSystemResource(file), photo.getContentType());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The uploaded file is empty");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported image type");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("The image size exceeds the allowed limit");
        }
    }

    public record LoadedPhoto(Resource resource, String contentType) {}

    private void attachContentUrl(ServiceOrderPhotoDTO dto) {
        String url = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/service_order_photos/v1/{id}/content")
                .buildAndExpand(dto.getId())
                .toUriString();
        dto.setContentUrl(url);
    }
}