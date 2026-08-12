package github.felipeschwartz.fiber_splice_locator.model.dto;

import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ServiceOrderPhotoDTO extends RepresentationModel<ServiceOrderPhotoDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private ServiceOrderDTO serviceOrderDTO;
    private String storagePath;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private Long fileSize;
    private Integer photoOrder;
    private LocalDateTime createdAt;

    public ServiceOrderPhotoDTO() {
    }

    public ServiceOrderPhotoDTO(Long id, ServiceOrderDTO serviceOrderDTO, String storagePath, String originalFilename, String storedFilename, String contentType, Long fileSize, Integer photoOrder, LocalDateTime createdAt) {
        this.id = id;
        this.serviceOrderDTO = serviceOrderDTO;
        this.storagePath = storagePath;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.photoOrder = photoOrder;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceOrderDTO getServiceOrder() {
        return serviceOrderDTO;
    }

    public void setServiceOrder(ServiceOrderDTO serviceOrderDTO) {
        this.serviceOrderDTO = serviceOrderDTO;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getPhotoOrder() {
        return photoOrder;
    }

    public void setPhotoOrder(Integer photoOrder) {
        this.photoOrder = photoOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServiceOrderPhotoDTO that = (ServiceOrderPhotoDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
