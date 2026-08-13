package github.felipeschwartz.fiber_splice_locator.model.dto;

import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ServiceOrderPhotoDTO extends RepresentationModel<ServiceOrderPhotoDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long serviceOrderPhotoId;
    private Long serviceOrderId;
    private String storagePath;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private Long fileSize;
    private Integer photoOrder;
    private LocalDateTime createdAt;

    public ServiceOrderPhotoDTO() {
    }

    public ServiceOrderPhotoDTO(Long serviceOrderPhotoId, Long serviceOrderId, String storagePath, String originalFilename, String storedFilename, String contentType, Long fileSize, Integer photoOrder, LocalDateTime createdAt) {
        this.serviceOrderPhotoId = serviceOrderPhotoId;
        this.serviceOrderId = serviceOrderId;
        this.storagePath = storagePath;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.photoOrder = photoOrder;
        this.createdAt = createdAt;
    }

    public Long getServiceOrderPhotoId() {
        return serviceOrderPhotoId;
    }

    public void setServiceOrderPhotoId(Long serviceOrderPhotoId) {
        this.serviceOrderPhotoId = serviceOrderPhotoId;
    }

    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
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
        return Objects.equals(getServiceOrderPhotoId(), that.getServiceOrderPhotoId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOrderPhotoId());
    }
}