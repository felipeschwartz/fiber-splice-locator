package github.felipeschwartz.fiber_splice_locator.model.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "service_order_photos")
public class ServiceOrderPhoto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceOrderPhotoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_order_photo")
    )
    private ServiceOrder serviceOrder;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "photo_order", nullable = false)
    private Integer photoOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ServiceOrderPhoto() {
    }

    public ServiceOrderPhoto(Long serviceOrderPhotoId, ServiceOrder serviceOrder, String storagePath, String originalFilename, String storedFilename, String contentType, Long fileSize, Integer photoOrder, LocalDateTime createdAt) {
        this.serviceOrderPhotoId = serviceOrderPhotoId;
        this.serviceOrder = serviceOrder;
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

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
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
        ServiceOrderPhoto that = (ServiceOrderPhoto) o;
        return Objects.equals(getServiceOrderPhotoId(), that.getServiceOrderPhotoId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOrderPhotoId());
    }
}