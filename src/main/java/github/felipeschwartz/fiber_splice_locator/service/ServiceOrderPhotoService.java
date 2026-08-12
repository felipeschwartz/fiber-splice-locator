package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderPhoto;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderPhotoRepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceOrderPhotoService {

    private final Path storageRoot;
    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderPhotoRepository photoRepository;

    public ServiceOrderPhotoService(
            @Value("${app.storage.service-order-photos}") String storagePath,
            ServiceOrderRepository serviceOrderRepository,
            ServiceOrderPhotoRepository photoRepository
    ) {
        this.storageRoot = Paths.get(storagePath).toAbsolutePath().normalize();
        this.serviceOrderRepository = serviceOrderRepository;
        this.photoRepository = photoRepository;
    }

    @Transactional
    public ServiceOrderPhoto savePhoto(
            Long serviceOrderId,
            MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("The uploaded file is empty");
        }

        if (!List.of("image/jpeg", "image/png", "image/webp")
                .contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported image type");
        }

        long maxFileSize = 10 * 1024 * 1024;

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("The image size exceeds the allowed limit");
        }

        ServiceOrder serviceOrder = serviceOrderRepository
                .findById(serviceOrderId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Service order not found"));

        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + extension;

        Path orderDirectory = storageRoot.resolve(serviceOrderId.toString());
        Files.createDirectories(orderDirectory);

        Path targetFile = orderDirectory
                .resolve(storedFileName)
                .normalize();

        if (!targetFile.startsWith(storageRoot)) {
            throw new SecurityException("Invalid file path");
        }

        Files.copy(
                file.getInputStream(),
                targetFile,
                StandardCopyOption.REPLACE_EXISTING
        );

        ServiceOrderPhoto photo = new ServiceOrderPhoto();
        photo.setServiceOrder(serviceOrder);
        photo.setStoragePath(
                serviceOrderId + "/" + storedFileName
        );
        photo.setStoredFileName(storedFileName);
        photo.setOriginalFileName(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setFileSize(file.getSize());

        return photoRepository.save(photo);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }

        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}
