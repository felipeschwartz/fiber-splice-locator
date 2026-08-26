package github.felipeschwartz.fiber_splice_locator.model.dto;

import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ServiceOrderStatusDescriptionDTO extends RepresentationModel<ServiceOrderStatusDescriptionDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long serviceOrderId;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceOrderStatusDescriptionDTO() {
    }

    public ServiceOrderStatusDescriptionDTO(Long id, Long serviceOrderId, String statusDescription, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.serviceOrderId = serviceOrderId;
        this.statusDescription = statusDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServiceOrderStatusDescriptionDTO that = (ServiceOrderStatusDescriptionDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
